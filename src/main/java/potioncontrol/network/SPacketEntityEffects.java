package potioncontrol.network;

import com.google.common.collect.HashMultimap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;
import potioncontrol.mixin.accessor.SPacketEntityEffectAccessor;
import potioncontrol.mixin.accessor.SPacketRemoveEntityEffectAccessor;
import potioncontrol.util.wrapper.IHasActualAmplifier;
import potioncontrol.util.wrapper.IStoresPotionEffect;

public class SPacketEntityEffects implements IMessage {
    private final HashMultimap<Integer, SPacketEntityEffect> addPackets = HashMultimap.create();
    private final HashMultimap<Integer, SPacketRemoveEntityEffect> removePackets = HashMultimap.create();

    public SPacketEntityEffects() {} // Prob not needed

    // returns false if it should flush
    public boolean addAddPacket(SPacketEntityEffect packet) { //This should only run on the serverside
        int entityId = ((SPacketEntityEffectAccessor)packet).pc_getEntityId();
        int newPotId = ((SPacketEntityEffectAccessor)packet).pc_getEffectId() & 0xFF;
        Potion newPot = Potion.getPotionById(newPotId);
        if(newPot == null) {
            PotionControl.LOGGER.warn("Received addPotionEffect packet request with an invalid potion id {}, skipping", newPotId);
            return true;
        }

        // is there already a packet with the same potion effect for that mob? use "better" by combining new into old
        boolean didExist = false;
        PotionEffect newEff = ((IStoresPotionEffect) packet).pc$getPotionEffect();
        for (SPacketEntityEffect pack : this.addPackets.get(entityId)) {
            if((((SPacketEntityEffectAccessor)pack).pc_getEffectId() & 0xFF) == newPotId) {
                ((IStoresPotionEffect) pack).pc$getPotionEffect().combine(newEff); //better one wins and stays
                didExist = true;
                PacketHandler.caughtCounter++;
                break;
            }
        }
        if(!didExist) this.addPackets.put(entityId, packet);

        // is there a remove packet with the same potion for that mob? remove that since the potion got re-added
        SPacketRemoveEntityEffect toRemove = null;
        for (SPacketRemoveEntityEffect pack : this.removePackets.get(entityId)) {
            if (((SPacketRemoveEntityEffectAccessor)pack).pc_getPotion() == newPot){
                toRemove = pack;
                PacketHandler.caughtCounter++;
                break;
            }
        }
        if(toRemove != null) this.removePackets.remove(entityId, toRemove);

        return getPacketCount() < PacketHandler.MAX_PACKETS_PER_PLAYER;
    }

    // returns false if it should flush
    public boolean addRemovePacket(SPacketRemoveEntityEffect packet) { //This should only run on the serverside
        SPacketRemoveEntityEffectAccessor acc = (SPacketRemoveEntityEffectAccessor) packet;
        int entityId = acc.pc_getEntityId();
        Potion newPot = acc.pc_getPotion();
        int newPotId = Potion.getIdFromPotion(newPot);
        if(newPotId == -1) {
            PotionControl.LOGGER.warn("Unable to find id for potion of class {}, added in removePotionEffect packet, skipping", newPot.getClass());
            return true;
        }

        // is there already a remove packet with the same potion for that mob? ignore
        boolean didExist = false;
        for (SPacketRemoveEntityEffect pack : this.removePackets.get(entityId)) {
            if (((SPacketRemoveEntityEffectAccessor)pack).pc_getPotion() == newPot) {
                didExist = true;
                PacketHandler.caughtCounter++;
                break;
            }
        }
        if(!didExist) this.removePackets.put(entityId, packet);

        // is there already an add packet with the same potion for that mob? remove
        SPacketEntityEffect toRemove = null;
        for (SPacketEntityEffect pack : this.addPackets.get(entityId)) {
            if((((SPacketEntityEffectAccessor)pack).pc_getEffectId() & 0xFF) == newPotId) {
                toRemove = pack;
                PacketHandler.caughtCounter++;
                break;
            }
        }
        if(toRemove != null) this.addPackets.remove(entityId, toRemove);

        return getPacketCount() < PacketHandler.MAX_PACKETS_PER_PLAYER;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.addPackets.size());
        this.addPackets.forEach((entityId, packet) -> {
            SPacketEntityEffectAccessor acc = (SPacketEntityEffectAccessor) packet;
            buf.writeInt(acc.pc_getEntityId());
            buf.writeByte(acc.pc_getEffectId());
            buf.writeByte(acc.pc_getAmplifier());
            buf.writeInt(acc.pc_getDuration());
            buf.writeByte(acc.pc_getFlags());

            if(ConfigHandler.mixinToggles.fixAmplifiers)
                buf.writeShort(((IHasActualAmplifier) packet).pc_getActualAmplifier());
        });

        buf.writeInt(this.removePackets.size());
        this.removePackets.forEach((entityId, packet) -> {
            SPacketRemoveEntityEffectAccessor acc = (SPacketRemoveEntityEffectAccessor) packet;
            buf.writeInt(acc.pc_getEntityId());
            buf.writeByte(Potion.getIdFromPotion(acc.pc_getPotion()));
        });
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int addCount = buf.readInt();
        for (int i = 0; i < addCount; i++) {
            int entityId = buf.readInt();
            int effectId = buf.readByte();
            int amplifier = buf.readByte();
            int duration = buf.readInt();
            byte flags = buf.readByte();

            if(ConfigHandler.mixinToggles.fixAmplifiers)
                amplifier = buf.readShort();

            Potion potion = Potion.getPotionById(effectId & 0xFF); //TODO: this is probably different in JEID and its successors

            if (potion != null) {
                PotionEffect effect = new PotionEffect(potion, duration, amplifier, (flags & 1) == 1, (flags & 2) == 2);
                effect.setPotionDurationMax(duration == 32767);

                SPacketEntityEffect packet = new SPacketEntityEffect(entityId, effect);

                this.addPackets.put(entityId, packet);
            }
        }

        int removeCount = buf.readInt();
        for (int i = 0; i < removeCount; i++) {
            int entityId = buf.readInt();
            int effectId = buf.readByte();

            Potion potion = Potion.getPotionById(effectId & 0xFF); //TODO: same

            if (potion != null) {
                SPacketRemoveEntityEffect packet = new SPacketRemoveEntityEffect(entityId, potion);
                this.removePackets.put(entityId, packet);
            }
        }
    }

    public int getPacketCount() {
        return this.removePackets.size() + this.addPackets.size();
    }

    public static class ClientHandler implements IMessageHandler<SPacketEntityEffects, IMessage> {

        @Override
        public IMessage onMessage(SPacketEntityEffects message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                message.removePackets.values().forEach(packet -> ctx.getClientHandler().handleRemoveEntityEffect(packet));
                message.addPackets.values().forEach(packet -> ctx.getClientHandler().handleEntityEffect(packet));
            });
            return null;
        }
    }
}

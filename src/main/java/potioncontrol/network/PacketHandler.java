package potioncontrol.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;

import java.util.HashMap;
import java.util.Map;

public class PacketHandler {

    public static SimpleNetworkWrapper instance = null;

    public static final int MAX_PACKETS_PER_PLAYER = 100; //will send+flush the current queue if there is more than this many packets to send to one player
    public static final int UPDATE_FREQUENCY = 20; //once every this many ticks it will auto send+flush

    public static int caughtCounter = 0;

    public static void preInit() {
        instance = NetworkRegistry.INSTANCE.newSimpleChannel(PotionControl.MODID);

        instance.registerMessage(new SPacketEntityEffects.ClientHandler(), SPacketEntityEffects.class, 1, Side.CLIENT);
        MinecraftForge.EVENT_BUS.register(PacketHandler.class);
    }

    public static final Map<EntityPlayer, SPacketEntityEffects> collectionPackets = new HashMap<>();

    public static void addAddPacketForPlayer(EntityPlayer player, SPacketEntityEffect packet) {
        if(!collectionPackets.computeIfAbsent(player, p -> new SPacketEntityEffects()).addAddPacket(packet))
            sendAll();
    }

    public static void addRemovePacketForPlayer(EntityPlayer player, SPacketRemoveEntityEffect packet) {
        if(!collectionPackets.computeIfAbsent(player, p -> new SPacketEntityEffects()).addRemovePacket(packet))
            sendAll();
    }

    public static void sendAll() {
        if(ConfigHandler.debug.syncPotionsDebugMode) {
            int packetsSent = collectionPackets.values().stream()
                    .mapToInt(SPacketEntityEffects::getPacketCount)
                    .sum();
            PotionControl.LOGGER.info("Sending {} packets, avoided {}", packetsSent, caughtCounter);
        }
        caughtCounter = 0;

        collectionPackets.forEach((player, packet) ->
                PacketHandler.instance.sendTo(packet, (EntityPlayerMP) player)
        );
        collectionPackets.clear();
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side != Side.SERVER) return;
        if (event.world.provider.getDimension() != 0) return;
        if (event.world.getTotalWorldTime() % UPDATE_FREQUENCY == 0)
            //Read and flush at least once per second
            sendAll();
    }
}

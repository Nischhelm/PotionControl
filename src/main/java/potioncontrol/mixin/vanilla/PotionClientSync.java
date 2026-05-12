package potioncontrol.mixin.vanilla;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.mixin.accessor.EntityTrackerAccessor;

@Mixin(EntityLivingBase.class)
public abstract class PotionClientSync extends Entity {
    public PotionClientSync(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onChangedPotionEffect", at = @At("TAIL"))
    private void pc_sendPackets_onChange(PotionEffect effect, boolean p_70695_2_, CallbackInfo ci){
        if(this.world.isRemote) return;
        if((Object) this instanceof EntityPlayer) return;

        ((EntityTrackerAccessor)((WorldServer) this.world).getEntityTracker()).getEntries().forEach(e ->
                e.trackingPlayers.forEach(p ->
                    p.connection.sendPacket(new SPacketEntityEffect(this.getEntityId(), effect))));
    }

    @Inject(method = "onNewPotionEffect", at = @At("TAIL"))
    private void pc_sendPackets_onNew(PotionEffect effect, CallbackInfo ci){
        if(this.world.isRemote) return;
        if((Object) this instanceof EntityPlayer) return;
        ((EntityTrackerAccessor)((WorldServer) this.world).getEntityTracker()).getEntries().forEach(e ->
                e.trackingPlayers.forEach(p ->
                        p.connection.sendPacket(new SPacketEntityEffect(this.getEntityId(), effect))));
    }

    @Inject(method = "onFinishedPotionEffect", at = @At("TAIL"))
    private void pc_sendPackets_onFinish(PotionEffect effect, CallbackInfo ci){
        if(this.world.isRemote) return;
        if((Object) this instanceof EntityPlayer) return;
        ((EntityTrackerAccessor)((WorldServer) this.world).getEntityTracker()).getEntries().forEach(e ->
                e.trackingPlayers.forEach(p ->
                        p.connection.sendPacket(new SPacketRemoveEntityEffect(this.getEntityId(), effect.getPotion()))));
    }
}

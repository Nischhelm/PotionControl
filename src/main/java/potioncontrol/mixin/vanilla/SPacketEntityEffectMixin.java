package potioncontrol.mixin.vanilla;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.util.wrapper.IHasActualAmplifier;

@Mixin(SPacketEntityEffect.class)
public abstract class SPacketEntityEffectMixin implements IHasActualAmplifier {
    @Unique private int pc_actualAmplifier;

    @Inject(
            method = "<init>(ILnet/minecraft/potion/PotionEffect;)V",
            at = @At("TAIL")
    )
    private void pc_storeActualAmplifier(int entityIdIn, PotionEffect effect, CallbackInfo ci) {
        this.pc_actualAmplifier = MathHelper.clamp(effect.getAmplifier(), Short.MIN_VALUE, Short.MAX_VALUE);
    }

    @Inject(
            method = "writePacketData",
            at = @At("TAIL")
    )
    private void pc_writeActualAmplifier(PacketBuffer buf, CallbackInfo ci){
        buf.writeShort(this.pc_actualAmplifier);
    }

    @Inject(
            method = "readPacketData",
            at = @At("TAIL")
    )
    private void pc_readActualAmplifier(PacketBuffer buf, CallbackInfo ci){
        this.pc_actualAmplifier = buf.readShort();
    }

    @Override
    public int pc_getActualAmplifier() {
        return this.pc_actualAmplifier;
    }
}

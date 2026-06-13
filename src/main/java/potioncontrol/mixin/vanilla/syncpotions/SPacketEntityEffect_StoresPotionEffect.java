package potioncontrol.mixin.vanilla.syncpotions;

import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.util.wrapper.IStoresPotionEffect;

@Mixin(SPacketEntityEffect.class)
public abstract class SPacketEntityEffect_StoresPotionEffect implements IStoresPotionEffect {

    @Unique
    private PotionEffect pc$effect;

    @Override
    public PotionEffect pc$getPotionEffect() {
        return pc$effect;
    }

    @Override
    public void pc$setPotionEffect(PotionEffect potionEffect) {
        this.pc$effect = potionEffect;
    }

    @Inject(method = "<init>(ILnet/minecraft/potion/PotionEffect;)V", at = @At("TAIL"))
    private void pc_storeEffect(int entityId, PotionEffect effect, CallbackInfo ci) {
        this.pc$effect = effect;
    }
}

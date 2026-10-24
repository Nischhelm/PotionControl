package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import potioncontrol.config.ConfigHandler;
import potioncontrol.config.folders.VanillaPotionConfig;
import potioncontrol.util.VanillaPotionValues;

@Mixin(Potion.class)
public abstract class PotionMixin_InstantHealth {
    //heal ordinal 0 is regeneration
    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;heal(F)V", ordinal = 1)
    )
    private float potioncontrol_modifyInstantHeal(float original, @Local(argsOnly = true) int amplifier) {
        VanillaPotionConfig.InstantHealthConfig cfg = ConfigHandler.vanillaPotions.instantHealth;
        return VanillaPotionValues.getInstantAmount(cfg.healAmount, cfg.exponential, amplifier);
    }

    @ModifyArg(
            method = "affectEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;heal(F)V")
    )
    private float potioncontrol_modifySplashInstantHeal(float original, @Local(argsOnly = true) int amplifier, @Local(argsOnly = true) double health) {
        VanillaPotionConfig.InstantHealthConfig cfg = ConfigHandler.vanillaPotions.instantHealth;
        return VanillaPotionValues.getSplashAmount(VanillaPotionValues.getInstantAmount(cfg.healAmount, cfg.exponential, amplifier), health);
    }
}

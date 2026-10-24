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
public abstract class PotionMixin_InstantDamage {
    //attackEntityFrom ordinal 0 is poison, 1 is wither
    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 2),
            index = 1
    )
    private float potioncontrol_modifyInstantDamage(float original, @Local(argsOnly = true) int amplifier) {
        VanillaPotionConfig.InstantDamageConfig cfg = ConfigHandler.vanillaPotions.instantDamage;
        return VanillaPotionValues.getInstantAmount(cfg.damageAmount, cfg.exponential, amplifier);
    }

    //both calls, with and without source entity
    @ModifyArg(
            method = "affectEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"),
            index = 1
    )
    private float potioncontrol_modifySplashInstantDamage(float original, @Local(argsOnly = true) int amplifier, @Local(argsOnly = true) double health) {
        VanillaPotionConfig.InstantDamageConfig cfg = ConfigHandler.vanillaPotions.instantDamage;
        return VanillaPotionValues.getSplashAmount(VanillaPotionValues.getInstantAmount(cfg.damageAmount, cfg.exponential, amplifier), health);
    }
}

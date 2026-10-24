package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import potioncontrol.config.ConfigHandler;

@Mixin(Potion.class)
public abstract class PotionMixin_Poison {
    //1.0F ordinal 0 is the regeneration heal, 1 is the poison health check
    @ModifyExpressionValue(
            method = "performEffect",
            at = @At(value = "CONSTANT", args = "floatValue=1.0", ordinal = 1)
    )
    private float potioncontrol_modifyPoisonMinHealth(float original) {
        return ConfigHandler.vanillaPotions.poison.minHealth;
    }

    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 0),
            index = 1
    )
    private float potioncontrol_modifyPoisonDamage(float original) {
        return ConfigHandler.vanillaPotions.poison.damageAmount;
    }
}

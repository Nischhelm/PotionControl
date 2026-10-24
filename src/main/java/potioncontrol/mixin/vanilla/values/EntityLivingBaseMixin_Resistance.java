package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.ConfigHandler;

/**
 * Vanilla blocks (level * 5) / 25 of the damage, this changes it to (level * percent) / 100
 */
@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin_Resistance {
    @ModifyExpressionValue(
            method = "applyPotionDamageCalculations",
            at = @At(value = "CONSTANT", args = "intValue=5")
    )
    private int potioncontrol_modifyResistancePerLevel(int original) {
        return ConfigHandler.vanillaPotions.resistance.reductionPercentPerLevel;
    }

    @ModifyExpressionValue(
            method = "applyPotionDamageCalculations",
            at = @At(value = "CONSTANT", args = "intValue=25")
    )
    private int potioncontrol_modifyResistanceMaxInt(int original) {
        return 100;
    }

    @ModifyExpressionValue(
            method = "applyPotionDamageCalculations",
            at = @At(value = "CONSTANT", args = "floatValue=25.0")
    )
    private float potioncontrol_modifyResistanceMaxFloat(float original) {
        return 100.0F;
    }
}

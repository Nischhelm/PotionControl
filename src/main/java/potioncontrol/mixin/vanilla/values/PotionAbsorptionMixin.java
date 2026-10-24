package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.potion.PotionAbsorption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.ConfigHandler;

@Mixin(PotionAbsorption.class)
public abstract class PotionAbsorptionMixin {
    @ModifyExpressionValue(
            method = {"applyAttributesModifiersToEntity", "removeAttributesModifiersFromEntity"},
            at = @At(value = "CONSTANT", args = "intValue=4")
    )
    private int potioncontrol_modifyAbsorptionAmount(int original) {
        return ConfigHandler.vanillaPotions.absorption.absorptionPerLevel;
    }
}

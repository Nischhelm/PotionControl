package potioncontrol.mixin.modded.firstaid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ichttt.mods.firstaid.common.util.ArmorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.ConfigHandler;

@Mixin(value = ArmorUtils.class, remap = false)
public abstract class ArmorUtilsMixin_Resistance {
    //FirstAid has its own copy of the vanilla resistance calculation for players
    @ModifyExpressionValue(
            method = "applyGlobalPotionModifiers",
            at = @At(value = "FIELD", target = "Lichttt/mods/firstaid/FirstAidConfig$ExternalHealing;resistanceReductionPercentPerLevel:I")
    )
    private static int potioncontrol_modifyResistancePerLevel(int original) {
        if (!ConfigHandler.vanillaPotions.resistance.enabled) return original;
        return ConfigHandler.vanillaPotions.resistance.reductionPercentPerLevel;
    }
}

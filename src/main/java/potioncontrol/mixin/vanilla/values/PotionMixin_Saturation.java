package potioncontrol.mixin.vanilla.values;

import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import potioncontrol.config.ConfigHandler;

@Mixin(Potion.class)
public abstract class PotionMixin_Saturation {
    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addStats(IF)V"),
            index = 0
    )
    private int potioncontrol_modifySaturationFood(int level) {
        return level * ConfigHandler.vanillaPotions.saturation.foodPerLevel;
    }

    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addStats(IF)V"),
            index = 1
    )
    private float potioncontrol_modifySaturationModifier(float original) {
        return ConfigHandler.vanillaPotions.saturation.saturationModifier;
    }
}

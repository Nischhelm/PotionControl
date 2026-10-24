package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.ConfigHandler;

@Mixin(Potion.class)
public abstract class PotionMixin_Hunger {
    @ModifyExpressionValue(
            method = "performEffect",
            at = @At(value = "CONSTANT", args = "floatValue=0.005")
    )
    private float potioncontrol_modifyHungerExhaustion(float original) {
        return ConfigHandler.vanillaPotions.hunger.exhaustionPerLevel;
    }
}

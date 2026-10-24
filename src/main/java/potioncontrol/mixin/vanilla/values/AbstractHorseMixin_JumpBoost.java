package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.ConfigHandler;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin_JumpBoost {
    //0.1F ordinal 1 is the jumpMovementFactor
    @ModifyExpressionValue(
            method = "travel",
            at = @At(value = "CONSTANT", args = "floatValue=0.1", ordinal = 0)
    )
    private float potioncontrol_modifyJumpBoostMotion(float original) {
        return ConfigHandler.vanillaPotions.jumpBoost.jumpMotionPerLevel;
    }
}

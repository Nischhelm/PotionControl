package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import potioncontrol.config.ConfigHandler;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin_JumpBoost {
    @ModifyExpressionValue(
            method = "jump",
            at = @At(value = "CONSTANT", args = "floatValue=0.1")
    )
    private float potioncontrol_modifyJumpBoostMotion(float original) {
        return ConfigHandler.vanillaPotions.jumpBoost.jumpMotionPerLevel;
    }

    //float ordinal 0 and 1 are the distance and damageMultiplier args, 2 is the jump boost level
    @ModifyVariable(
            method = "fall",
            at = @At("STORE"),
            ordinal = 2
    )
    private float potioncontrol_modifyJumpBoostSafeFall(float level) {
        return level * ConfigHandler.vanillaPotions.jumpBoost.safeFallDistancePerLevel;
    }
}

package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.ConfigHandler;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin_Levitation {
    @ModifyExpressionValue(
            method = "travel",
            at = @At(value = "CONSTANT", args = "doubleValue=0.05")
    )
    private double potioncontrol_modifyLevitationSpeed(double original) {
        return ConfigHandler.vanillaPotions.levitation.speedPerLevel;
    }
}

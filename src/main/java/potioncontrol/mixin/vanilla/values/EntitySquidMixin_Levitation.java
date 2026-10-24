package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.passive.EntitySquid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.ConfigHandler;

@Mixin(EntitySquid.class)
public abstract class EntitySquidMixin_Levitation {
    @ModifyExpressionValue(
            method = "onLivingUpdate",
            at = @At(value = "CONSTANT", args = "doubleValue=0.05")
    )
    private double potioncontrol_modifyLevitationSpeed(double original) {
        return ConfigHandler.vanillaPotions.levitation.speedPerLevel;
    }
}

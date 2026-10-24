package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.monster.EntityElderGuardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import potioncontrol.config.ConfigHandler;

@Mixin(EntityElderGuardian.class)
public abstract class EntityElderGuardianMixin_MiningFatigue {
    //Amplifier 2 is used for the reapply check and the applied effect, slice skips the unused local before them
    @ModifyExpressionValue(
            method = "updateAITasks",
            at = @At(value = "CONSTANT", args = "intValue=2"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getAmplifier()I"))
    )
    private int potioncontrol_modifyElderGuardianAmplifier(int original) {
        return ConfigHandler.vanillaPotions.miningFatigue.elderGuardianLevel - 1;
    }
}

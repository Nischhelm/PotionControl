package potioncontrol.mixin.vanilla.values;

import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import potioncontrol.config.ConfigHandler;

@Mixin(Potion.class)
public abstract class PotionMixin_Regeneration {
    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;heal(F)V", ordinal = 0)
    )
    private float potioncontrol_modifyRegenerationHeal(float original) {
        return ConfigHandler.vanillaPotions.regeneration.healAmount;
    }
}

package potioncontrol.mixin.vanilla.values;

import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import potioncontrol.config.ConfigHandler;

@Mixin(Potion.class)
public abstract class PotionMixin_Wither {
    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 1),
            index = 1
    )
    private float potioncontrol_modifyWitherDamage(float original) {
        return ConfigHandler.vanillaPotions.wither.damageAmount;
    }
}

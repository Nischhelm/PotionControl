package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import potioncontrol.config.ConfigHandler;
import potioncontrol.config.folders.VanillaPotionConfig;

import javax.annotation.Nullable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin_Haste {
    @Shadow @Nullable public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    @WrapMethod(method = "getArmSwingAnimationEnd")
    private int potioncontrol_modifyHasteSwingDuration(Operation<Integer> original) {
        PotionEffect haste = this.getActivePotionEffect(MobEffects.HASTE);
        if (haste == null) return original.call();
        VanillaPotionConfig.HasteConfig cfg = ConfigHandler.vanillaPotions.haste;
        int duration = 6 - (haste.getAmplifier() + 1) * cfg.swingReductionPerLevel;
        //0 disables the minimum. Clamping to 0 would make the swing progress 0 / 0 = NaN
        if (cfg.minSwingDuration <= 0) return duration;
        return Math.max(cfg.minSwingDuration, duration);
    }
}

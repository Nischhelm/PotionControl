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

import javax.annotation.Nullable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin_MiningFatigue {
    @Shadow @Nullable public abstract PotionEffect getActivePotionEffect(Potion potionIn);
    @Shadow public abstract boolean isPotionActive(Potion potionIn);

    @WrapMethod(method = "getArmSwingAnimationEnd")
    private int potioncontrol_modifyMiningFatigueSwingDuration(Operation<Integer> original) {
        PotionEffect miningFatigue = this.getActivePotionEffect(MobEffects.MINING_FATIGUE);
        //haste takes priority in vanilla
        if (miningFatigue == null || this.isPotionActive(MobEffects.HASTE)) return original.call();
        return 6 + (miningFatigue.getAmplifier() + 1) * ConfigHandler.vanillaPotions.miningFatigue.swingIncreasePerLevel;
    }
}

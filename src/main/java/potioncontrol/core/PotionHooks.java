package potioncontrol.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import potioncontrol.config.potioninfojsons.PotionInfoInferrerWriter;
import potioncontrol.util.PotionInfo;

import java.util.List;

@SuppressWarnings("unused")
public class PotionHooks {

    public static void isBadEffect(Potion potion, CallbackInfoReturnable<Boolean> cir) {
        PotionInfo info = PotionInfo.get(potion);
        if(info != null && info.overwritesIsBeneficial) cir.setReturnValue(!info.isBeneficial);
    }

    public static void isBeneficial(Potion potion, CallbackInfoReturnable<Boolean> cir) {
        PotionInfo info = PotionInfo.get(potion);
        if(info != null && info.overwritesIsBeneficial) cir.setReturnValue(info.isBeneficial);
    }

    public static void isInstant(Potion potion, CallbackInfoReturnable<Boolean> cir) {
        PotionInfo info = PotionInfo.get(potion);
        if(info != null && info.overwritesIsInstant) cir.setReturnValue(info.isInstant);
    }

    public static void performEffect_isRepeating(Potion potion, EntityLivingBase entityLivingBaseIn, int amplifier, CallbackInfo ci) {
        PotionInfo info = PotionInfo.get(potion);
        if(info != null && info.overwritesIsRepeating && !info.isRepeating) ci.cancel();
    }

    public static void isReady(Potion potion, int duration, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        PotionInfo info = PotionInfo.get(potion);
        if(info != null && info.overwritesIsRepeating) cir.setReturnValue(info.getIsReady(duration, amplifier));
    }

    public static void getLiquidColor(Potion potion, CallbackInfoReturnable<Integer> cir) {
        PotionInfo info = PotionInfo.get(potion);
        if(info != null && info.liquidColorHex != null) cir.setReturnValue(info.getLiquidColor());
    }

    public static List<ItemStack> getCurativeItems(Potion potion, List<ItemStack> original) {
        PotionInfo info = PotionInfo.get(potion);
        if(info != null) {
            if (!info.milkRemovable)
                original.removeIf(stack -> stack.isItemEqual(PotionInfoInferrerWriter.milk));
            if (info.curativeItems != null && !info.curativeItems.isEmpty())
                info.curativeItems.forEach(stack -> original.add(stack.copy()));
        }
        return original;
    }
}

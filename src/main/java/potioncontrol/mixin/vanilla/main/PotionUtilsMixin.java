package potioncontrol.mixin.vanilla.main;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PotionUtils.class)
public class PotionUtilsMixin {
    @Unique private static final String[] pc$ROMAN = {"", "", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

    @WrapOperation(
            method = "addPotionTooltip",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/text/translation/I18n;translateToLocal(Ljava/lang/String;)Ljava/lang/String;", ordinal = 2)
    )
    private static String pc_morePotencyTooltips(String key, Operation<String> original, @Local PotionEffect effect) {
        int lvl = effect.getAmplifier() + 1;
        if (lvl > 0 && lvl <= 10) return pc$ROMAN[lvl];
        else return String.valueOf(lvl);
    }
}
package potioncontrol.mixin.vanilla;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import potioncontrol.util.PotionInfo;

@Mixin(InventoryEffectRenderer.class)
public abstract class InventoryEffectRendererMixin {
    @ModifyArg(
            method = "drawActivePotionEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I", ordinal = 0)
    )
    private String pc_modifyName(String text, @Local Potion potion, @Local PotionEffect effect) {
        PotionInfo info = PotionInfo.get(potion);
        if(info != null) return info.getTranslatedName(potion, effect.getAmplifier());
        else {
            text = I18n.format(potion.getName());

            int amp = effect.getAmplifier();
            if (amp > 0 && amp < 10)
                text += " " + I18n.format("enchantment.level." + (amp + 1));
            else if (amp != 0) text = text + " " + (amp + 1); //lvl 11+ or negative lvls

            return text;
        }
    }
}

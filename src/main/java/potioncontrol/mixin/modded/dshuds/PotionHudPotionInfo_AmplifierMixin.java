package potioncontrol.mixin.modded.dshuds;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.orecruncher.lib.Localization;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.util.PotionInfo;

@Mixin(targets = "org.orecruncher.dshuds.hud.PotionHUD$PotionInfo")
public abstract class PotionHudPotionInfo_AmplifierMixin {

    @Mutable @Shadow(remap = false) @Final private String effectText;
    @Shadow(remap = false) @Final private Potion potion;
    @Shadow(remap = false) @Final private PotionEffect potionEffect;

    @Inject(method = "<init>", at = @At(value = "TAIL"), remap = false)
    private void pc_displayCorrectAmplifier(PotionEffect effect, CallbackInfo ci) {
        if(this.potion.shouldRenderInvText(this.potionEffect)) return;
        PotionInfo info = PotionInfo.get(this.potion);
        if(info != null) this.effectText = info.getTranslatedName(this.potion, this.potionEffect.getAmplifier());
        else {
            String newText = Localization.format(this.potion.getName());
            if (this.potionEffect.getAmplifier() > 0 && this.potionEffect.getAmplifier() < 10)
                newText += " " + Localization.format("enchantment.level." + (this.potionEffect.getAmplifier() + 1));
            else if (this.potionEffect.getAmplifier() < 0 || this.potionEffect.getAmplifier() >= 10)
                newText += " " + (this.potionEffect.getAmplifier() + 1);
            this.effectText = newText;
        }
    }
}
package potioncontrol.mixin.modded.potiondescriptions;

import azmalent.potiondescriptions.PotionDescriptions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import potioncontrol.config.descriptions.DescriptionReader;

@Mixin(PotionDescriptions.class)
public abstract class PotionDescriptionsMixin {
    @ModifyVariable(method = "postInit", at = @At(value = "STORE"), name = "translationKey", remap = false)
    private String pc_fixlog(String key, @Local(name = "potion") Potion potion) {
        return "description."+potion.getName();
    }

    @ModifyExpressionValue(method = "postInit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/I18n;hasKey(Ljava/lang/String;)Z"), remap = false)
    private boolean pc_injectDescriptions(boolean original, @Local(name = "potion") Potion potion) {
        return original || DescriptionReader.hasDescriptionFor(potion);
    }
}

package potioncontrol.mixin.modded.potiondescriptions;

import azmalent.potiondescriptions.client.TooltipHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.descriptions.DescriptionReader;

@Mixin(TooltipHandler.class)
public abstract class TooltipHandlerMixin {
    @ModifyReturnValue(method = "getEffectDescription", at = @At(value = "RETURN"), remap = false)
    private static String pc_injectDescription(String value, @Local(argsOnly = true) Potion potion) {
        return DescriptionReader.getDescriptionKey(potion, value);
    }
}

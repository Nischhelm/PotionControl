package potioncontrol.mixin.modded.potiondescriptions;

import azmalent.potiondescriptions.client.TooltipHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.descriptions.DescriptionReader;

import java.util.Arrays;
import java.util.List;

@Mixin(TooltipHandler.class)
public abstract class TooltipHandlerMixin {
    @ModifyReturnValue(method = "getEffectDescription", at = @At(value = "RETURN"), remap = false)
    private static String pc_injectDescription(String value, @Local(argsOnly = true) Potion potion) {
        return DescriptionReader.getDescriptionKey(potion, value);
    }

    @WrapOperation(
            method = "addTooltip",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2),
            remap = false
    )
    private static boolean pc_addLineBreaks(List<String> tooltips, Object obj, Operation<Boolean> original){
        String desc = (String) obj;
        if(desc.contains("\\n"))
            return tooltips.addAll(Arrays.asList(desc.split("\\\\n")));

        return original.call(tooltips, desc);
    }
}

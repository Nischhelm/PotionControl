package potioncontrol.mixin.vanilla;

import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.ai.attributes.IAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.util.BaseAttributeRegistry;

import javax.annotation.Nullable;

@Mixin(BaseAttribute.class)
public abstract class BaseAttributeMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void pc_grabRegisteredAttributes(@Nullable IAttribute attribute, String translationKey, double defaultVal, CallbackInfo ci) {
        BaseAttributeRegistry.register((BaseAttribute) (Object) this);
    }
}

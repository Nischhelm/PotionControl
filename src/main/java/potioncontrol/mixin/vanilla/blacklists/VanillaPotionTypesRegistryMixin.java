package potioncontrol.mixin.vanilla.blacklists;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.init.PotionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.core.PotionTypeDummy;

@Mixin(PotionTypes.class)
public abstract class VanillaPotionTypesRegistryMixin {

    @ModifyExpressionValue(method = "getRegisteredPotionType", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/RegistryNamespacedDefaultedByKey;getObject(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object onRegister(Object original, String id) {
        if(!id.equals("empty") && original == PotionTypes.EMPTY) return new PotionTypeDummy(id);
        return original;
    }
}
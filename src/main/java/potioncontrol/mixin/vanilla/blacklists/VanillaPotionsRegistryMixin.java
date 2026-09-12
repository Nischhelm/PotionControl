package potioncontrol.mixin.vanilla.blacklists;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.init.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.core.PotionDummy;

@Mixin(MobEffects.class)
public abstract class VanillaPotionsRegistryMixin {

    @ModifyExpressionValue(method = "getRegisteredMobEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/RegistryNamespaced;getObject(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object onRegister(Object original) {
        if(original == null) return PotionDummy.dummy;
        return original;
    }
}


package potioncontrol.mixin.vanilla.blacklists;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.PotionControl;
import potioncontrol.util.PotionTypeDeRegisterHelper;

@Mixin(PotionType.class)
public abstract class VanillaPotionTypeRegistryMixin {

    @WrapWithCondition(method = "registerPotionType", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/RegistryNamespacedDefaultedByKey;register(ILjava/lang/Object;Ljava/lang/Object;)V"))
    private static boolean onRegister(RegistryNamespacedDefaultedByKey<ResourceLocation, PotionType> instance, int id, Object loc, Object potionType) {
        //Prevent registration of config defined potions
        if (PotionTypeDeRegisterHelper.isBlacklisted((PotionType) potionType)) {
            PotionControl.LOGGER.info("Preventing registration of potion type {}", loc.toString());
            return false;
        }
        return true;
    }
}


package potioncontrol.mixin.vanilla.blacklists;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.PotionControl;
import potioncontrol.config.EarlyConfigReader;

@Mixin(Potion.class)
public abstract class VanillaPotionRegistryMixin {

    @WrapWithCondition(method = "registerPotions", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/RegistryNamespaced;register(ILjava/lang/Object;Ljava/lang/Object;)V"))
    private static boolean onRegister(RegistryNamespaced<ResourceLocation, Potion> instance, int id, Object loc, Object pot) {
        if(EarlyConfigReader.getPotionRegistrationBlacklist().isEmpty()) return true;

        //Prevent registration of config defined potions
        if (EarlyConfigReader.getPotionRegistrationBlacklist().contains(loc.toString())) {
            PotionControl.LOGGER.info("Preventing registration of potion {}", loc.toString());
            return false;
        }
        return true;
    }
}


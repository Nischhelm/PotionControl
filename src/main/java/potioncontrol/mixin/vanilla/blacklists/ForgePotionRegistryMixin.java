package potioncontrol.mixin.vanilla.blacklists;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;

@Mixin(ForgeRegistry.class)
public abstract class ForgePotionRegistryMixin<V extends IForgeRegistryEntry<V>> {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRegister(V value, CallbackInfo ci) {
        if (value instanceof Potion) {
            if (ConfigHandler.blacklists.blacklistedRegistryPotions.isEmpty()) return;

            ResourceLocation loc = value.getRegistryName();
            if (loc == null) return;

            //Prevent registration of config defined potions
            if (ConfigHandler.blacklists.blacklistedRegistryPotions.contains(loc.toString())) {
                PotionControl.LOGGER.info("Preventing registration of potion {}", loc.toString());
                ci.cancel();
            }
        } else if (value instanceof PotionType) {
            if (ConfigHandler.blacklists.blacklistedRegistryPotionTypes.isEmpty()) return;

            ResourceLocation loc = value.getRegistryName();
            if (loc == null) return;

            //Prevent registration of config defined potions
            if (ConfigHandler.blacklists.blacklistedRegistryPotionTypes.contains(loc.toString())) {
                PotionControl.LOGGER.info("Preventing registration of potion type {}", loc.toString());
                ci.cancel();
            }
        }
    }
}


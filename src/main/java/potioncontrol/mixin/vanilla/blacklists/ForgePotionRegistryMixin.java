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
import potioncontrol.util.PotionTypeDeRegisterHelper;

@Mixin(ForgeRegistry.class)
public abstract class ForgePotionRegistryMixin<V extends IForgeRegistryEntry<V>> {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRegister(V value, CallbackInfo ci) {
        if (value instanceof Potion) {
            ResourceLocation loc = value.getRegistryName();
            if (loc == null) return;

            //Prevent registration of config defined potions
            if (ConfigHandler.blacklists.blacklistedRegistryPotions.contains(loc.toString())) {
                PotionControl.LOGGER.info("Preventing registration of Potion {}", loc.toString());
                ci.cancel();
            }
        } else if (value instanceof PotionType) {
            //Prevent registration of config defined potions
            if (PotionTypeDeRegisterHelper.isBlacklisted((PotionType) value)) {
                PotionControl.LOGGER.info("Preventing registration of PotionType {}", value.getRegistryName().toString());
                ci.cancel();
            }
        }
    }
}


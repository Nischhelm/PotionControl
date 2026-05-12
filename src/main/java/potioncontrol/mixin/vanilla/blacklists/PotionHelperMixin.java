package potioncontrol.mixin.vanilla.blacklists;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.config.provider.BlacklistConfigProvider;

@Mixin(PotionHelper.class)
public abstract class PotionHelperMixin {
    @Inject(
            method = "addMix(Lnet/minecraft/potion/PotionType;Lnet/minecraft/item/crafting/Ingredient;Lnet/minecraft/potion/PotionType;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void pc_unregisterRecipes(PotionType typeIn, Ingredient ingredient, PotionType typeOut, CallbackInfo ci) {
        ResourceLocation locIn = typeIn.getRegistryName();
        if(locIn != null && BlacklistConfigProvider.getRegistryPotionTypeBlacklist().contains(locIn.toString())) {
            ci.cancel();
            return;
        }
        ResourceLocation locOut = typeOut.getRegistryName();
        if(locOut != null && BlacklistConfigProvider.getRegistryPotionTypeBlacklist().contains(locOut.toString()))
            ci.cancel();
    }
}

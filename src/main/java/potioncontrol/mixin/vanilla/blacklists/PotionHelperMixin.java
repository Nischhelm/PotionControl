package potioncontrol.mixin.vanilla.blacklists;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.util.PotionTypeDeRegisterHelper;

@Mixin(PotionHelper.class)
public abstract class PotionHelperMixin {
    @Inject(
            method = "addMix(Lnet/minecraft/potion/PotionType;Lnet/minecraft/item/crafting/Ingredient;Lnet/minecraft/potion/PotionType;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void pc_unregisterRecipes(PotionType typeIn, Ingredient ingredient, PotionType typeOut, CallbackInfo ci) {
        if(PotionTypeDeRegisterHelper.isBlacklisted(typeIn) || PotionTypeDeRegisterHelper.isBlacklisted(typeOut))
            ci.cancel();
    }
}

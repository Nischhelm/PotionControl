package potioncontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.util.brewing.BrewRecipe;
import potioncontrol.util.brewing.BrewRecipeUtil;
import potioncontrol.util.wrapper.IHasBrewRecipe;

import java.util.List;

@Mixin(PotionHelper.class)
public abstract class PotionHelperMixin {
    @Shadow @Final private static List<Object> POTION_TYPE_CONVERSIONS;

    @ModifyReturnValue(
            method = "hasTypeConversions",
            at = @At(value = "RETURN", ordinal = 0)
    )
    private static boolean potioncontrol_setCurrentlySelectedRecipe(boolean original, @Local(name = "i") int index){
        if(!original) return false; //this should never happen except other mixins

        Object mixPredicate = POTION_TYPE_CONVERSIONS.get(index);

        if(mixPredicate instanceof IHasBrewRecipe){
            BrewRecipe recipe = ((IHasBrewRecipe) mixPredicate).pc$getBrewRecipe();
            BrewRecipeUtil.setCurrentlySelectedRecipe(recipe);
        }
        return true;
    }

    @ModifyReturnValue(
            method = "doReaction",
            at = @At(value = "RETURN", ordinal = 1)
    )
    private static ItemStack potioncontrol_setCurrentlySelectedRecipe_doReaction(ItemStack original, @Local(name = "i") int index){
        Object mixPredicate = POTION_TYPE_CONVERSIONS.get(index);

        if(mixPredicate instanceof IHasBrewRecipe){
            BrewRecipe recipe = ((IHasBrewRecipe) mixPredicate).pc$getBrewRecipe();
            BrewRecipeUtil.setCurrentlySelectedRecipe(recipe);
        }
        return original;
    }
}

package potioncontrol.mixin.vanilla;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import potioncontrol.compat.CompatUtil;
import potioncontrol.compat.crafttweaker.MultiBrewingRecipeCompat;
import potioncontrol.util.brewing.BrewRecipe;
import potioncontrol.util.brewing.BrewRecipeUtil;
import potioncontrol.util.brewing.Input;

import java.util.ArrayList;
import java.util.List;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {
//    @Shadow private static List<IBrewingRecipe> recipes;

//    @Inject(
//            method = "addRecipe(Lnet/minecraftforge/common/brewing/IBrewingRecipe;)Z",
//            at = @At("HEAD"),
//            remap = false,
//            cancellable = true
//    )
//    private static void potioncontrol_saveBrewRecipes(IBrewingRecipe irecipe, CallbackInfoReturnable<Boolean> cir){
        //VanillaBrewingRecipe / VanillaBrewPlus passthrough
//        if(CompatUtil.crafttweaker.isLoaded() && MultiBrewingRecipeCompat.isMultiRecipe(irecipe)) {
//            List<BrewRecipe> ct_recipes = MultiBrewingRecipeCompat.registerRecipe(irecipe);
//            if(ct_recipes != null && !ct_recipes.isEmpty()) {
//                recipes.addAll(ct_recipes);
//                cir.setReturnValue(true); //don't register the CT MultiRecipe
//            }
//            //otherwise default handling if not a normal brewing recipe
//        } else
//            if(irecipe instanceof AbstractBrewingRecipe) {
//            AbstractBrewingRecipe<?> recipe = (AbstractBrewingRecipe<?>) irecipe;
//            Object reagent = recipe.getIngredient();
//
//            List<ItemStack> reagents = new ArrayList<>();
//            if (reagent instanceof ItemStack) {
//                reagents.add((ItemStack) reagent);
//            } else if (reagent instanceof List) {
//                reagents.addAll((List<ItemStack>) reagent);
//            }
//            if (reagents.isEmpty())
//                return; // must be some special case, pass to default handling TODO: check if this ever happens
//
//            reagents.forEach(r -> recipes.add(BrewRecipeUtil.addRecipe(
//                    new Input.ItemStackInput(recipe.getInput()),
//                    r,
//                    new Input.ItemStackInput(recipe.getOutput())
//            )));
//
//            cir.setReturnValue(true); //don't add the original recipe
//        }
//    }
}

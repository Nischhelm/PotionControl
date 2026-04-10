package potioncontrol.mixin.vanilla;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import potioncontrol.util.BrewRecipeUtil;

import java.util.ArrayList;
import java.util.List;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {
    @Inject(
            method = "addRecipe(Lnet/minecraftforge/common/brewing/IBrewingRecipe;)Z",
            at = @At("HEAD"),
            remap = false
    )
    private static void potioncontrol_saveBrewRecipes(IBrewingRecipe irecipe, CallbackInfoReturnable<Boolean> cir){
        if(!(irecipe instanceof AbstractBrewingRecipe)) return; //deliberately doesnt cath VanillaBrewingRecipes //TODO: CT compat
        AbstractBrewingRecipe<?> recipe = (AbstractBrewingRecipe<?>) irecipe;
        PotionType potionTypeIn = PotionUtils.getPotionFromItem(recipe.getInput());
        PotionType potionTypeOut = PotionUtils.getPotionFromItem(recipe.getOutput());
        Object reagent = recipe.getIngredient();

        List<ItemStack> reagents = new ArrayList<>();
        if(reagent instanceof ItemStack) {
            reagents.add((ItemStack) reagent);
        } else if(reagent instanceof List) {
            reagents.addAll((List<ItemStack>) reagent);
        }

        reagents.forEach(r -> BrewRecipeUtil.recipes.add(new BrewRecipeUtil.BrewRecipe(potionTypeIn, r, potionTypeOut)));
    }
}

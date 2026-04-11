package potioncontrol.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.vanilla.brewing.BrewingRecipeUtil;
import mezz.jei.plugins.vanilla.brewing.BrewingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import potioncontrol.util.brewing.BrewRecipe;
import potioncontrol.util.brewing.Input;

import java.util.*;
import java.util.stream.Collectors;

@JEIPlugin @SuppressWarnings("unused")
public class JEIAddonPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(getBrewingRecipes(), VanillaRecipeCategoryUid.BREWING);
    }

    private List<BrewingRecipeWrapper> getBrewingRecipes() {
        List<BrewingRecipeWrapper> recipes = this.getBrewRecipeWrappers();
        List<BrewingRecipeWrapper> recipeList = new ArrayList<>(recipes);
        recipeList.sort(Comparator.comparingInt(BrewingRecipeWrapper::getBrewingSteps));
        return recipeList;
    }

    private List<BrewingRecipeWrapper> getBrewRecipeWrappers() {
        return BrewingRecipeRegistry.getRecipes().stream()
                .filter(recipe -> recipe instanceof BrewRecipe)
                .map(recipe -> (BrewRecipe) recipe)
                .map(brewRecipe -> {
                    List<ItemStack> input = brewRecipe.input.asItemStacks();
                    List<ItemStack> output = brewRecipe.output.asItemStacks();
                    for(int i = 0; i < input.size(); i++)
                        if(ItemStack.areItemStacksEqual(input.get(i), BrewingRecipeUtil.POTION))
                            input.set(i, BrewingRecipeUtil.WATER_BOTTLE);

                    List<BrewingRecipeWrapper> recipes = new ArrayList<>();
                    if(brewRecipe.input instanceof Input.ItemStackInput && brewRecipe.output instanceof Input.PotionTypeInput){
                        ItemStack in = input.get(0);
                        ItemStack out = PotionUtils.addPotionToItemStack(in.copy(), ((Input.PotionTypeInput) brewRecipe.output).type);
                        recipes.add(new BrewingRecipeWrapper(Collections.singletonList(brewRecipe.reagent), in, out));
                    }
                    else if(brewRecipe.input instanceof Input.PotionTypeInput && brewRecipe.output instanceof Input.ItemStackInput)
                        for (ItemStack in : input)
                            recipes.add(new BrewingRecipeWrapper(Collections.singletonList(brewRecipe.reagent), in, output.get(0)));
                    //itemstack to itemstack will never appear here (bc tied to jsons for a type)
                    //type to type will never appear here bc handled in vanillaBrewingRecipe
                    return recipes;
                }).flatMap(Collection::stream).collect(Collectors.toList());
    }
}

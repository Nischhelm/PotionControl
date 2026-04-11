package potioncontrol.util.brewing;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import potioncontrol.mixin.accessor.PotionHelperAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BrewRecipeUtil {
    private static final List<BrewRecipe> recipes = new ArrayList<>();
    public static final List<VanillaContainer> vanillaRecipes = new ArrayList<>();

    public static List<BrewRecipe> getRecipes(PotionType potionType, boolean getByInput){
        if(getByInput) {
            List<BrewRecipe> recipesOfType = recipes.stream().filter(r -> Input.isPotionTypeOf(r.input, potionType)).collect(Collectors.toList());
            recipesOfType.addAll(vanillaRecipes.stream().filter(r -> Input.isPotionTypeOf(r.input, potionType)).collect(Collectors.toList()));
            return recipesOfType;
        }
        else {
            List<BrewRecipe> recipesOfType = recipes.stream().filter(r -> Input.isPotionTypeOf(r.output, potionType)).collect(Collectors.toList());
            recipesOfType.addAll(vanillaRecipes.stream().filter(r -> Input.isPotionTypeOf(r.output, potionType)).collect(Collectors.toList()));
            return recipesOfType;
        }
    }

    public static BrewRecipe addRecipe(Input in, ItemStack reagent, Input out){
//        for(BrewRecipe r : recipes)
//            if(r.input == in && areItemStacksEqual(r.reagent, reagent)) return r; //TODO

        BrewRecipe recipe = new BrewRecipe(in, reagent, out);
        recipes.add(recipe);
        BrewingRecipeRegistry.addRecipe(recipe);
        return recipe;
    }

    public static void removeForType(PotionType potionType, boolean asInput){
        //If a json defines either brews_from or brews_to, we remove all existing brewing recipes that have this type as to/from

        if(asInput) recipes.removeIf(r -> Input.isPotionTypeOf(r.input, potionType));
        else recipes.removeIf(r -> Input.isPotionTypeOf(r.output, potionType));

        //Remove from vanilla list:
        List<VanillaContainer> vrecipes = vanillaRecipes.stream().filter(r -> asInput ? Input.isPotionTypeOf(r.input, potionType) : Input.isPotionTypeOf(r.output, potionType)).collect(Collectors.toList());
        vrecipes.forEach(r -> PotionHelperAccessor.getPotionTypeConversions().remove(r.mixPredicate));
        vanillaRecipes.removeAll(vrecipes);
    }

    public static void addVanillaRecipe(PotionType in, ItemStack reagent, PotionType out, Object mixPredicate) {
        vanillaRecipes.add(new VanillaContainer(new Input.PotionTypeInput(in), reagent, new Input.PotionTypeInput(out), mixPredicate));
    }

    public static class VanillaContainer extends BrewRecipe {
        //Vanilla Recipes are registered through VanillaBrewingRecipe -> PotionHelper.MixPredicate which we get here (as object cause private class)
        private final Object mixPredicate;

        public VanillaContainer(Input in, ItemStack reagent, Input out, Object mixPredicate) {
            super(in, reagent, out);
            this.mixPredicate = mixPredicate;
        }
    }
}

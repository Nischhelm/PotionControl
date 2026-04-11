package potioncontrol.util.brewing;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import potioncontrol.mixin.accessor.PotionHelperAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BrewRecipeUtil {
    private static final ThreadLocal<BrewRecipe> currentlySelectedRecipe = ThreadLocal.withInitial(() -> null);
    public static BrewRecipe getAndClearCurrentBrewRecipe(){
        BrewRecipe recipe = currentlySelectedRecipe.get();
        currentlySelectedRecipe.remove();
        return recipe;
    }
    public static void setCurrentlySelectedRecipe(BrewRecipe recipe){
        currentlySelectedRecipe.set(recipe);
    }

    private static final List<BrewRecipe> customRecipes = new ArrayList<>();
    public static final List<VanillaBrewRecipe> vanillaRecipes = new ArrayList<>();

    public static List<BrewRecipe> getRecipes(PotionType potionType, boolean getByInput){
        if(getByInput) {
            List<BrewRecipe> recipesOfType = customRecipes.stream().filter(r -> Input.isPotionTypeOf(r.input, potionType)).collect(Collectors.toList());
            recipesOfType.addAll(vanillaRecipes.stream().filter(r -> Input.isPotionTypeOf(r.input, potionType)).collect(Collectors.toList()));
            return recipesOfType;
        }
        else {
            List<BrewRecipe> recipesOfType = customRecipes.stream().filter(r -> Input.isPotionTypeOf(r.output, potionType)).collect(Collectors.toList());
            recipesOfType.addAll(vanillaRecipes.stream().filter(r -> Input.isPotionTypeOf(r.output, potionType)).collect(Collectors.toList()));
            return recipesOfType;
        }
    }

    public static BrewRecipe addRecipe(Input in, ItemStack reagent, Input out){
        if(in instanceof Input.PotionTypeInput && out instanceof Input.PotionTypeInput){
            //Use vanilla system
            PotionHelper.addMix(((Input.PotionTypeInput)in).type, Ingredient.fromStacks(reagent), ((Input.PotionTypeInput)out).type);
            //via mixin calls addVanillaRecipe
            return vanillaRecipes.get(vanillaRecipes.size() - 1);
            //Vanilla Recipes don't need to be registered at BrewingRecipeRegistry.addRecipe
        }

        BrewRecipe recipe = new BrewRecipe(in, reagent, out);
        customRecipes.add(recipe);
        BrewingRecipeRegistry.addRecipe(recipe);
        return recipe;
    }

    public static void removeForType(PotionType potionType, boolean asInput){
        //If a json defines brews_from or brews_to, we remove all existing vanilla style brewing recipes that have this type as to/from
        // Note: we don't need to delete modded recipes (any IBrewingRecipe) as these almost never fulfil the conditions of filling an entire potiontype
        // These recipes are left untouched
        List<VanillaBrewRecipe> vrecipes = vanillaRecipes.stream().filter(r -> asInput ? Input.isPotionTypeOf(r.input, potionType) : Input.isPotionTypeOf(r.output, potionType)).collect(Collectors.toList());
        vrecipes.forEach(r -> PotionHelperAccessor.getPotionTypeConversions().remove(r.mixPredicate));
        vanillaRecipes.removeAll(vrecipes);
    }

    public static VanillaBrewRecipe addVanillaRecipe(PotionType in, ItemStack reagent, PotionType out, Object mixPredicate) {
        VanillaBrewRecipe recipe = new VanillaBrewRecipe(new Input.PotionTypeInput(in), reagent, new Input.PotionTypeInput(out), mixPredicate);
        vanillaRecipes.add(recipe);
        return recipe;
    }

    public static class VanillaBrewRecipe extends BrewRecipe {
        //Vanilla Recipes are registered through VanillaBrewingRecipe -> PotionHelper.MixPredicate which we get here (as object cause private class)
        private final Object mixPredicate; //This handle is used to remove it from PotionHelper.POTION_TYPE_CONVERSIONS

        public VanillaBrewRecipe(Input in, ItemStack reagent, Input out, Object mixPredicate) {
            super(in, reagent, out);
            this.mixPredicate = mixPredicate;
        }
    }
}

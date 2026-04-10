package potioncontrol.util;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class BrewRecipeUtil {
    public static final List<BrewRecipe> recipes = new ArrayList<>();

    public static class BrewRecipe {
        public PotionType in, out;
        public ItemStack reagent;

        public BrewRecipe(PotionType in, ItemStack reagent, PotionType out) {
            this.in = in;
            this.out = out;
            this.reagent = reagent;
        }
    }
}

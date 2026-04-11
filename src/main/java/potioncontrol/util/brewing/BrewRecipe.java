package potioncontrol.util.brewing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;

public class BrewRecipe implements IBrewingRecipe {
    public Input input;
    public ItemStack reagent;
    public Input output;
    public int brewTime = 400;

    public BrewRecipe(Input in, ItemStack reagent, Input out) {
        this.input = in;
        this.output = out;
        this.reagent = reagent;
    }

    public void setBrewTime(int brewTime) {
        this.brewTime = brewTime;
    }

    @Override
    public boolean isInput(@Nonnull ItemStack input) {
        return this.input.isInput(input);
    }

    @Override
    public boolean isIngredient(@Nonnull ItemStack ingredient) {
        return Input.areItemStacksEqual(this.reagent, ingredient);
    }

    @Nonnull @Override
    public ItemStack getOutput(@Nonnull ItemStack input, @Nonnull ItemStack ingredient) {
        if(!isInput(input) || !isIngredient(ingredient)) return  ItemStack.EMPTY;
        return this.output.getOutput(input);
    }
}


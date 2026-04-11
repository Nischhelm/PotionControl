package potioncontrol.util.wrapper;

import potioncontrol.util.brewing.BrewRecipe;

public interface IHasBrewRecipe {
    BrewRecipe pc$getBrewRecipe();
    void pc$setBrewRecipe(BrewRecipe recipe);
}

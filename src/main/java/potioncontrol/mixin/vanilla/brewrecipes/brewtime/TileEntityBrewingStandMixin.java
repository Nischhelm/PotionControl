package potioncontrol.mixin.vanilla.brewrecipes.brewtime;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import potioncontrol.config.ConfigHandler;
import potioncontrol.util.brewing.BrewRecipe;
import potioncontrol.util.brewing.BrewRecipeUtil;

@Mixin(TileEntityBrewingStand.class)
public abstract class TileEntityBrewingStandMixin extends TileEntity {
    @ModifyConstant(
            method = "update",
            constant = @Constant(intValue = 400)
    )
    private int potioncontrol_overwriteBrewTime(int original){
        BrewRecipe recipe = BrewRecipeUtil.getAndClearCurrentBrewRecipe();
        if(recipe == null) return (int) (original * ConfigHandler.brewingStand.brewTimeMultiplier);
        return (int) (recipe.getBrewTime() * ConfigHandler.brewingStand.brewTimeMultiplier);
    }
}

package potioncontrol.mixin.vanilla;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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
        if(recipe == null) return original;
        this.world.playerEntities.forEach(player -> player.sendMessage(new TextComponentString(""+recipe.getBrewTime())));
        return recipe.getBrewTime();
    }
}

package potioncontrol.mixin.vanilla.brewrecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.util.brewing.BrewRecipe;
import potioncontrol.util.brewing.BrewRecipeUtil;
import potioncontrol.util.wrapper.IHasBrewRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(targets = "net.minecraft.potion.PotionHelper.MixPredicate")
public abstract class PotionHelper_MixPredicateMixin implements IHasBrewRecipe {
    @Shadow @Final IRegistryDelegate<Object> input;
    @Shadow @Final Ingredient reagent;
    @Shadow @Final IRegistryDelegate<Object> output;

    @Unique private BrewRecipe pc$brewRecipe = null;
    @Unique public void pc$setBrewRecipe(BrewRecipe recipe) {
        this.pc$brewRecipe = recipe;
    }
    @Unique public BrewRecipe pc$getBrewRecipe(){
        return this.pc$brewRecipe;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void potioncontrol_saveVanillaBrewingRecipes(CallbackInfo ci){
        Object typeIn = input.get();
        Object typeOut = output.get();
        if(!(typeIn instanceof PotionType) || !(typeOut instanceof PotionType)) return; //discard container changing recipes = ITEM_CONVERSIONS
        PotionType potionTypeIn = (PotionType) typeIn;
        PotionType potionTypeOut = (PotionType) typeOut;

        List<ItemStack> reagents = Arrays.stream(reagent.getMatchingStacks()).collect(Collectors.toList());

        if(reagents.isEmpty()) return; //this will always be size 1, except if mods add more Mixes

        reagents.forEach(r -> {
            BrewRecipe recipe = BrewRecipeUtil.addVanillaRecipe(potionTypeIn, r, potionTypeOut, this);
            if(this.pc$getBrewRecipe() == null) //only save the first, this is only for brewTime
                this.pc$setBrewRecipe(recipe);
        });
    }
}

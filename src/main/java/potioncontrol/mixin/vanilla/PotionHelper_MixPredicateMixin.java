package potioncontrol.mixin.vanilla;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.util.brewing.BrewRecipeUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(targets = "net.minecraft.potion.PotionHelper.MixPredicate")
public abstract class PotionHelper_MixPredicateMixin<T extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<T>> {
    @Shadow @Final IRegistryDelegate<T> input;
    @Shadow @Final Ingredient reagent;
    @Shadow @Final IRegistryDelegate<T> output;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void potioncontrol_saveVanillaBrewingRecipes(CallbackInfo ci){
        Object typeIn = input.get();
        Object typeOut = output.get();
        if(!(typeIn instanceof PotionType) || !(typeOut instanceof PotionType)) return;
        PotionType potionTypeIn = (PotionType) typeIn;
        PotionType potionTypeOut = (PotionType) typeOut;

        List<ItemStack> reagents = Arrays.stream(reagent.getMatchingStacks()).collect(Collectors.toList());

        if(reagents.isEmpty()) return; //this will always be size 1, except if mods add more Mixes

        reagents.forEach(r -> BrewRecipeUtil.addVanillaRecipe(potionTypeIn, r, potionTypeOut, this));
    }
}

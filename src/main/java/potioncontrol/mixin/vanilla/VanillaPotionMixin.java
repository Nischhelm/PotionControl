package potioncontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.potioninfojsons.PotionInfoInferrerWriter;
import potioncontrol.util.PotionInfo;

import java.util.List;

@Mixin(targets = {
        "net.minecraft.potion.PotionAbsorption",
        "net.minecraft.potion.PotionAttackDamage",
        "net.minecraft.potion.PotionHealth",
        "net.minecraft.potion.PotionHealthBoost"
})
@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
public abstract class VanillaPotionMixin extends Potion { //copy of VanillaBasePotionMixin and modded.PotionMixin just for all vanilla potions
    protected VanillaPotionMixin(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    @WrapMethod(method = "isBadEffect()Z")
    public boolean pc_isBadEffect(Operation<Boolean> original) {
        PotionInfo info = PotionInfo.get(this);
        if(info != null && info.overwritesIsBeneficial) return !info.isBeneficial;
        return original.call();
    }

    @SideOnly(Side.CLIENT)
    @WrapMethod(method = "isBeneficial()Z")
    public boolean pc_isBeneficial(Operation<Boolean> original) {
        PotionInfo info = PotionInfo.get(this);
        if(info != null && info.overwritesIsBeneficial) return info.isBeneficial;
        return original.call();
    }

    @WrapMethod(method = "getLiquidColor()I")
    public int pc_getLiquidColor(Operation<Integer> original) {
        PotionInfo info = PotionInfo.get(this);
        if(info != null && info.liquidColorHex != null) return info.getLiquidColor();
        return original.call();
    }

    @ModifyReturnValue(method = "getCurativeItems()Ljava/util/List;", at = @At("RETURN"))
    public List<ItemStack> pc_getCurativeItems(List<ItemStack> original) {
        PotionInfo info = PotionInfo.get(this);
        if(info != null) {
            if (!info.milkRemovable)
                original.removeIf(stack -> stack.isItemEqual(PotionInfoInferrerWriter.milk));
            if (info.curativeItems != null)
                info.curativeItems.forEach(stack -> original.add(stack.copy()));
        }
        return original;
    }
}
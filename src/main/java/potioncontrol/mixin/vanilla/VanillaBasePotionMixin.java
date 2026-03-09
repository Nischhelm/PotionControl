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

@Mixin(Potion.class) //copy of VanillaPotionMixin and modded.PotionMixin just for net.minecraft.potion.Potion
public abstract class VanillaBasePotionMixin {

    @WrapMethod(method = "isBadEffect")
    public boolean pc_isBadEffect(Operation<Boolean> original) {
        PotionInfo info = PotionInfo.get((Potion) (Object) this);
        if(info != null && info.overwritesIsBeneficial) return !info.isBeneficial;
        return original.call();
    }

    @SideOnly(Side.CLIENT)
    @WrapMethod(method = "isBeneficial")
    public boolean pc_isBeneficial(Operation<Boolean> original) {
        PotionInfo info = PotionInfo.get((Potion) (Object) this);
        if(info != null && info.overwritesIsBeneficial) return info.isBeneficial;
        return original.call();
    }

    @WrapMethod(method = "getLiquidColor")
    public int pc_getLiquidColor(Operation<Integer> original) {
        PotionInfo info = PotionInfo.get((Potion) (Object) this);
        if(info != null && info.liquidColorHex != null) return info.getLiquidColor();
        return original.call();
    }

    @ModifyReturnValue(method = "getCurativeItems", at = @At("RETURN"), remap = false)
    public List<ItemStack> pc_getCurativeItems(List<ItemStack> original) {
        PotionInfo info = PotionInfo.get((Potion) (Object) this);
        if(info != null) {
            if (!info.milkRemovable)
                original.removeIf(stack -> stack.isItemEqual(PotionInfoInferrerWriter.milk));
            if (info.curativeItems != null)
                info.curativeItems.forEach(stack -> original.add(stack.copy()));
        }
        return original;
    }
}
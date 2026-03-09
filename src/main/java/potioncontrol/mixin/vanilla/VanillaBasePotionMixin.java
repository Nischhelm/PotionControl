package potioncontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import potioncontrol.util.PotionInfo;

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
}
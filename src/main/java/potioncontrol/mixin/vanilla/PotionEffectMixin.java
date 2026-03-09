package potioncontrol.mixin.vanilla;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionEffect.class)
public abstract class PotionEffectMixin {
    @Shadow public abstract int getAmplifier();

    @Inject(
            method = "writeCustomPotionEffectToNBT",
            at = @At("TAIL")
    )
    private void pc_writeActualAmplifier(NBTTagCompound nbt, CallbackInfoReturnable<NBTTagCompound> cir){
        nbt.setShort("PC_ActualAmplifier", (short) this.getAmplifier());
    }

    @ModifyArg(
            method = "readCustomPotionEffectFromNBT",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;<init>(Lnet/minecraft/potion/Potion;IIZZ)V"),
            index = 2
    )
    private static int pc_readActualAmplifier(int ampIn, @Local(argsOnly = true) NBTTagCompound nbt){
        if(nbt.hasKey("PC_ActualAmplifier")) return nbt.getShort("PC_ActualAmplifier");
        return ampIn;
    }
}

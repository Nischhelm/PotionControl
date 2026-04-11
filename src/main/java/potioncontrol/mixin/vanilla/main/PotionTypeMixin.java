package potioncontrol.mixin.vanilla.main;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.util.PotionTypeInfo;

import java.util.List;

@Mixin(PotionType.class)
public abstract class PotionTypeMixin {
    @ModifyReturnValue(
            method = "getEffects",
            at = @At("RETURN")
    )
    private List<PotionEffect> pc_modifyEffects(List<PotionEffect> original){
        PotionTypeInfo info = PotionTypeInfo.get((PotionType)(Object) this);
        if(info != null && info.effects != null) return info.effects;
        return original;
    }
}

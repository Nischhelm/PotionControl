package potioncontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.potion.PotionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import potioncontrol.util.PotionTypeInfo;

@Mixin(EntityTippedArrow.class)
public abstract class EntityTippedArrowMixin {
    @Shadow private PotionType potion;

    @ModifyExpressionValue(
            method = "arrowHit",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I")
    )
    private int potioncontrol_modifyDuration(int originalDuration){
        PotionTypeInfo info = PotionTypeInfo.get(this.potion);
        if(info != null && info.overwritesTippedDuration)
            return info.tippedDuration;
        return originalDuration;
    }
}

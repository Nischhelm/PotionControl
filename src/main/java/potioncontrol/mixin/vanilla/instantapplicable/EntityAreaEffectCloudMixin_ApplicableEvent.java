package potioncontrol.mixin.vanilla.instantapplicable;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.util.InstantPotionHelper;

import javax.annotation.Nullable;

// Instant potion effects (lingering clouds) skip PotionApplicableEvent, so it is fired here before they apply.
@Mixin(EntityAreaEffectCloud.class)
public abstract class EntityAreaEffectCloudMixin_ApplicableEvent {
    @WrapWithCondition(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/Potion;affectEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityLivingBase;ID)V")
    )
    private boolean potioncontrol_fireApplicableEvent(Potion potion, @Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entity, int amplifier, double health) {
        return InstantPotionHelper.isApplicable(potion, entity, amplifier);
    }
}

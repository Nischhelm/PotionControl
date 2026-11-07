package potioncontrol.mixin.vanilla.values;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin_FireResistance {
    @Shadow public World world;
    @Shadow private int fire;
    @Shadow protected boolean isImmuneToFire;
    @Shadow public abstract void extinguish();

    @Inject(method = "setFire", at = @At("HEAD"), cancellable = true)
    private void potioncontrol_preventBurning(int seconds, CallbackInfo ci) {
        if (pc$hasFireResistance()) ci.cancel();
    }

    //Entities that were already burning when they got Fire Resistance
    @Inject(method = "onEntityUpdate", at = @At("HEAD"))
    private void potioncontrol_extinguishBurning(CallbackInfo ci) {
        if (!this.world.isRemote && this.fire > 0 && pc$hasFireResistance()) this.extinguish();
    }

    //Natively fire immune entities (blazes, magma cubes, ...) can still burn, fire res changes nothing for them
    @Unique
    private boolean pc$hasFireResistance() {
        if (this.isImmuneToFire) return false;
        Entity self = (Entity) (Object) this;
        return self instanceof EntityLivingBase && ((EntityLivingBase) self).isPotionActive(MobEffects.FIRE_RESISTANCE);
    }
}

package potioncontrol.mixin.vanilla;

import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.util.PotionInfo;

@Mixin(PotionEffect.class)
public abstract class PotionEffectMixin_DurationPriority {
    @Shadow private int amplifier;
    @Shadow private int duration;
    @Shadow private boolean isAmbient;
    @Shadow private boolean showParticles;

    @Inject(
            method = "combine",
            at = @At(value = "FIELD", target = "Lnet/minecraft/potion/PotionEffect;amplifier:I", ordinal = 0),
            cancellable = true
    )
    private void pc_maybePrioritiseDuration(PotionEffect other, CallbackInfo ci) {
        PotionInfo info = PotionInfo.get(other.getPotion());
        if (info == null) return;
        if (!info.prioritisesDuration) return;

        //vanilla system just inside out, first duration then amplifier

        if(this.duration < other.getDuration()) { //higher duration wins
            this.amplifier = other.getAmplifier();
            this.duration = other.getDuration();
        } else if(this.duration == other.getDuration() && this.amplifier < other.getAmplifier()) //with same duration, higher amplifier wins
            this.amplifier = other.getAmplifier();
        else if (this.isAmbient && !other.getIsAmbient()) //this will basically never happen
            this.isAmbient = other.getIsAmbient();

        this.showParticles = other.doesShowParticles();

        ci.cancel();
    }
}

package potioncontrol.mixin.accessor;

import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionEffect.class)
public interface PotionEffectAccessor {
    @Accessor("duration")
    public void setDuration(int duration);
    @Accessor("amplifier")
    public void setAmplifier(int amplifier);
}

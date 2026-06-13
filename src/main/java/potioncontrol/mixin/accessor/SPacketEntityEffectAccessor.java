package potioncontrol.mixin.accessor;

import net.minecraft.network.play.server.SPacketEntityEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntityEffect.class)
public interface SPacketEntityEffectAccessor {
    @Accessor("entityId") int pc_getEntityId();
    @Accessor("effectId") byte pc_getEffectId();
    @Accessor("duration") int pc_getDuration();
    @Accessor("amplifier") byte pc_getAmplifier();
    @Accessor("flags") byte pc_getFlags();
}

package potioncontrol.mixin.accessor;

import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketRemoveEntityEffect.class)
public interface SPacketRemoveEntityEffectAccessor {
    @Accessor("entityId") int pc_getEntityId();
    @Accessor("effectId") Potion pc_getPotion();
}

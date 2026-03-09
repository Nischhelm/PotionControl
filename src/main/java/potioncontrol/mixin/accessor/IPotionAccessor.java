package potioncontrol.mixin.accessor;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Potion.class)
public interface IPotionAccessor {
    @Accessor("attributeModifierMap")
    Map<IAttribute, AttributeModifier> pc_getAttributeModifierMap();
}

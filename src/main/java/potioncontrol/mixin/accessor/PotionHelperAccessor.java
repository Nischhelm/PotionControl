package potioncontrol.mixin.accessor;

import net.minecraft.potion.PotionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PotionHelper.class)
public interface PotionHelperAccessor {
    @Accessor("POTION_TYPE_CONVERSIONS")
    static List<Object> getPotionTypeConversions(){throw new AssertionError("Failed to access PotionHelper.POTION_TYPE_CONVERSIONS!");};
}

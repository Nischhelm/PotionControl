package potioncontrol.mixin.accessor;

import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(EntityTracker.class)
public interface EntityTrackerAccessor {
    @Accessor("entries")
    Set<EntityTrackerEntry> getEntries();
}

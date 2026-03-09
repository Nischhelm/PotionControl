package potioncontrol.mixin.accessor;

import net.minecraft.util.text.translation.LanguageMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LanguageMap.class)
public interface LanguageMapAccessor {
    @Accessor("languageList")
    Map<String, String> getLanguageList();
}

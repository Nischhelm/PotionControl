package potioncontrol.mixin.accessor;

import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("deprecation")
@Mixin(I18n.class)
public interface I18nAccessor_translation {
    @Accessor("localizedName")
    static LanguageMap getLocalizedName(){return null;}
}

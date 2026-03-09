package potioncontrol.mixin.accessor;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(I18n.class)
public interface I18nAccessor {
    @Accessor("i18nLocale")
    static Locale getLocale(){return null;}
}

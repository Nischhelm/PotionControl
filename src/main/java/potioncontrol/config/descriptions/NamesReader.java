package potioncontrol.config.descriptions;

import net.minecraft.potion.Potion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import potioncontrol.mixin.accessor.I18nAccessor;
import potioncontrol.mixin.accessor.I18nAccessor_translation;
import potioncontrol.mixin.accessor.LanguageMapAccessor;
import potioncontrol.mixin.accessor.LocaleAccessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class NamesReader {
    public static final String PATH = "config/potioncontrol/names.txt";

    public static void init(){
        Map<String, String> potToName = readNames();
        ((LocaleAccessor) I18nAccessor.getLocale()).getProperties().putAll(potToName);
        ((LanguageMapAccessor) I18nAccessor_translation.getLocalizedName()).getLanguageList().putAll(potToName);
    }

    private static Map<String, String> readNames(){
        Map<String, String> potToName = new HashMap<>();
        Path path = Paths.get(PATH);
        try {
            Files.createDirectories(path.getParent());

            for(String line : Files.readAllLines(path)){
                String[] split = line.split("=");
                if(split.length < 2) continue;
                String potionId = split[0].trim();
                String translatedName = split[1].trim();
                Potion potion = Potion.getPotionFromResourceLocation(potionId);
                if(potion == null) continue;

                potToName.put(potion.getName(), translatedName);
            }
        }
        catch(IOException ignored) {}
        return potToName;
    }
}

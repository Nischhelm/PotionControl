package potioncontrol.config.descriptions;

import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
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
public class PotionTypeNamesReader {
    public static final String PATH = "config/potioncontrol/potiontypes/names.txt";

    public static void init(){
        Map<String, String> typeToName = readNames();
        ((LocaleAccessor) I18nAccessor.getLocale()).getProperties().putAll(typeToName);
        ((LanguageMapAccessor) I18nAccessor_translation.getLocalizedName()).getLanguageList().putAll(typeToName);
    }

    private static Map<String, String> readNames(){
        Map<String, String> typeToName = new HashMap<>();
        Path path = Paths.get(PATH);
        try {
            Files.createDirectories(path.getParent());

            for(String line : Files.readAllLines(path)){
                String[] split = line.split("=");
                if(split.length < 2) continue;
                String typeId = split[0].trim();
                String translatedName = split[1].trim();
                PotionType type = PotionType.REGISTRY.getObject(new ResourceLocation(typeId));
                if(type == PotionTypes.EMPTY && !typeId.equals("minecraft:empty")) continue;

                typeToName.put("potion.effect." + typeId, "Potion of " + translatedName);
                typeToName.put("splash_potion.effect." + typeId, "Splash Potion of " + translatedName);
                typeToName.put("lingering_potion.effect." + typeId, "Lingering Potion of " + translatedName);
                typeToName.put("tipped_arrow.effect." + typeId, "Arrow of " + translatedName);
            }
        }
        catch(IOException ignored) {}
        return typeToName;
    }
}
package potioncontrol.config.descriptions;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class DescriptionReader {
    public static final String PATH = "config/potioncontrol/descriptions.txt";

    private static final Map<ResourceLocation, String> injectedDescriptions = new HashMap<>();

    public static void readDescriptions() {
        Path path = Paths.get(PATH);
        try {
            Files.createDirectories(path.getParent());

            for(String line : Files.readAllLines(path)){
                String[] split = line.split("=");
                if(split.length < 2) continue;
                String potid = split[0].trim();
                String desc = split[1].trim();
                injectedDescriptions.put(new ResourceLocation(potid), desc);
            }
        }
        catch(IOException ignored) {}
    }

    @SideOnly(Side.CLIENT)
    public static String getDescriptionKey(Potion potion, String original){
        //prioritize new system using registry name instead of translation key
        return injectedDescriptions.getOrDefault(potion.getRegistryName(), original);
    }

    @SideOnly(Side.CLIENT)
    public static boolean hasDescriptionFor(Potion potion){
        return injectedDescriptions.containsKey(potion.getRegistryName());
    }
}

package potioncontrol.config.descriptions;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes a file with just the registry ids of all registered potions for users to fill
 */
public class EmptyPotionWriter {
    public static void write(String path){
        Path configPath = Paths.get(path);
        if(Files.exists(configPath)) return;

        //Write empty line per enchant
        List<String> lines = new ArrayList<>();
        Potion.REGISTRY.forEach(e -> {
            ResourceLocation potionid = e.getRegistryName();
            if(potionid == null) return;

            // Add the concrete class of the registered enchantment
            lines.add(potionid.getNamespace() + ":" + potionid.getPath() + "=");
        });

        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, lines);
        }
        catch(IOException ignored) {}
    }
}

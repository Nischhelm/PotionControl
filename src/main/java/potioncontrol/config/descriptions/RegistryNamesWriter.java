package potioncontrol.config.descriptions;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes a file with just the registry ids of all registered entries for users to fill
 */
public class RegistryNamesWriter {
    public static <T extends IForgeRegistryEntry<T>> void write(String path, IForgeRegistry<T> registry){
        Path configPath = Paths.get(path);
        if(Files.exists(configPath)) return;

        List<String> lines = new ArrayList<>();
        registry.forEach(e -> {
            ResourceLocation id = e.getRegistryName();
            if(id == null) return;

            lines.add(id.getNamespace() + ":" + id.getPath() + "=");
        });

        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, lines);
        }
        catch(IOException ignored) {}
    }
}

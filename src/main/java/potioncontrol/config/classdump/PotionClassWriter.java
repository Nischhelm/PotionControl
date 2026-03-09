package potioncontrol.config.classdump;

import potioncontrol.core.PotionControlPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PotionClassWriter {
    public static void postInit(){
        //Write current mappings potion_id; org.some.mod.has.PotionClass
        List<String> lines = new ArrayList<>();

        lines.add("!! This file is used by PotionControl internally and automatically. Not a config !!");
        lines.addAll(PotionControlPlugin.actuallyEarlyPotions);

        Path configPath = Paths.get(PotionClassReader.DUMP_PATH);
        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, lines);
        }
        catch(IOException ignored) {}
    }
}

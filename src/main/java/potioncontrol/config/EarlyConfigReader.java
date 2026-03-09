package potioncontrol.config;

import potioncontrol.util.ConfigRef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EarlyConfigReader {
    public static final String CONFIG_PATH = "config/potioncontrol.cfg";

    private static Set<String> blacklistConfig = null;

    private static List<String> lines = null;
    private static List<String> readLines(){
        if(lines == null){
            lines = new ArrayList<>();
            Path enchclasses_path = Paths.get(CONFIG_PATH);
            try {
                Files.createDirectories(enchclasses_path.getParent());
                lines.addAll(Files.readAllLines(enchclasses_path));
            } catch (IOException ignored) {}
        }
        return lines;
    }
    public static void clearLines(){
        lines = null;
    }

    public static Set<String> getClassBlacklistConfig(){
        if(blacklistConfig == null) {
            blacklistConfig = new HashSet<>();
            boolean isReading = false;
            for (String line : readLines()) {
                if (line.contains("S:\""+ ConfigRef.BLACKLIST_CONFIG_NAME +"\"")) {
                    isReading = true;
                    continue;
                }
                if (!isReading) continue; //unimportant lines
                if (line.contains(">")) break; //End of bracket

                blacklistConfig.add(line.trim());
            }
        }

        return blacklistConfig;
    }
}

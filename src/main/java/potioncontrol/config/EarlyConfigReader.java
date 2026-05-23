package potioncontrol.config;

import potioncontrol.PotionControl;
import potioncontrol.util.ConfigRef;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static File configFile = null;
    private static String configIntString = null;

    public static int getInt(String name, int defaultValue) {
        if (configFile == null) configFile = new File("config", PotionControl.MODID + ".cfg");

        if (configIntString == null) {
            if (configFile.exists() && configFile.isFile()) {
                try (Stream<String> stream = Files.lines(configFile.toPath())) {
                    configIntString = stream.filter(s -> s.trim().startsWith("I:")).collect(Collectors.joining());
                } catch (Exception ex) {
                    PotionControl.LOGGER.error("Failed to parse " + PotionControl.NAME + " config: " + ex);
                }
            } else configIntString = "";
        }

        if (configIntString.contains("I:\"" + name + "\"=")) {
            int index = configIntString.indexOf("I:\"" + name + "\"=");
            try {
                Matcher matcher = Pattern.compile("(\\d+)").matcher(configIntString.substring(index));
                matcher.find();
                return Integer.parseInt(matcher.group(1));
            } catch (Exception e) {
                PotionControl.LOGGER.error(PotionControl.NAME + ": Failed to parse int config "+ name + ", " + e);
                return 0;
            }
        }
        //If config is not generated yet or missing entries, we use the default value that will get written into it right after this
        else return defaultValue;
    }
}

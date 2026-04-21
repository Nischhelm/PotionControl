package potioncontrol.config.potioninfojsons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;
import potioncontrol.util.PotionInfo;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

public class PotionInfoWriter {
    public static String modifiablePath = "potions/loaded";
    public static final String MAIN_DIR = "config/potioncontrol/";

    public static void printLoaded(){
        if (!ConfigHandler.debug.printLoaded) return;
        writeAllCurrentPotionInfos(PotionInfo.getAll(), MAIN_DIR + modifiablePath);
    }

    public static void writeAllCurrentPotionInfos(Collection<PotionInfo> infos, String path) {
        // Write one file per potion into config/potioncontrol/out/modid/potionid.json
        try {
            File baseOut = new File(path);
            if (!baseOut.exists() && !baseOut.mkdirs()) {
                PotionControl.LOGGER.warn("Could not create directory: {}", baseOut.getPath());
            }

            // Clear any existing files so the directory reflects only the current run
            clearDirectoryContents(baseOut);

            for (PotionInfo info : infos) {
                writeSinglePotionInfo(info, baseOut);
            }
        } catch (Exception e) {
            PotionControl.LOGGER.warn("Writing potion infos failed!");
        }
    }

    public static void writeSinglePotionInfo(PotionInfo info, File baseOut) {
        String id = PotionInfo.getPotionId(info);
        String[] split = id.split(":");
        String modid = split[0];
        String potionid = split[1];

        File modDir = new File(baseOut, modid);
        if (!modDir.exists() && !modDir.mkdirs()) {
            PotionControl.LOGGER.warn("Could not create directory: {}", modDir.getPath());
        }

        File outFile = new File(modDir, potionid + ".json");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PotionInfo.class, new PotionInfoDeserialiser())
                .setPrettyPrinting()
                .create();
        try (Writer w = new OutputStreamWriter(Files.newOutputStream(outFile.toPath()), StandardCharsets.UTF_8)) {
            gson.toJson(info, PotionInfo.class, w);
        } catch (IOException e) {
            PotionControl.LOGGER.warn("Writing potion info for {} failed!", id);
        }
    }

    public static void clearDirectoryContents(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File f : children) {
            if (f.isDirectory()) {
                clearDirectoryContents(f);
            }
            if (!f.delete()) {
                PotionControl.LOGGER.warn("Could not delete {} while clearing {}", f.getPath(), dir.getPath());
            }
        }
    }
}

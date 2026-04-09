package potioncontrol.config.potiontypeinfojsons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;
import potioncontrol.util.PotionTypeInfo;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

public class PotionTypeInfoWriter {
    public static String modifiablePath = "potiontypes/loaded";
    public static final String MAIN_DIR = "config/potioncontrol/";

    public static void printLoaded(){
        if (!ConfigHandler.debug.printLoaded) return;
        writeAllCurrentPotionTypeInfos(PotionTypeInfo.getAll(), MAIN_DIR + modifiablePath);
    }

    public static void writeAllCurrentPotionTypeInfos(Collection<PotionTypeInfo> infos, String path) {
        // Write one file per potion into config/potioncontrol/potiontypes/out/modid/potionid.json
        try {
            File baseOut = new File(path);
            if (!baseOut.exists() && !baseOut.mkdirs()) {
                PotionControl.LOGGER.warn("Could not create directory: {}", baseOut.getPath());
            }

            // Clear any existing files so the directory reflects only the current run
            clearDirectoryContents(baseOut);

            for (PotionTypeInfo info : infos) {
                writeSinglePotionTypeInfo(info, baseOut);
            }
        } catch (Exception e) {
            PotionControl.LOGGER.warn("Writing loaded potion type infos failed!");
        }
    }

    public static void writeSinglePotionTypeInfo(PotionTypeInfo info, File baseOut) {
        String id = PotionTypeInfo.getTypeId(info);
        String[] split = id.split(":");
        String modid = split[0];
        String potionid = split[1];

        File modDir = new File(baseOut, modid);
        if (!modDir.exists() && !modDir.mkdirs()) {
            PotionControl.LOGGER.warn("Could not create directory: {}", modDir.getPath());
        }

        File outFile = new File(modDir, potionid + ".json");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PotionTypeInfo.class, new PotionTypeInfoDeserialiser())
                .setPrettyPrinting()
                .create();
        try (Writer w = new OutputStreamWriter(Files.newOutputStream(outFile.toPath()), StandardCharsets.UTF_8)) {
            gson.toJson(info, PotionTypeInfo.class, w);
        } catch (IOException e) {
            PotionControl.LOGGER.warn("Writing potion type info for {} failed!", id);
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

package potioncontrol.config.potiontypeinfojsons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.potion.Potion;
import potioncontrol.PotionControl;
import potioncontrol.mixin.accessor.PotionAccessor;
import potioncontrol.util.PotionTypeInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PotionTypeInfoConfigReader {
    public static final String MAIN_DIR = "config/potioncontrol/potiontypes/active";

    public static void preInit(){
        // Per-type files: config/potioncontrol/potiontypes/active/modid/typeid.json
        try {
            List<PotionTypeInfo> readInfos = readPerFileConfigs();
            PotionTypeInfo.registerAll(readInfos);
        } catch (Exception e){
            PotionControl.LOGGER.warn("Reading potion type configs failed!");
            e.printStackTrace(System.out);
        }
    }

    private static PotionTypeInfo readSingleWithGson(InputStream in) throws IOException {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(PotionTypeInfo.class, new PotionTypeInfoDeserialiser())
                    .create();
            return gson.fromJson(reader, PotionTypeInfo.class);
        }
    }

    private static List<PotionTypeInfo> readPerFileConfigs() {
        File base = new File(MAIN_DIR);
        if (!base.exists() && !base.mkdir()) return new ArrayList<>(); // nothing to read but create the folder

        List<PotionTypeInfo> infos = new ArrayList<>();
        readPerFileDirRecursive(base, infos);
        return infos;
    }

    private static List<PotionTypeInfo> readPerFileDirRecursive(File dir, List<PotionTypeInfo> infos)  {
        File[] children = dir.listFiles();
        if (children == null) return infos;
        for (File f : children) {
            if (f.isDirectory()) {
                infos.addAll(readPerFileDirRecursive(f, infos));
            } else if (f.isFile() && f.getName().endsWith(".json")) {
                try (InputStream in = Files.newInputStream(f.toPath())) {
                    PotionTypeInfo info = readSingleWithGson(in);
                    if (info == null)
                        PotionControl.LOGGER.warn("Skipping invalid potion json: {}", f.getPath());
                    else infos.add(info);
                } catch (Exception ex) {
                    PotionControl.LOGGER.warn("Failed reading potion json: {}", f.getPath());
                    ex.printStackTrace(System.out);
                }
            }
        }
        return infos;
    }

    //Done in post init to modify nothing yet
    public static void applyManualOverrides(){
//        for(PotionTypeInfo info : PotionTypeInfo.getAll()){
//        }
    }
}

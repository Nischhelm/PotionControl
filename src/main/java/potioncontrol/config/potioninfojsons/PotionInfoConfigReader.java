package potioncontrol.config.potioninfojsons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.potion.Potion;
import potioncontrol.PotionControl;
import potioncontrol.mixin.accessor.PotionAccessor;
import potioncontrol.util.PotionInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PotionInfoConfigReader {
    public static final String MAIN_DIR = "config/potioncontrol/potions";

    public static void preInit(){
        // Per-potion files: config/potioncontrol/potions/modid/potionid.json
        try {
            List<PotionInfo> readInfos = readPerFileConfigs();
            PotionInfo.registerAll(readInfos);
        } catch (Exception e){
            PotionControl.LOGGER.warn("Reading potion configs failed!");
            e.printStackTrace(System.out);
        }
    }

    public static List<PotionInfo> readListWithGson(InputStream in) throws IOException {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(PotionInfo.class, new PotionInfoDeserialiser())
                    .create();
            java.lang.reflect.Type listType = new TypeToken<List<PotionInfo>>(){}.getType();
            List<PotionInfo> infos = gson.fromJson(reader, listType);
            return infos == null ? new ArrayList<>() : infos;
        }
    }

    private static PotionInfo readSingleWithGson(InputStream in) throws IOException {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(PotionInfo.class, new PotionInfoDeserialiser())
                    .create();
            return gson.fromJson(reader, PotionInfo.class);
        }
    }

    private static List<PotionInfo> readPerFileConfigs() {
        File base = new File(MAIN_DIR);
        if (!base.exists() && !base.mkdir()) return new ArrayList<>(); // nothing to read but create the folder

        List<PotionInfo> infos = new ArrayList<>();
        readPerFileDirRecursive(base, infos);
        return infos;
    }

    private static List<PotionInfo> readPerFileDirRecursive(File dir, List<PotionInfo> infos)  {
        File[] children = dir.listFiles();
        if (children == null) return infos;
        for (File f : children) {
            if (f.isDirectory()) {
                infos.addAll(readPerFileDirRecursive(f, infos));
            } else if (f.isFile() && f.getName().endsWith(".json")) {
                try (InputStream in = Files.newInputStream(f.toPath())) {
                    PotionInfo info = readSingleWithGson(in);
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

    //Done in post init to modify the final fields ench.rarity and ench.applicableEquipmentTypes
    public static void applyManualOverrides(){
        for(PotionInfo info : PotionInfo.getAll()){
            if(info.attributeModifierMap == null) continue;
            Potion potion = PotionInfo.getPotionObject(info);
            if(!(potion instanceof PotionAccessor)) return;
            ((PotionAccessor) potion).pc_getAttributeModifierMap().entrySet().removeIf(entry -> entry.getKey() instanceof BaseAttribute); //keeps theoretical iattributes that arent BaseAttribute
            ((PotionAccessor) potion).pc_getAttributeModifierMap().putAll(info.attributeModifierMap);
        }
    }
}

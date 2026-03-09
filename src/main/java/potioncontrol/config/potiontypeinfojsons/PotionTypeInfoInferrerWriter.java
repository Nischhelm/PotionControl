package potioncontrol.config.potiontypeinfojsons;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;
import potioncontrol.util.ConfigRef;
import potioncontrol.util.PotionTypeInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds PotionTypeInfo by inspecting PotionType objects and writes those to file as an approximate start point.
 */
public class PotionTypeInfoInferrerWriter {
    public static final String MAIN_DIR = "config/potioncontrol/potiontypes/inferred-inactive";

    public static void printInferred(){
        PotionTypeInfoWriter.clearDirectoryContents(new File(MAIN_DIR));
        List<PotionTypeInfo> infos = inferInfoForAllRegisteredPotions();
        PotionTypeInfoWriter.writeAllCurrentPotionTypeInfos(infos, MAIN_DIR);

        //Create empty folders for each mod in /potiontypes/
        File baseOut = new File(PotionTypeInfoConfigReader.MAIN_DIR);
        if (!baseOut.exists() && !baseOut.mkdirs())
            PotionControl.LOGGER.warn("Could not create directory: {}", baseOut.getPath());
        for(String modid : infos.stream().map(info -> info.modId).collect(Collectors.toSet())){ //make one empty folder per mod
            File modDir = new File(baseOut, modid);
            if (!modDir.exists() && !modDir.mkdirs())
                PotionControl.LOGGER.warn("Could not create directory: {}", modDir.getPath());
        }

        PotionControl.CONFIG.get("general.first setup", ConfigRef.DO_INFER_CONFIG_NAME, ConfigHandler.dev.printInferred).set(false);
        ConfigHandler.dev.printInferred = false;
        PotionControl.configNeedsSaving = true;
    }

    public static List<PotionTypeInfo> inferInfoForAllRegisteredPotions() {
        List<PotionTypeInfo> out = new ArrayList<>();
        for (PotionType type : PotionType.REGISTRY) {
            PotionTypeInfo info = inferInfoForPotionType(type);
            if (info != null) out.add(info);
        }
        return out;
    }

    public static @Nullable PotionTypeInfo inferInfoForPotionType(PotionType type) {
        if (type == null || type.getRegistryName() == null) return null;

        PotionTypeInfo info = new PotionTypeInfo(type.getRegistryName().toString());

        info.effects = type.getEffects();

        return info;
    }
}

package potioncontrol.config.potioninfojsons;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.TextFormatting;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;
import potioncontrol.mixin.accessor.IPotionAccessor;
import potioncontrol.util.ConfigRef;
import potioncontrol.util.PotionInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds PotionInfo by inspecting Potion objects and writes those to file as an approximate start point.
 */
public class PotionInfoInferrerWriter {
    public static final String MAIN_DIR = "config/potioncontrol/inferred-inactive";

    public static void printInferred(){
        PotionInfoWriter.clearDirectoryContents(new File(MAIN_DIR));
        List<PotionInfo> infos = inferInfoForAllRegisteredPotions();
        PotionInfoWriter.writeAllCurrentPotionInfos(infos, MAIN_DIR);

        //Create empty folders for each mod in /potions/
        File baseOut = new File(PotionInfoConfigReader.MAIN_DIR);
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

    public static List<PotionInfo> inferInfoForAllRegisteredPotions() {
        List<PotionInfo> out = new ArrayList<>();
        for (Potion potion : Potion.REGISTRY) {
            PotionInfo info = inferInfoForPotion(potion);
            if (info != null) out.add(info);
        }
        return out;
    }

    public static @Nullable PotionInfo inferInfoForPotion(Potion potion) {
        if (potion == null || potion.getRegistryName() == null) return null;

        PotionInfo info = new PotionInfo(potion.getRegistryName().toString());

        info.setBeneficial(!potion.isBadEffect());

        info.setLiquidColor(potion.getLiquidColor());

        Map<IAttribute, AttributeModifier> map = ((IPotionAccessor) potion).pc_getAttributeModifierMap();
        info.setAttributeModifierMap(map.isEmpty() ? null : map);

        TextFormatting fmt = probeDisplayColor(potion); // Display color (only for unusual, not for default none or RED if curse)
        if (!(!info.isBeneficial && fmt == TextFormatting.RED)) info.setTextDisplayColor(fmt);

        return info;
    }

    private static @Nullable TextFormatting probeDisplayColor(Potion potion) {
        try {
            String name = "";//potion.getTranslatedName(1); //TODO
            // Search the start of the name for a textformatting flag
            for (TextFormatting fmt : TextFormatting.values()) if (name.startsWith(fmt.toString())) return fmt;
        } catch (Throwable ignored) {
            PotionControl.LOGGER.warn("Failed to probe display color for {}", potion.getRegistryName());
        }
        return null;
    }
}

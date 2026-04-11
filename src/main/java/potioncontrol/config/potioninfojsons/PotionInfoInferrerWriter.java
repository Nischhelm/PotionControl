package potioncontrol.config.potioninfojsons;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.TextFormatting;
import potioncontrol.PotionControl;
import potioncontrol.mixin.accessor.PotionAccessor;
import potioncontrol.util.PotionInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builds PotionInfo by inspecting Potion objects and writes those to file as an approximate start point.
 */
public class PotionInfoInferrerWriter {
    public static final String MAIN_DIR = "config/potioncontrol/potions/inferred-inactive";

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

//        PotionControl.CONFIG.get("general.first setup", ConfigRef.DO_INFER_CONFIG_NAME, ConfigHandler.dev.printInferred).set(false);
//        ConfigHandler.dev.printInferred = false;
//        PotionControl.configNeedsSaving = true;
    }

    public static List<PotionInfo> inferInfoForAllRegisteredPotions() {
        List<PotionInfo> out = new ArrayList<>();
        for (Potion potion : Potion.REGISTRY) {
            PotionInfo info = inferInfoForPotion(potion);
            if (info != null) out.add(info);
        }
        return out;
    }

    public static final ItemStack milk = new ItemStack(Items.MILK_BUCKET);

    public static @Nullable PotionInfo inferInfoForPotion(Potion potion) {
        if (potion == null || potion.getRegistryName() == null) return null;

        PotionInfo info = new PotionInfo(potion.getRegistryName().toString());

        info.setBeneficial(!potion.isBadEffect());

        info.setLiquidColor(potion.getLiquidColor());

        List<ItemStack> curativeItems = potion.getCurativeItems();
        List<ItemStack> savedCurativeItems = new ArrayList<>();
        boolean hasMilk = false;
        for(ItemStack stack : curativeItems){
            if(stack.isItemEqual(milk)) hasMilk = true;
            else savedCurativeItems.add(stack.copy());
        }
        if(!savedCurativeItems.isEmpty())
            info.curativeItems = savedCurativeItems;
        if(!hasMilk) info.milkRemovable = false;

        Map<IAttribute, AttributeModifier> map = ((PotionAccessor) potion).pc_getAttributeModifierMap();
        info.setAttributeModifierMap(map.isEmpty() ? null : map);

        List<TextFormatting> fmts = probeDisplayColor(potion);
        if(!fmts.isEmpty())
            info.setTextDisplayColors(fmts);

        return info;
    }

    private static final Map<String, TextFormatting> textFormattingByControlString = Arrays.stream(TextFormatting.values()).collect(Collectors.toMap(
            TextFormatting::toString,
            Function.identity()
    ));

    private static List<TextFormatting> probeDisplayColor(Potion potion) {
        List<TextFormatting> fmts = new ArrayList<>();
        try {
            String name = I18n.format(potion.getName());
            // Search the start of the name for textformatting flags
            while(name.startsWith("§")){
                fmts.add(textFormattingByControlString.get(name.substring(0,2)));
                name = name.substring(2);
            }
            return fmts;
        } catch (Throwable ignored) {
            PotionControl.LOGGER.warn("Failed to probe display color for {}", potion.getRegistryName());
        }
        return fmts;
    }
}

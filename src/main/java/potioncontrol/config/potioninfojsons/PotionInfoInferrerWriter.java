package potioncontrol.config.potioninfojsons;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.text.TextFormatting;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;
import potioncontrol.mixin.accessor.PotionAccessor;
import potioncontrol.util.ConfigRef;
import potioncontrol.util.PotionInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
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

        PotionControl.CONFIG.get("general.first setup", ConfigRef.DO_INFER_CONFIG_NAME_POTION, ConfigHandler.dev.printInferredPotions).set(false);
        ConfigHandler.dev.printInferredPotions = false;
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

        if(potion.isInstant()) info.setInstant(true);

        inferRepeatingProperties(potion, info);

        List<Integer> beaconLevels = new ArrayList<>();
        for(int lvl = 0; lvl < TileEntityBeacon.EFFECTS_LIST.length; lvl++)
            for(Potion pot : TileEntityBeacon.EFFECTS_LIST[lvl])
                if(pot == potion) beaconLevels.add(lvl);
        if(!beaconLevels.isEmpty())
            info.beaconLevels = beaconLevels;

        Map<IAttribute, AttributeModifier> map = ((PotionAccessor) potion).pc_getAttributeModifierMap();
        info.setAttributeModifierMap(map.isEmpty() ? null : map);

        List<TextFormatting> fmts = probeDisplayColor(potion);
        if(!fmts.isEmpty())
            info.setTextDisplayColors(fmts);

        return info;
    }

    /**
     * shadertoy:
     * void mainImage( out vec4 fragColor, in vec2 fragCoord )
     * {
     *     // Normalized pixel coordinates (from 0 to 1)
     *     vec2 uv = fragCoord/iResolution.y;
     *
     *     int cycle = 10;
     *     int cycleByAmp = 1;
     *
     *     int duration = int(uv.x*500.);
     *     int amplifier = int(uv.y*7.);
     *
     *     int cycleTot = cycle >> (cycleByAmp * amplifier);
     *
     *     float isReady = cycleTot > 0 ? float((duration % cycleTot) == 0) : 0.5;
     *
     *     // Output to screen
     *     fragColor = vec4(isReady);
     * }
     */
    private static void inferRepeatingProperties(Potion potion, PotionInfo info) {
        int maxDur = 400, maxAmp = 5;
        int period, periodAmpMod;
        boolean isRepeating = false;
        Map<Integer, Integer> cycleAtAmp = new HashMap<>();
        for(int amp = 0; amp < maxAmp; amp++) {
            int lastReady = 0;
            List<Integer> dists = new ArrayList<>();
            for(int durLeft = 0; durLeft < maxDur; durLeft++){
                boolean isReady = potion.isReady(durLeft, amp);
                if(isReady){
                    dists.add(durLeft - lastReady);
                    lastReady = durLeft;
                    if(!isRepeating) isRepeating = true; //if it ever isReady we count it as repeating
                }
            }

            //true
            //durLeft >= 1
            //amp > 1 && ...
            if(dists.size() > maxDur * 0.5){

            }
            else if(dists.size() >= 3 && dists.size() <= maxDur * 0.5) { //needs to have at least three hits, and not almost all of them
                dists.remove(0); //first doesnt count correctly
                int mean = dists.stream().mapToInt(Integer::intValue).sum() / dists.size();
                int sd = (int) (Math.sqrt(dists.stream().mapToInt(Integer::intValue).map(val -> (val-mean)*(val-mean)).sum()) / dists.size());

                if(sd == 0) //all dists the same
                    cycleAtAmp.put(amp, mean);

                //TODO: what if it isnt
            }
        }

        if(!cycleAtAmp.containsKey(0)) return; //TODO set repeating/non repeating?

        //assume
        // durLeft % cycle == 0 -> all measured cycle durations the same
        if(cycleAtAmp.entrySet().stream().allMatch(entry -> entry.getValue().equals(cycleAtAmp.get(0)))) {
            period = cycleAtAmp.get(0);
            periodAmpMod = 0;

            info.setRepeating(true, period, periodAmpMod);
        }

        // cycle / 2 ^ cycleAtAmp_i = cycleTotal_i

        //or assume
        // durLeft % (cycle >> cycleAtAmp) == 0
        if(cycleAtAmp.size() > 1) {
            period = cycleAtAmp.get(0);

            List<Double> multis = new ArrayList<>();
            for(Map.Entry<Integer, Integer> entry : cycleAtAmp.entrySet()) {
                if(entry.getKey() == 0) continue;
                double dist = (Math.log(period) - Math.log(entry.getValue()))/Math.log(2) / entry.getKey();
                multis.add(dist);
            }
            double mean = multis.stream().mapToDouble(Double::doubleValue).sum() / multis.size();

            periodAmpMod = (int) Math.round(mean);

            info.setRepeating(true, period, periodAmpMod);
        }

        if(!isRepeating) info.setNotRepeating();
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

package potioncontrol.util;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import potioncontrol.core.PotionDummy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PotionInfo {
    // -------- STATIC LOOKUP --------

    private static final Map<String, PotionInfo> byPotionId = new HashMap<>();
    private static final Map<Potion, PotionInfo> byPotionObj = new HashMap<>();
    private static final Map<PotionInfo, Potion> toPotionObj = new HashMap<>();

    public static @Nullable PotionInfo get(String potionid){
        return byPotionId.get(potionid);
    }

    public static @Nullable PotionInfo get(Potion potion) {
        PotionInfo info = byPotionObj.get(potion);
        if(info == null){
            info = potion.getRegistryName() == null ? null : get(potion.getRegistryName().toString());
            if(info != null){
                byPotionObj.put(potion, info);
                toPotionObj.put(info, potion);
            }
        }
        return info;
    }

    public static @Nullable Potion getPotionObject(PotionInfo info){
        return toPotionObj.get(info);
    }
    public static String getPotionId(PotionInfo info){
        return info.id;
    }

    public static Collection<PotionInfo> getAll(){
        return byPotionId.values();
    }

    public static PotionInfo register(PotionInfo info){
        byPotionId.put(info.id, info);
        return info;
    }

    public static void registerAll(List<PotionInfo> infos){
        infos.forEach(PotionInfo::register);
    }
    // -------- PROPERTIES --------

    @SerializedName("id")
    public String id;
    public String modId;
    public String potionId;

    public boolean overwritesIsBeneficial = false;
    @SerializedName("isBeneficial")
    public boolean isBeneficial;

    public boolean overwritesIsInstant = false;
    @SerializedName("isInstant")
    public boolean isInstant;

    public boolean overwritesIsRepeating = false;
    @SerializedName("isRepeating")
    public boolean isRepeating;
    @SerializedName("repeatingPeriod")
    public int repeatingPeriod;
    @SerializedName("periodAmpModifier")
    public int periodAmpModifier;

    @SerializedName("milkRemovable")
    public boolean milkRemovable = true;

    @SerializedName("curativeItems")
    public List<ItemStack> curativeItems = null;

    @SerializedName("maxLevel")
    public int maxLevel = -1; //-1 means don't read, don't use
    @SerializedName("maxDuration")
    public int maxDur = -1;

    @SerializedName("liquidColor")
    public String liquidColorHex = null;

    @SerializedName("displayColor")
    public List<TextFormatting> displayColors = null;

    @SerializedName("attributeModifiers")
    public Map<IAttribute, AttributeModifier> attributeModifierMap = null;

    @SerializedName("prioritisesDuration")
    public boolean prioritisesDuration = false;

    //TODO: incompats/auto removal, sources
    //TODO: modify beacon effects

    //-------- CONSTRUCTOR --------

    public PotionInfo(@Nonnull String id) {
        this.id = id;
        String[] split = id.split(":");
        this.modId = split[0].trim();
        this.potionId = split[1].trim(); //unsafe
    }

    public PotionInfo(@Nonnull String modid, @Nonnull String potionid) {
        this.id = modid + ":" + potionid;
        this.modId = modid;
        this.potionId = potionid;
    }

    public Potion create() {
        return new PotionDummy(this);
    }

    //-------- SETTERS --------

    public void setLiquidColor(int color){
        this.liquidColorHex = "#" + Integer.toHexString(color).toUpperCase();
    }

    public void setLiquidColor(String color){
        this.liquidColorHex = color;
    }

    public void setTextDisplayColors(List<TextFormatting> displayColor) {
        this.displayColors = displayColor;
    }

    public void setBeneficial(boolean isBeneficial) {
        this.isBeneficial = isBeneficial;
        this.overwritesIsBeneficial = true;
    }

    public void setInstant(boolean isInstant) {
        this.isInstant = isInstant;
        this.overwritesIsInstant = true;
    }

    public void setNotRepeating() {
        this.isRepeating = false;
        this.overwritesIsRepeating = true;
    }

    public void setRepeating(boolean isRepeating, int period, int periodAmpMod) {
        this.isRepeating = isRepeating;
        this.overwritesIsRepeating = true;
        this.repeatingPeriod = period;
        this.periodAmpModifier = periodAmpMod;
    }

    public void setAttributeModifierMap(Map<IAttribute, AttributeModifier> attributeModifierMap) {
        this.attributeModifierMap = attributeModifierMap;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDur = maxDuration;
    }

    // -------- GETTERS --------

    @SideOnly(Side.CLIENT)
    public String getTranslatedName(Potion potion, int amp){
        String format = "";
        String potTranslation = I18n.format(potion.getName());
        if(this.displayColors != null) {
            format = this.displayColors.stream().map(TextFormatting::toString).collect(Collectors.joining());
            while(potTranslation.startsWith("§"))
                potTranslation = potTranslation.substring(2); //TODO: might be better to store in lang file, but annoying if player changes language
        }
        String text = format + potTranslation;

        if(amp > 0 && amp < 10)
            text += " " + I18n.format("enchantment.level."+(amp+1));
        else if(amp != 0) text = text + " " + (amp + 1); //lvl 11+ or negative lvls

        return text;
    }

    public int getLiquidColor(){
        return Integer.decode(this.liquidColorHex);
    }

    public boolean getIsReady(int durationLeft, int amplifier) {
        int cycleTotal = this.repeatingPeriod >> (this.periodAmpModifier * amplifier);
        return cycleTotal <= 0 || durationLeft % cycleTotal == 0;
    }
}

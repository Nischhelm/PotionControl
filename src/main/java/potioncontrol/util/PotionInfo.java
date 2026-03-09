package potioncontrol.util;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @SerializedName("liquidColor")
    public String liquidColorHex = null;

    @SerializedName("displayColor") //TODO: implement
    public TextFormatting displayColor = null;

    @SerializedName("attributeModifiers")
    public Map<IAttribute, AttributeModifier> attributeModifierMap = null;

    //TODO: instant, performEffect, isReady, incompats/auto removal, sources, description
    //TODO: maxlvl, maxduration
    //TODO: connected types, curative items
    //TODO: default allow extra strong/extra long or stronglong, brewtime
    //TODO: modcompat for inspirations cauldron brewing, rustic alchemy

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

    //-------- SETTERS --------

    public void setLiquidColor(int color){
        this.liquidColorHex = Integer.toHexString(color).toUpperCase();
    }

    public void setLiquidColor(String color){
        this.liquidColorHex = color;
    }

    public void setTextDisplayColor(TextFormatting displayColor) {
        this.displayColor = displayColor;
    }

    public void setBeneficial(boolean isBeneficial) {
        this.isBeneficial = isBeneficial;
        this.overwritesIsBeneficial = true;
    }

    public void setAttributeModifierMap(Map<IAttribute, AttributeModifier> attributeModifierMap) {
        this.attributeModifierMap = attributeModifierMap;
    }

    // -------- GETTERS --------

    @SideOnly(Side.CLIENT) @SuppressWarnings("deprecation")
    public String getTranslatedName(Potion ench, int lvl){
        String s = I18n.translateToLocal(ench.getName());

        if (this.displayColor != null){
            s = this.displayColor + s;
        } else if (!this.isBeneficial) {
            s = TextFormatting.RED + s;
        }

        return s + " " + I18n.translateToLocal("potion.level." + lvl);
    }

    public int getLiquidColor(){
        return Integer.decode("#"+this.liquidColorHex);
    }

    public Map<IAttribute, AttributeModifier> getAttributeModifierMap(){
        return this.attributeModifierMap;
    }
}

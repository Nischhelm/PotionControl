package potioncontrol.util;

import com.google.gson.annotations.SerializedName;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import potioncontrol.util.brewing.BrewRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PotionTypeInfo {
    // -------- STATIC LOOKUP --------

    private static final Map<String, PotionTypeInfo> byTypeId = new HashMap<>();
    private static final Map<PotionType, PotionTypeInfo> byTypeObj = new HashMap<>();
    private static final Map<PotionTypeInfo, PotionType> toTypeObj = new HashMap<>();

    public static @Nullable PotionTypeInfo get(String typeId){
        return byTypeId.get(typeId);
    }

    public static @Nullable PotionTypeInfo get(PotionType type) {
        PotionTypeInfo info = byTypeObj.get(type);
        if(info == null){
            info = type.getRegistryName() == null ? null : get(type.getRegistryName().toString());
            if(info != null){
                byTypeObj.put(type, info);
                toTypeObj.put(info, type);
            }
        }
        return info;
    }

    public static @Nullable PotionType getPotionTypeObject(PotionTypeInfo info){
        return toTypeObj.get(info);
    }
    public static String getTypeId(PotionTypeInfo info){
        return info.id;
    }

    public static Collection<PotionTypeInfo> getAll(){
        return byTypeId.values();
    }

    public static PotionTypeInfo register(PotionTypeInfo info){
        byTypeId.put(info.id, info);
        //TODO: option to actually register the type to REGISTRY
        return info;
    }

    public static void registerAll(List<PotionTypeInfo> infos){
        infos.forEach(PotionTypeInfo::register);
    }
    // -------- PROPERTIES --------

    @SerializedName("id")
    public String id;
    public String modId;
    public String potionId;

    @SerializedName("effects")
    public List<PotionEffect> effects = null;

    @SerializedName("brews_from")
    public List<BrewRecipe> brewsFrom = null;
    @SerializedName("brews_to")
    public List<BrewRecipe> brewsTo = null;

    @SerializedName("tipped_arrow_duration")
    public int tippedDuration;
    public boolean overwritesTippedDuration;

    //TODO: default allow extra strong/extra long or stronglong
    //TODO: modcompat for inspirations cauldron brewing, rustic alchemy
    //TODO: recipes with brewtime

    //-------- CONSTRUCTOR --------

    public PotionTypeInfo(@Nonnull String id) {
        this.id = id;
        String[] split = id.split(":");
        this.modId = split[0].trim();
        this.potionId = split[1].trim(); //unsafe
    }

    public PotionTypeInfo(@Nonnull String modid, @Nonnull String potionid) {
        this.id = modid + ":" + potionid;
        this.modId = modid;
        this.potionId = potionid;
    }

    //-------- SETTERS --------

    public void setTippedDuration(int duration){
        this.tippedDuration = duration;
        this.overwritesTippedDuration = true;
    }

    // -------- GETTERS --------

}

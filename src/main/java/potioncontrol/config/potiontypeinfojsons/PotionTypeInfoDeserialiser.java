package potioncontrol.config.potiontypeinfojsons;

import com.google.gson.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;
import potioncontrol.config.folders.FirstSetupConfig;
import potioncontrol.util.BrewRecipeUtil;
import potioncontrol.util.PotionTypeInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PotionTypeInfoDeserialiser implements JsonDeserializer<PotionTypeInfo>, JsonSerializer<PotionTypeInfo> {
    @Override
    public PotionTypeInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //Read from JSON to PotionTypeInfo
        if (!json.isJsonObject()) return null;
        JsonObject jsonObj = json.getAsJsonObject();

        String id = getAsString(jsonObj, "id");
        if (id == null) return null;

        PotionTypeInfo info = new PotionTypeInfo(id);

        if(jsonObj.has("effects")) {
            JsonArray arr = jsonObj.getAsJsonArray("effects");
            List<PotionEffect> effects = new ArrayList<>();
            for(JsonElement el : arr){
                JsonObject obj = el.getAsJsonObject();

                String potionId = obj.get("potion").getAsString();
                Potion potion = Potion.getPotionFromResourceLocation(potionId);
                if(potion == null){
                    PotionControl.LOGGER.warn("Unable to find potion {} for effect of potion type {}, skipping", potionId, id);
                    continue;
                }

                int amp = 0;
                if(obj.has("amplifier")) amp = obj.get("amplifier").getAsInt();
                else if(obj.has("level")) amp = obj.get("level").getAsInt() - 1;

                int dur = 0;
                if(obj.has("duration_tick")) dur = obj.get("duration_tick").getAsInt();
                else if(obj.has("duration_sec")) dur = obj.get("duration_sec").getAsInt() * 20;
                else if(obj.has("duration_min")) dur = (int) (obj.get("duration_min").getAsFloat() * 20 * 60);

                boolean ambient = false;
                if(obj.has("is_beacon_style"))
                    ambient = obj.get("is_beacon_style").getAsBoolean();

                boolean particles = true;
                if(obj.has("shows_particles"))
                    particles = obj.get("shows_particles").getAsBoolean();

                effects.add(new PotionEffect(potion, dur, amp, ambient, particles));
            }
            if(!effects.isEmpty()) info.effects = effects;
        }

        //TODO: read in brewing recipes

        if(jsonObj.has("tipped_arrow_duration"))
            info.setTippedDuration(jsonObj.get("tipped_arrow_duration").getAsInt());

        return info;
    }

    @Override
    public JsonElement serialize(PotionTypeInfo info, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
        //Write from PotionTypeInfo to JSON
        JsonObject o = new JsonObject();

        // id
        o.addProperty("id", PotionTypeInfo.getTypeId(info));

        // curative items
        if (info.effects != null) {
            JsonArray effects = new JsonArray();
            for(PotionEffect effect : info.effects) {
                if(effect.getPotion().getRegistryName() == null) continue;
                JsonObject obj = new JsonObject();
                obj.addProperty("potion", effect.getPotion().getRegistryName().toString());
                if(ConfigHandler.dev.asAmplifier) obj.addProperty("amplifier", effect.getAmplifier());
                else obj.addProperty("level", effect.getAmplifier() + 1);

                switch (ConfigHandler.dev.durationScale){
                    case TICK: obj.addProperty("duration_tick", effect.getDuration()); break;
                    case SEC: obj.addProperty("duration_sec", effect.getDuration() / 20); break;
                    case MIN: obj.addProperty("duration_min", (float) effect.getDuration() / 20F / 60F); break;
                }

                obj.addProperty("is_beacon_style", effect.getIsAmbient()); //ambient is for Beacon Effects to not flicker when running out and have blue outline
                obj.addProperty("shows_particles", effect.doesShowParticles());
                effects.add(obj);
            }
            o.add("effects", effects);
        }

        if(info.brewsFrom != null && (ConfigHandler.dev.recipeDirection == FirstSetupConfig.EnumBrewRecipeDirection.FROM || ConfigHandler.dev.recipeDirection == FirstSetupConfig.EnumBrewRecipeDirection.BOTH)) {
            JsonArray brewsFrom = new JsonArray();
            for(BrewRecipeUtil.BrewRecipe recipe : info.brewsFrom) {
                JsonObject obj = new JsonObject();
                if(recipe.in.getRegistryName() == null) continue;
                obj.addProperty("from", recipe.in.getRegistryName().toString());

                ResourceLocation loc = recipe.reagent.getItem().getRegistryName();
                if(loc == null) continue;
                int meta = recipe.reagent.getMetadata();
                NBTTagCompound tag = recipe.reagent.getTagCompound();


                if(meta == 0 && tag == null){
                    obj.addProperty("reagent", loc.toString());
                } else {
                    JsonObject reagent = new JsonObject();
                    reagent.addProperty("item", loc.toString());
                    if (meta != 0) reagent.addProperty("meta", meta);
                    if (tag != null) reagent.addProperty("tag", tag.toString());
                    obj.add("reagent", reagent);
                }

                brewsFrom.add(obj);
            }
            o.add("brews_from", brewsFrom);
        }

        if(info.brewsTo != null && (ConfigHandler.dev.recipeDirection == FirstSetupConfig.EnumBrewRecipeDirection.TO || ConfigHandler.dev.recipeDirection == FirstSetupConfig.EnumBrewRecipeDirection.BOTH)) {
            JsonArray brewsTo = new JsonArray();
            for(BrewRecipeUtil.BrewRecipe recipe : info.brewsTo) {
                JsonObject obj = new JsonObject();

                ResourceLocation loc = recipe.reagent.getItem().getRegistryName();
                if(loc == null || recipe.out.getRegistryName() == null) continue;
                int meta = recipe.reagent.getMetadata();
                NBTTagCompound tag = recipe.reagent.getTagCompound();

                if(meta == 0 && tag == null){
                    obj.addProperty("reagent", loc.toString());
                } else {
                    JsonObject reagent = new JsonObject();
                    reagent.addProperty("item", loc.toString());
                    if (meta != 0) reagent.addProperty("meta", meta);
                    if (tag != null) reagent.addProperty("tag", tag.toString());
                    obj.add("reagent", reagent);
                }

                obj.addProperty("to", recipe.out.getRegistryName().toString());
                brewsTo.add(obj);
            }
            o.add("brews_to", brewsTo);
        }

        if(info.overwritesTippedDuration)
            o.addProperty("tipped_arrow_duration", info.tippedDuration);

        return o;
    }

    private static String getAsString(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : null;
    }

    private static Boolean getAsBoolean(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsBoolean() : null;
    }

    private static Integer getAsInt(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsInt() : null;
    }

    private static Float getAsFloat(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsFloat() : null;
    }

    private static List<String> readStringList(JsonObject o, String key) {
        if (!o.has(key) || o.get(key).isJsonNull()) return null;
        JsonElement e = o.get(key);
        if (!e.isJsonArray()) return null;
        List<String> out = new ArrayList<>();
        for (JsonElement je : e.getAsJsonArray()) {
            if (je.isJsonPrimitive()) out.add(je.getAsString());
        }
        return out;
    }

    private static <E extends Enum<E>> List<E> readEnumList(JsonObject o, String key, Class<E> enumCls) {
        if (!o.has(key) || o.get(key).isJsonNull()) return null;
        JsonElement e = o.get(key);
        if (!e.isJsonArray()) return null;
        List<E> out = new ArrayList<>();
        for (JsonElement je : e.getAsJsonArray()) {
            if (je.isJsonPrimitive()) {
                out.add(Enum.valueOf(enumCls, je.getAsString()));
            }
        }
        return out;
    }
}

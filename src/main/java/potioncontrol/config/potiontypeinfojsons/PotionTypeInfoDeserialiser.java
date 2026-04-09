package potioncontrol.config.potiontypeinfojsons;

import com.google.gson.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import potioncontrol.PotionControl;
import potioncontrol.util.PotionTypeInfo;

import java.lang.reflect.Type;
import java.util.*;

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
                if(obj.has("amplifier")) amp = obj.get("amplifier").getAsInt() ;
                else if(obj.has("level")) amp = obj.get("level").getAsInt() - 1;

                int dur = 0;
                if(obj.has("duration")) dur = obj.get("duration").getAsInt() * 20;

//                boolean ambient = false;
//                if(obj.has("ambient"))
//                    ambient = obj.get("ambient").getAsBoolean();

                boolean particles = true;
                if(obj.has("particles"))
                    particles = obj.get("particles").getAsBoolean();

                effects.add(new PotionEffect(potion, dur, amp, false, particles));
            }
            if(!effects.isEmpty()) info.effects = effects;
        }

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
                if(effect.getAmplifier() != 0)
                    obj.addProperty("level", effect.getAmplifier() + 1);
                if(effect.getDuration() != 0)
                    obj.addProperty("duration", effect.getDuration() / 20);
//                if(effect.getIsAmbient()) //ambient is for Beacon Effects to not flicker when running out and have blue outline
//                    obj.addProperty("ambient", true);
                if(!effect.doesShowParticles())
                    obj.addProperty("particles", false);
                effects.add(obj);
            }
            o.add("effects", effects);
        }

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

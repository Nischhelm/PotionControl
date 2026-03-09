package potioncontrol.config.potioninfojsons;

import com.google.gson.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.text.TextFormatting;
import potioncontrol.PotionControl;
import potioncontrol.util.BaseAttributeRegistry;
import potioncontrol.util.PotionInfo;

import java.util.*;

public class PotionInfoDeserialiser implements JsonDeserializer<PotionInfo>, JsonSerializer<PotionInfo> {
    @Override
    public PotionInfo deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //Read from JSON to PotionInfo
        if (!json.isJsonObject()) return null;
        JsonObject jsonObj = json.getAsJsonObject();

        String id = getAsString(jsonObj, "id");
        if (id == null) return null;

        PotionInfo info = new PotionInfo(id);

        // booleans (with legacy aliases)
        Boolean isBeneficial = getAsBoolean(jsonObj, "isGood");
        if (isBeneficial != null) info.setBeneficial(isBeneficial);

        // displayColor
        String colorStr = getAsString(jsonObj, "displayColor");
        if (colorStr != null) info.setTextDisplayColor(TextFormatting.valueOf(colorStr));

        // liquidColor
        String liquidColorHex = getAsString(jsonObj, "liquidColor");
        if (liquidColorHex != null) info.setLiquidColor(liquidColorHex);

        // attribute modifiers
        if (jsonObj.has("attributeModifiers")) {
            JsonArray attributeModifiers = jsonObj.getAsJsonArray("attributeModifiers");
            Map<IAttribute, AttributeModifier> map = new HashMap<>();
            for (JsonElement attributeModifier : attributeModifiers) {
                JsonObject obj = (JsonObject) attributeModifier;
                BaseAttribute attribute = BaseAttributeRegistry.get(obj.get("attribute").getAsString());
                if(attribute == null){
                    PotionControl.LOGGER.warn("Unknown attribute {} for potion {}, skipping",  obj.get("attribute").getAsString(), id);
                    continue;
                }
                String modifierName = obj.get("modifierName").getAsString();
                double amount = obj.get("amount").getAsDouble();
                int operation = obj.get("operation").getAsInt();
                UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
                map.put(attribute, new AttributeModifier(uuid, modifierName, amount, operation));
            }
            info.attributeModifierMap = map;
        }

        return info;
    }

    @Override
    public JsonElement serialize(PotionInfo info, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
        //Write from PotionInfo to JSON
        JsonObject o = new JsonObject();

        // id
        o.addProperty("id", PotionInfo.getPotionId(info));

        // booleans with overwrite flags
        if (info.overwritesIsBeneficial) o.addProperty("isGood", info.isBeneficial);

        // displayColor
        if (info.displayColor != null) o.addProperty("displayColor", info.displayColor.name());

        // liquidColor
        if (info.liquidColorHex != null) o.addProperty("liquidColor", info.liquidColorHex);

        // attribute modifiers
        if (info.attributeModifierMap != null && !info.attributeModifierMap.isEmpty()) {
            JsonArray attributeModifiers = new JsonArray();
            for(Map.Entry<IAttribute, AttributeModifier> entry : info.attributeModifierMap.entrySet()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("attribute", entry.getKey().getName());
                obj.addProperty("modifierName", entry.getValue().getName());
                obj.addProperty("amount", entry.getValue().getAmount());
                obj.addProperty("operation", entry.getValue().getOperation());
                obj.addProperty("uuid", entry.getValue().getID().toString());
                attributeModifiers.add(obj);
            }
            o.add("attributeModifiers", attributeModifiers);
        }

        return o;
    }

    private static String getAsString(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : null;
    }

    private static Boolean getAsBoolean(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsBoolean() : null;
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

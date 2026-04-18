package potioncontrol.config.potioninfojsons;

import com.google.gson.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.IForgeRegistryEntry;
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

        Boolean isBeneficial = getAsBoolean(jsonObj, "isGood");
        if (isBeneficial != null) info.setBeneficial(isBeneficial);

        Boolean isInstant = getAsBoolean(jsonObj, "isInstant");
        if (isInstant != null) info.setInstant(isInstant);

        Boolean isRepeating = getAsBoolean(jsonObj, "isRepeating");
        if (isRepeating != null){
            int cycle = jsonObj.getAsJsonObject( "repeatingPeriod").getAsInt();
            int cycleByAmp = jsonObj.getAsJsonObject( "periodAmpModifier").getAsInt();
            info.setRepeating(isRepeating, cycle, cycleByAmp);
        }

        Boolean prioritisesDuration = getAsBoolean(jsonObj, "prioritisesDuration");
        if (prioritisesDuration != null) info.prioritisesDuration = prioritisesDuration;

        // displayColor
        if(jsonObj.has("displayColor")) {
            String colorStr = getAsString(jsonObj, "displayColor");
            if (colorStr != null) info.setTextDisplayColors(Collections.singletonList(TextFormatting.valueOf(colorStr)));
        } else if(jsonObj.has("displayColors")) {
            JsonArray colorArray = jsonObj.getAsJsonArray("displayColors");
            List<TextFormatting> colors = new ArrayList<>();
            for(JsonElement color : colorArray)
                colors.add(TextFormatting.valueOf(color.getAsString()));
            info.setTextDisplayColors(colors);
        }

        // liquidColor
        String liquidColorHex = getAsString(jsonObj, "liquidColor");
        if (liquidColorHex != null) info.setLiquidColor(liquidColorHex);

        Integer maxLvl = getAsInt(jsonObj, "maxLevel");
        if(maxLvl != null && maxLvl != -1) info.setMaxLevel(maxLvl);

        Integer maxDur = getAsInt(jsonObj, "maxDuration");
        if(maxDur != null && maxDur != -1) info.setMaxDuration(maxDur);

        Boolean milkCurable = getAsBoolean(jsonObj, "milkRemovable");
        if(milkCurable != null) info.milkRemovable = milkCurable;

        if(jsonObj.has("curativeItems")) {
            JsonArray arr = jsonObj.getAsJsonArray("curativeItems");
            List<ItemStack> curativeItems = new ArrayList<>();
            for(JsonElement el : arr){
                JsonObject obj = el.getAsJsonObject();
                String itemName = obj.get("item").getAsString();
                Item item = Item.getByNameOrId(itemName);
                if(item == null){
                    PotionControl.LOGGER.warn("Unable to find curative Item {} for potion {}, skipping", itemName, id);
                    continue;
                }
                int meta = 0;
                if(obj.has("metadata")) meta = obj.get("metadata").getAsInt();
                curativeItems.add(new ItemStack(item, 1, meta));
            }
            if(!curativeItems.isEmpty()) info.curativeItems = curativeItems;
        }

        if(jsonObj.has("beaconLevels")) {
            JsonArray arr = jsonObj.getAsJsonArray("beaconLevels");
            List<Integer> beaconLevels = new ArrayList<>();
            for(JsonElement el : arr){
                int lvl = el.getAsInt();
                beaconLevels.add(lvl);
            }
            info.beaconLevels = beaconLevels;
        }

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

        if(jsonObj.has("removesOnApplication")) {
            JsonArray arr = jsonObj.getAsJsonArray("removesOnApplication");
            Set<Potion> pots = new HashSet<>();
            for (JsonElement el : arr) {
                Potion pot = Potion.getPotionFromResourceLocation(el.getAsString());
                if(pot != null) pots.add(pot);
            }
            if(!pots.isEmpty())
                info.removesOnApplication = new ArrayList<>(pots);
        }

        if(jsonObj.has("blocksApplicationOf")) {
            JsonArray arr = jsonObj.getAsJsonArray("blocksApplicationOf");
            Set<Potion> pots = new HashSet<>();
            for (JsonElement el : arr) {
                Potion pot = Potion.getPotionFromResourceLocation(el.getAsString());
                if(pot != null) pots.add(pot);
            }
            if(!pots.isEmpty())
                info.blocksApplicationOf = new ArrayList<>(pots);
        }

        if(jsonObj.has("blacklistedTags")) {
            JsonArray arr = jsonObj.getAsJsonArray("blacklistedTags");
            Set<String> tags = new HashSet<>();
            arr.forEach(el -> tags.add(el.getAsString()));
            if(!tags.isEmpty())
                info.blacklistedTags = new ArrayList<>(tags);
        }

        if(jsonObj.has("blacklistedEntities")) {
            JsonArray arr = jsonObj.getAsJsonArray("blacklistedEntities");
            Set<String> entities = new HashSet<>();
            arr.forEach(el -> entities.add(el.getAsString()));
            if(!entities.isEmpty())
                info.blacklistedEntities = new ArrayList<>(entities);
        }

        return info;
    }

    @Override
    public JsonElement serialize(PotionInfo info, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
        //Write from PotionInfo to JSON
        JsonObject o = new JsonObject();

        // id
        o.addProperty("id", PotionInfo.getPotionId(info));

        if (info.overwritesIsBeneficial) o.addProperty("isGood", info.isBeneficial);
        if (info.overwritesIsInstant) o.addProperty("isInstant", info.isInstant);
        if (info.overwritesIsRepeating){
            o.addProperty("isRepeating", info.isRepeating);
            o.addProperty("repeatingPeriod", info.repeatingPeriod);
            o.addProperty("periodAmpModifier", info.periodAmpModifier);
        }
        if (info.prioritisesDuration) o.addProperty("prioritisesDuration", true);

        // displayColor
        if (info.displayColors != null){
            if(info.displayColors.size() == 1)
                o.addProperty("displayColor", info.displayColors.get(0).name());
            else {
                JsonArray colors = new JsonArray();
                for (TextFormatting color : info.displayColors)
                    colors.add(color.toString());
                o.add("displayColors", colors);
            }
        }

        // curative items
        o.addProperty("milkRemovable", info.milkRemovable);
        if (info.curativeItems != null) {
            JsonArray curativeItems = new JsonArray();
            for(ItemStack stack : info.curativeItems) {
                if(stack.getItem().getRegistryName() == null) continue;
                JsonObject curativeItem = new JsonObject();
                curativeItem.addProperty("item", stack.getItem().getRegistryName().toString());
                if(stack.getItemDamage() != 0)
                    curativeItem.addProperty("metadata", stack.getItemDamage());
                curativeItems.add(curativeItem);
            }
            o.add("curativeItems", curativeItems);
        }

        // liquidColor
        if (info.liquidColorHex != null) o.addProperty("liquidColor", info.liquidColorHex);

        if (info.beaconLevels != null) {
            JsonArray beaconLevels = new JsonArray();
            info.beaconLevels.forEach(beaconLevels::add);
            o.add("beaconLevels", beaconLevels);
        }

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

        if(info.removesOnApplication != null && !info.removesOnApplication.isEmpty()) {
            JsonArray pots = new JsonArray();
            info.removesOnApplication.stream()
                    .map(IForgeRegistryEntry.Impl::getRegistryName)
                    .filter(Objects::nonNull)
                    .map(ResourceLocation::toString)
                    .forEach(pots::add);
            o.add("removesOnApplication", pots);
        }

        if(info.blocksApplicationOf != null && !info.blocksApplicationOf.isEmpty()) {
            JsonArray pots = new JsonArray();
            info.blocksApplicationOf.stream()
                    .map(IForgeRegistryEntry.Impl::getRegistryName)
                    .filter(Objects::nonNull)
                    .map(ResourceLocation::toString)
                    .forEach(pots::add);
            o.add("blocksApplicationOf", pots);
        }

        if(info.blacklistedTags != null && !info.blacklistedTags.isEmpty()) {
            JsonArray tags = new JsonArray();
            info.blacklistedTags.forEach(tags::add);
            o.add("blacklistedTags", tags);
        }

        if(info.blacklistedEntities != null && !info.blacklistedEntities.isEmpty()) {
            JsonArray entities = new JsonArray();
            info.blacklistedEntities.forEach(entities::add);
            o.add("blacklistedEntities", entities);
        }

        if(info.maxLevel != -1) o.addProperty("maxLevel", info.maxLevel);
        if(info.maxDur != -1) o.addProperty("maxDuration", info.maxDur);

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

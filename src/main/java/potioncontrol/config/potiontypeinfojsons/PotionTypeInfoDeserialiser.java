package potioncontrol.config.potiontypeinfojsons;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import potioncontrol.PotionControl;
import potioncontrol.config.ConfigHandler;
import potioncontrol.config.folders.FirstSetupConfig;
import potioncontrol.util.PotionTypeInfo;
import potioncontrol.util.brewing.BrewRecipe;
import potioncontrol.util.brewing.BrewRecipeUtil;
import potioncontrol.util.brewing.Input;

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
        PotionType potionType = PotionType.getPotionTypeForName(id);

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

        if(potionType != null && jsonObj.has("brews_to")){
            JsonArray arr = jsonObj.getAsJsonArray("brews_to");

            List<BrewRecipe> recipes = new ArrayList<>();

            for(JsonElement el : arr){
                JsonObject obj = el.getAsJsonObject();

                PotionType typeOut = null;
                ItemStack stackOut = null;
                if(obj.has("to")){
                    if(obj.get("to").isJsonObject()){
                        JsonObject to = obj.get("to").getAsJsonObject();
                        stackOut = getAsItemStack(to);
                        if(stackOut.isEmpty()) continue;
                    } else if(obj.get("to").isJsonPrimitive()) {
                        typeOut = PotionType.getPotionTypeForName(obj.get("to").getAsString());
                        if(typeOut == null) continue;
                    }
                }

                ItemStack reagentStack = ItemStack.EMPTY;
                if(obj.has("reagent")) {
                    if (obj.get("reagent").isJsonObject()) {
                        reagentStack = getAsItemStack(obj.getAsJsonObject("reagent"));
                    } else if(obj.get("reagent").isJsonPrimitive()) {
                        Item reagent = Item.getByNameOrId(obj.get("reagent").getAsString());
                        if(reagent != null) reagentStack = new ItemStack(reagent);
                    }
                }

                int brewTime = -1;
                if(obj.has("brew_time"))
                    brewTime = obj.get("brew_time").getAsInt();

                BrewRecipe recipe = BrewRecipeUtil.addRecipe(Input.getFromObj(potionType), reagentStack, Input.getFromObj(typeOut != null ? typeOut : stackOut));
                if(brewTime != -1) recipe.setBrewTime(brewTime);
                recipes.add(recipe);
            }
            info.brewsTo = recipes;
        }

        if(potionType != null && jsonObj.has("brews_from")){
            JsonArray arr = jsonObj.getAsJsonArray("brews_from");
            List<BrewRecipe> recipes = new ArrayList<>();

            for(JsonElement el : arr){
                JsonObject obj = el.getAsJsonObject();

                PotionType typeIn = null;
                ItemStack stackIn = null;
                if(obj.has("from")){
                    if(obj.get("from").isJsonObject()){
                        JsonObject from = obj.get("from").getAsJsonObject();
                        stackIn = getAsItemStack(from);
                        if(stackIn.isEmpty()) continue;
                    } else if(obj.get("from").isJsonPrimitive()) {
                        typeIn = PotionType.getPotionTypeForName(obj.get("from").getAsString());
                        if(typeIn == null) continue;
                    }
                }

                ItemStack reagentStack = ItemStack.EMPTY;
                if(obj.has("reagent")) {
                    if (obj.get("reagent").isJsonObject()) {
                        reagentStack = getAsItemStack(obj.getAsJsonObject("reagent"));
                    } else if(obj.get("reagent").isJsonPrimitive()) {
                        Item reagent = Item.getByNameOrId(obj.get("reagent").getAsString());
                        if(reagent != null) reagentStack = new ItemStack(reagent);
                    }
                }
                int brewTime = -1;
                if(obj.has("brew_time"))
                    brewTime = obj.get("brew_time").getAsInt();

                BrewRecipe recipe = BrewRecipeUtil.addRecipe(Input.getFromObj(typeIn != null ? typeIn : stackIn), reagentStack, Input.getFromObj(potionType));
                if(brewTime != -1) recipe.setBrewTime(brewTime);
                recipes.add(recipe);
            }
            info.brewsFrom = recipes;
        }

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
            for(BrewRecipe recipe : info.brewsFrom) {
                JsonObject obj = new JsonObject();

                if(recipe.input instanceof Input.PotionTypeInput){
                    PotionType typeFrom = ((Input.PotionTypeInput)recipe.input).type;
                    if(typeFrom.getRegistryName() == null) continue;
                    obj.addProperty("from", typeFrom.getRegistryName().toString());
                } else if(recipe.input instanceof Input.ItemStackInput) {
                    ItemStack stackFrom = ((Input.ItemStackInput) recipe.input).stack;
                    obj.add("from", fromItemStack(stackFrom));
                }

                ResourceLocation loc = recipe.reagent.getItem().getRegistryName();
                if(loc == null) continue;
                int meta = recipe.reagent.getMetadata();
                NBTTagCompound tag = recipe.reagent.getTagCompound();

                if(meta == 0 && tag == null){
                    obj.addProperty("reagent", loc.toString());
                } else {
                    obj.add("reagent", fromItemStack(recipe.reagent));
                }

                if(recipe.getBrewTime() != 400)
                    obj.addProperty("brew_time", recipe.getBrewTime());

                brewsFrom.add(obj);
            }
            o.add("brews_from", brewsFrom);
        }

        if(info.brewsTo != null && (ConfigHandler.dev.recipeDirection == FirstSetupConfig.EnumBrewRecipeDirection.TO || ConfigHandler.dev.recipeDirection == FirstSetupConfig.EnumBrewRecipeDirection.BOTH)) {
            JsonArray brewsTo = new JsonArray();
            for(BrewRecipe recipe : info.brewsTo) {
                JsonObject obj = new JsonObject();

                ResourceLocation loc = recipe.reagent.getItem().getRegistryName();
                if(loc == null) continue;
                int meta = recipe.reagent.getMetadata();
                NBTTagCompound tag = recipe.reagent.getTagCompound();

                if(meta == 0 && tag == null){
                    obj.addProperty("reagent", loc.toString());
                } else {
                    obj.add("reagent", fromItemStack(recipe.reagent));
                }

                if(recipe.output instanceof Input.PotionTypeInput){
                    PotionType typeTo = ((Input.PotionTypeInput)recipe.output).type;
                    if(typeTo.getRegistryName() == null) continue;
                    obj.addProperty("to", typeTo.getRegistryName().toString());
                } else if(recipe.output instanceof Input.ItemStackInput) {
                    ItemStack stackTo = ((Input.ItemStackInput) recipe.output).stack;
                    obj.add("to", fromItemStack(stackTo));
                }

                brewsTo.add(obj);
            }
            o.add("brews_to", brewsTo);
        }

        if(info.overwritesTippedDuration)
            o.addProperty("tipped_arrow_duration", info.tippedDuration);

        return o;
    }

    private static ItemStack getAsItemStack(JsonObject reag){
        Item reagent = Item.getByNameOrId(reag.get("item").getAsString());
        if(reagent == null) return ItemStack.EMPTY;

        int meta = reag.has("meta") ? reag.get("meta").getAsInt() : 0;
        ItemStack reagentStack = new ItemStack(reagent, 1, meta);

        NBTTagCompound nbt = null;
        if(reag.has("tag"))
            try {
                nbt = JsonToNBT.getTagFromJson(JsonUtils.getString(reag, "tag"));
            } catch (NBTException ignored) {}
        if(nbt != null) reagentStack.setTagCompound(nbt);

        if(reag.has("type")){
            PotionType type = PotionType.getPotionTypeForName(reag.get("type").getAsString());
            PotionUtils.addPotionToItemStack(reagentStack, type);
        }

        return reagentStack;
    }

    private static JsonObject fromItemStack(ItemStack stack){
        JsonObject from = new JsonObject();

        ResourceLocation loc = stack.getItem().getRegistryName();
        if(loc == null) return from;
        int meta = stack.getMetadata();
        NBTTagCompound tag = stack.getTagCompound();

        from.addProperty("item", loc.toString());
        if (meta != 0) from.addProperty("meta", meta);
        if (tag != null) from.addProperty("tag", tag.toString());

        return from;
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

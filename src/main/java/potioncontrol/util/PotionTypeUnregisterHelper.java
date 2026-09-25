package potioncontrol.util;

import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import potioncontrol.Tags;
import potioncontrol.config.ConfigHandler;

public class PotionTypeUnregisterHelper {
    public static boolean isBlacklisted(PotionType potionType, ResourceLocation loc) {
        if(ConfigHandler.blacklists.potionsDisableTheirTypes && potionType.getEffects().size() == 1) {
            ResourceLocation potionLoc = potionType.getEffects().get(0).getPotion().getRegistryName();
            if(potionLoc != null) {
                String potionRegName = potionLoc.toString();
                if(
                        potionRegName.equals("minecraft:dummy") ||      // vanilla potion already blacklisted -> doesn't have its old registry name
                        ConfigHandler.blacklists.blacklistedRegistryPotions.contains(potionRegName) // modded potion blacklisted
                )
                    return true;
            }
        }

        ResourceLocation typeLoc = loc != null ? loc : potionType.getRegistryName();
        if(typeLoc != null && ConfigHandler.blacklists.blacklistedRegistryPotionTypes.contains(typeLoc.toString())){
            if(typeLoc.toString().equals("minecraft:empty")){
                Tags.LOGGER.info("Unable to prevent the registration of default potiontype minecraft:empty. It is needed for the game to work. Skipping...");
                return false;
            }
            return true;
        }
        return false;
    }
}

package potioncontrol.util;

import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import potioncontrol.config.ConfigHandler;

public class PotionTypeDeRegisterHelper {
    public static boolean isBlacklisted(PotionType potionType) {
        if(ConfigHandler.blacklists.potionsDisableTheirTypes && potionType.getEffects().size() == 1){
            ResourceLocation potLoc = potionType.getEffects().get(0).getPotion().getRegistryName();
            return potLoc != null && ConfigHandler.blacklists.blacklistedRegistryPotions.contains(potLoc.toString());
        }

        ResourceLocation typeLoc = potionType.getRegistryName();
        return typeLoc != null && ConfigHandler.blacklists.blacklistedRegistryPotionTypes.contains(typeLoc.toString());
    }
}

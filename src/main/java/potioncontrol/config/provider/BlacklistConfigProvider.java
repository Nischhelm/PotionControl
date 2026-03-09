package potioncontrol.config.provider;

import potioncontrol.config.ConfigHandler;

import java.util.Arrays;
import java.util.List;

public class BlacklistConfigProvider {
    private static List<String> registryPotionsBlacklist = null;
    private static List<String> registryPotionTypesBlacklist = null;

    public static void onResetConfig(){
    }

    public static List<String> getRegistryPotionBlacklist(){
        if(registryPotionsBlacklist == null)
            registryPotionsBlacklist = Arrays.asList(ConfigHandler.blacklists.blacklistedRegistryPotions);
        return registryPotionsBlacklist;
    }

    public static List<String> getRegistryPotionTypeBlacklist(){
        if(registryPotionTypesBlacklist == null)
            registryPotionTypesBlacklist = Arrays.asList(ConfigHandler.blacklists.blacklistedRegistryPotionTypes);
        return registryPotionTypesBlacklist;
    }
}

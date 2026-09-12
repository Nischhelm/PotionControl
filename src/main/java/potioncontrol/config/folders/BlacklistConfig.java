package potioncontrol.config.folders;

import net.minecraftforge.common.config.Config;
import potioncontrol.util.ConfigRef;

public class BlacklistConfig {
    @Config.Comment("Potions in this list will be prevented from being registered in the game. There will be no way to access them at all.")
    @Config.Name(ConfigRef.REGISTRY_POTION_BLACKLIST_CONFIG_NAME)
    @Config.RequiresMcRestart
    public String[] blacklistedRegistryPotions = {
    };

    @Config.Comment("Potion Types in this list will be prevented from being registered in the game. There will be no way to access them at all.")
    @Config.Name(ConfigRef.REGISTRY_POTIONTYPE_BLACKLIST_CONFIG_NAME)
    @Config.RequiresMcRestart
    public String[] blacklistedRegistryPotionTypes = {
    };
}

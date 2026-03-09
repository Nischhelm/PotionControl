package potioncontrol.config.folders;

import net.minecraftforge.common.config.Config;

public class BlacklistConfig {
    @Config.Comment("Potions in this list will be prevented from being registered in the game. There will be no way to access them at all.")
    @Config.Name("Registered Potion Blacklist")
    @Config.RequiresMcRestart
    public String[] blacklistedRegistryPotions = {
    };

    @Config.Comment("Potion Types in this list will be prevented from being registered in the game. There will be no way to access them at all.")
    @Config.Name("Registered Potion Types Blacklist")
    @Config.RequiresMcRestart
    public String[] blacklistedRegistryPotionTypes = {
    };
}

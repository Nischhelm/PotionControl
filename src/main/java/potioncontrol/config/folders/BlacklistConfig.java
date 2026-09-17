package potioncontrol.config.folders;

import net.minecraftforge.common.config.Config;

import java.util.LinkedHashSet;
import java.util.Set;

public class BlacklistConfig {
    @Config.Comment({
            "Potions in this list will be prevented from being registered in the game. There will be no way to access them at all.",
            "If \"Potions disable their types\" is enabled, this will also unregister all connected potiontypes that only have this potion as an effect."
    })
    @Config.Name("Registered Potion Blacklist")
    @Config.RequiresMcRestart
    public Set<String> blacklistedRegistryPotions = new LinkedHashSet<>();

    @Config.Comment("Potion Types in this list will be prevented from being registered in the game. There will be no way to access them at all.")
    @Config.Name("Registered Potion Types Blacklist")
    @Config.RequiresMcRestart
    public Set<String> blacklistedRegistryPotionTypes = new LinkedHashSet<>();

    @Config.Comment({
            "Most potion types are directly tied to a specific potion, as they only give that potion as an effect",
            "Example: long_fire_resistance gives the effect of type fire_resistance, and no other effects",
            "This toggle makes it so unregistering potions will automatically also unregister all potion types that only map to this potion",
            "Disabling this toggle will force you to manually blacklist all connected types for each potion you unregister."
    })
    @Config.Name("Potions disable their types")
    public boolean potionsDisableTheirTypes = true;
}

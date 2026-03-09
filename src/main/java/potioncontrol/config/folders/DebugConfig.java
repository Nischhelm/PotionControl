package potioncontrol.config.folders;

import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import potioncontrol.PotionControl;
import potioncontrol.util.ConfigRef;

@MixinConfig(name = PotionControl.MODID)
public class DebugConfig {
    @Config.Comment("Potion classes that should not be modified at all by this mod. \n" +
            "Use this if there are crashes when this mod tries to automatically modify some mods enchantments. \n" +
            "You can find the class name in config/potioncontrol/tmp/enchclasses.dump\n" +
            "Class names noted here need to look like net.minecraft.potion.PotionAttackDamage\n" +
            "Vanilla enchants will always be targeted, so putting their classes in here won't do anything")
    @Config.Name(ConfigRef.BLACKLIST_CONFIG_NAME)
    @Config.RequiresMcRestart
    public String[] disabledClasses = {};

    @Config.Comment("If enabled, writes all currently loaded potion infos to /config/potioncontrol/loaded/ during startup. Can be used to check if a given config json is actually loaded (and loaded correctly).")
    @Config.Name("Print Loaded Potion Infos")
    @Config.RequiresMcRestart
    public boolean printLoaded = false;

    @Config.Comment("Disable this to remove PotionControls main feature which hooks into all registered potions code to modify how they behave. \n" +
            "Some features will still work. This is mainly meant for testing if this mods black magic mixins is responsible for a crash (hope not)")
    @Config.Name("(MixinToggle) Enable Potion Injection")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(
            earlyMixin = "mixins.potioncontrol.vanilla.main.json",
            lateMixin = "mixins.potioncontrol.modded.json",
            defaultValue = true
    )
    public static boolean enablePotionInjection = true;
}

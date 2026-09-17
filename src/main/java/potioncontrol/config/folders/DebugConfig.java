package potioncontrol.config.folders;

import fermiumbooter.annotations.MixinConfig;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;
import potioncontrol.Tags;

import java.util.LinkedHashSet;
import java.util.Set;

@MixinConfig(name = Tags.MODID)
public class DebugConfig {
    @Config.Comment("Disable this to remove PotionControls main feature which hooks into all registered potions code to modify how they behave. \n" +
            "Some features will still work. This is mainly meant for testing if this mods black magic mixins is responsible for a crash (hope not)")
    @Config.Name("(MixinToggle) Enable Potion Injection")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(
            earlyMixin = "mixins.potioncontrol.vanilla.main.json",
            lateMixin = "mixins.potioncontrol.modded.json",
            defaultValue = true
    )
    @Order(0)
    public static boolean enablePotionInjection = true;

    @Config.Comment("Potion classes that should not be modified at all by this mod. \n" +
            "Use this if there are crashes when this mod tries to automatically modify some mods enchantments. \n" +
            "You can find the class name in config/potioncontrol/tmp/enchclasses.dump\n" +
            "Class names noted here need to look like net.minecraft.potion.PotionAttackDamage\n" +
            "Vanilla enchants will always be targeted, so putting their classes in here won't do anything")
    @Config.Name("Blacklisted Potion Classes")
    @Config.RequiresMcRestart
    @Order(1)
    public Set<String> disabledClasses = new LinkedHashSet<>();

    @Config.Comment("If enabled, writes all currently loaded potion and potion type infos to /config/potioncontrol/potion[type]s/loaded/ during startup. Can be used to check if a given config json is actually loaded (and loaded correctly).")
    @Config.Name("Print Loaded Potion+Type Infos")
    @Config.RequiresMcRestart
    @Order(2)
    public boolean printLoaded = false;

    @Config.Comment("If enabled, will log sent and avoided additional potion effect packet count for \"Sync Entity Potions\".")
    @Config.Name("Sync Potions Debug Mode")
    @Order(3)
    public boolean syncPotionsDebugMode = false;
}

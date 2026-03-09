package potioncontrol.config.folders;

import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import potioncontrol.PotionControl;
import potioncontrol.util.ConfigRef;

@MixinConfig(name = PotionControl.MODID)
public class FirstSetupConfig {

    @Config.Comment({
            "!Disables itself after a one time use!",
            "",
            "If enabled, during startup this mod will infer info about all registered enchantments and print them out in /config/potioncontrol/inferred-inactive/.",
            "The created json files can be used as blueprints from which to work off of.",
            "To do so, copy everything to /config/potioncontrol/enchantments/, then delete every file + line in file that should stay default/untouched.",
            "DEBUG: These can also be used to check if the changes you apply to the enchantments are actually applied, which would reflect in the inferred files (except \"types\")",
            "WARNING: All files in /inferred-inactive/ will be overwritten every time you start the game with this option enabled"
    })
    @Config.Name(ConfigRef.DO_INFER_CONFIG_NAME)
    @Config.RequiresMcRestart
    public boolean printInferred = true;
}

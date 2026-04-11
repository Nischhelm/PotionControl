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
            "If enabled, during startup this mod will infer info about all registered potions and print them out in /config/potioncontrol/inferred-inactive/.",
            "The created json files can be used as blueprints from which to work off of.",
            "To do so, copy everything to /config/potioncontrol/potions/active/, then delete every file + line in file that should stay default/untouched.",
            "DEBUG: These can also be used to check if the changes you apply to the potions are actually applied, which would reflect in the inferred files",
            "WARNING: All files in /inferred-inactive/ will be overwritten every time you start the game with this option enabled"
    })
    @Config.Name(ConfigRef.DO_INFER_CONFIG_NAME)
    @Config.RequiresMcRestart
    public boolean printInferred = true;

    @Config.Comment("Which time scale to use for durations in inferred potion type infos (MIN as float)")
    @Config.Name("Duration Time Scale")
    @Config.RequiresMcRestart
    public EnumTimeScale durationScale = EnumTimeScale.MIN;
    public enum EnumTimeScale {TICK, SEC, MIN}

    @Config.Comment({
            "Which brewing direction should be printed into inferred potion types.",
            " FROM: writes which other potiontypes + reagents can create this potion type",
            " TO: writes which other potiontypes this one can be brewed to, using which reagents",
    })
    @Config.Name("Brewing Recipe Direction")
    @Config.RequiresMcRestart
    public EnumBrewRecipeDirection recipeDirection = EnumBrewRecipeDirection.FROM;
    public enum EnumBrewRecipeDirection {FROM, TO, BOTH, NONE}

    @Config.Comment("Whether to follow vanillas weird convention of using amplifiers (=lvl-1) instead of levels in inferred potion type infos")
    @Config.Name("Levels as Amplifiers")
    @Config.RequiresMcRestart
    public boolean asAmplifier = false;

    @Config.Comment("Property jsons for potions found in /config/potioncontrol/potions/active/... that are unknown in game will be created and filled with the given properties.")
    @Config.Name("Should create unknown Potions")
    @Config.RequiresMcRestart
    public boolean shouldCreatePotions = false;

    @Config.Comment("Property jsons for potion types found in /config/potioncontrol/potiontypes/active/... that are unknown in game will be created and filled with the given properties.")
    @Config.Name("Should create unknown Potion Types")
    @Config.RequiresMcRestart
    public boolean shouldCreatePotionTypes = true;
}

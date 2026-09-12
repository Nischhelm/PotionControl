package potioncontrol.config;

import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.LoadEarly;
import net.minecraftforge.common.config.Config;
import potioncontrol.Tags;
import potioncontrol.config.folders.*;

@BetterConfig(
		modid = Tags.MODID,
		version = Tags.VERSION,
		bigCategoryComments = false
)
@LoadEarly
public class ConfigHandler {

	@Config.Comment("If you're a modpack dev just starting to set up this mod, you probably want to start here.")
	@Config.Name("First Setup")
	public static FirstSetupConfig dev = new FirstSetupConfig();

	@Config.Comment("Option to blacklist potions and potion types entirely")
	@Config.Name("Blacklists")
	public static BlacklistConfig blacklists = new BlacklistConfig();

	@Config.Comment("Various toggleable options")
	@Config.Name("Mixin Toggles")
	public static MixinToggleConfig mixinToggles = new MixinToggleConfig();

	@Config.Comment("Debug Options")
	@Config.Name("Debug")
	public static DebugConfig debug = new DebugConfig();

	@Config.Comment("Brewing Stand Options")
	@Config.Name("Brewing Stand")
	public static BrewingStandConfig brewingStand = new BrewingStandConfig();
}
package potioncontrol.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import potioncontrol.PotionControl;
import potioncontrol.config.folders.BlacklistConfig;
import potioncontrol.config.folders.BrewingStandConfig;
import potioncontrol.config.folders.DebugConfig;
import potioncontrol.config.folders.FirstSetupConfig;
import potioncontrol.config.provider.BlacklistConfigProvider;

@Config(modid = PotionControl.MODID)
public class ConfigHandler {

	@Config.Comment("If you're a modpack dev just starting to set up this mod, you probably want to start here.")
	@Config.Name("First Setup")
	public static FirstSetupConfig dev = new FirstSetupConfig();

	@Config.Comment("Option to blacklist potions to appear from various sources (or entirely)")
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

	@Mod.EventBusSubscriber(modid = PotionControl.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(PotionControl.MODID)) {
				ConfigManager.sync(PotionControl.MODID, Config.Type.INSTANCE);

				BlacklistConfigProvider.onResetConfig();
			}
		}
	}
}
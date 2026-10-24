package potioncontrol.config;

import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.BetterConfigManager;
import meldexun.betterconfig.api.LoadEarly;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import potioncontrol.Tags;
import potioncontrol.config.folders.*;

@BetterConfig(
		modid = Tags.MODID,
		version = Tags.CFG_VERSION,
		bigCategoryComments = false
)
@LoadEarly
public class ConfigHandler {

	@Config.Comment("If you're a modpack dev just starting to set up this mod, you probably want to start here.")
	@Config.Name("First Setup")
	@Order(0)
	public static FirstSetupConfig dev = new FirstSetupConfig();

	@Config.Comment("Option to blacklist potions and potion types entirely")
	@Config.Name("Blacklists")
	@Order(1)
	public static BlacklistConfig blacklists = new BlacklistConfig();

	@Config.Comment("Various toggleable options")
	@Config.Name("Mixin Toggles")
	@Order(2)
	public static MixinToggleConfig mixinToggles = new MixinToggleConfig();

	@Config.Comment("Brewing Stand Options")
	@Config.Name("Brewing Stand")
	@Order(3)
	public static BrewingStandConfig brewingStand = new BrewingStandConfig();

	@Config.Comment("Modify hardcoded values of vanilla potion effects, like how much Instant Health heals")
	@Config.Name("Vanilla Potion Values")
	@Order(4)
	public static VanillaPotionConfig vanillaPotions = new VanillaPotionConfig();

	@Config.Comment("Debug Options")
	@Config.Name("Debug")
	@Order(5)
	public static DebugConfig debug = new DebugConfig();

    @Mod.EventBusSubscriber(modid = Tags.MODID)
    public static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Tags.MODID)) {
                BetterConfigManager.sync(Tags.MODID);
            }
        }
    }
}
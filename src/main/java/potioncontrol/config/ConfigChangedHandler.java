package potioncontrol.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import potioncontrol.PotionControl;

@Mod.EventBusSubscriber(modid = PotionControl.MODID)
class ConfigChangedHandler {
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(PotionControl.MODID)) {
            ConfigManager.sync(PotionControl.MODID, Config.Type.INSTANCE);
        }
    }
}

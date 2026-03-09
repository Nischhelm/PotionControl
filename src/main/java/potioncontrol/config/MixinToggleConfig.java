package potioncontrol.config;

import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import potioncontrol.PotionControl;

@MixinConfig(name = PotionControl.MODID)
public class MixinToggleConfig {
    @Config.Comment("Makes potion effects properly display their levels above 4 and below 1")
    @Config.Name("Potion Amplifier Visibility (DSHuds)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.potioncontrol.dshuds.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "dshuds", desired = true, reason = "Requires mod to properly function", warnIngame = false)
    public boolean potionAmplifierVisibility = true;

    @Config.Comment("Writes only the actually missing descriptions in log instead of all of them if its \"Log missing descriptions\" config is enabled")
    @Config.Name("Fix Missing Desc Log (PotionDescriptions)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.potioncontrol.potiondescriptions.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "potiondescriptions", desired = true, reason = "Requires mod to properly function", warnIngame = false)
    public boolean fixPotionDescriptionLog = true;
}

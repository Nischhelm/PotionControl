package potioncontrol.config.folders;

import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import potioncontrol.PotionControl;

@MixinConfig(name = PotionControl.MODID)
public class BrewingStandConfig {
    @Config.Comment({
            "Multiply the brewing duration of all brewing actions in the vanilla brewing stand by this amount.",
            "Smaller than 1: Brews faster",
            "Bigger than 1: Brews slower"
    })
    @Config.Name("Global Brew Time Multiplier")
    @Config.RangeDouble(min = 0)
    public float brewTimeMultiplier = 1;
}

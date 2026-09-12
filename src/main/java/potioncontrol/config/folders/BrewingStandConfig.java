package potioncontrol.config.folders;

import net.minecraftforge.common.config.Config;

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

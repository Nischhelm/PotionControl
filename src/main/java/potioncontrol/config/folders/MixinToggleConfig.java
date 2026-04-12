package potioncontrol.config.folders;

import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import potioncontrol.PotionControl;

@MixinConfig(name = PotionControl.MODID)
public class MixinToggleConfig {
    @Config.Comment("Makes potion effects properly display their levels above 4 and below 1 in Dynamic Surroundings HUDs potion effect overlay")
    @Config.Name("Potion Level Display (DSHuds)")
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

    @Config.Comment({
            "Allows to modify brew times of brewing recipes either globally via config or by recipe via potion type jsons.",
            "Requires \"Modify Brewing Recipes\""
    })
    @Config.Name("Modify Brew Time (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.potioncontrol.vanilla.brewtime.json", defaultValue = true)
    public boolean modifyBrewTime = true;

    @Config.Comment({
            "Due to an oversight instead of planned 255 the real max potion amplifier is 127.",
            "Higher amplifiers are possible, but will be saved and recognised on clientside as negative values.",
            "This fixes it by allowing amplifiers a range between -32768 and +32767"
    })
    @Config.Name("Fix Amplifier Cap (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.potioncontrol.vanilla.amplifierfix.json", defaultValue = true)
    public boolean fixAmplifiers = true;

    @Config.Comment("Displays Potion Levels outside of the range 1-3 in inventory (1-10 as roman numerals)")
    @Config.Name("Fix Potion Level Display (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.potioncontrol.vanilla.amplifierdisplay.json", defaultValue = true)
    public boolean fixPotionLevelDisplay = true;

    @Config.Comment("Allows to modify tipped arrow potion durations (in ticks) via potion type jsons (key: tipped_arrow_duration)")
    @Config.Name("Modify Tipped Arrow Time (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.potioncontrol.vanilla.tippedarrowtime.json", defaultValue = true)
    public boolean modifyTippedArrowTime = true;

    @Config.Comment({
            "Allows to modify brewing recipes via potion type jsons",
            "If enabled, potion types that have the brews_from or brews_to set will have vanilla-style recipes in that direction replaced by the given ones",
            "Modded recipes are left untouched, only the ones added by the PotionHelper.addMix / CT IPotionRecipe.add method are modified",
            "Added recipes can also be modded with the input or the output item being any itemstack (including metadata, tags and potion type)",
            "Use \"to\"/\"from\": {\"item\", \"meta\", \"tag\", \"type\"}",
            "Recipes with a non bottle item in input will apply the output type on the input item as output",
            "Recipes with a non bottle item in output will add recipes for all 3 bottle shapes to turn into the given output"
    })
    @Config.Name("Modify Brewing Recipes (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.potioncontrol.vanilla.brewrecipes.json", defaultValue = true)
    public boolean modifyBrewRecipes = true;

    @Config.Comment("Allows to modify which potions or potion types get registered via \"Registered Potion/Type Blacklist\" configs")
    @Config.Name("Allow Unregister Potions (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.potioncontrol.vanilla.unregisterpotions.json", defaultValue = true)
    public boolean modifyRegistry = true;

    @Config.Comment("Allows to set a maximum for specific potions level or duration via their jsons. Use keys \"maxLevel\" and \"maxDuration\"")
    @Config.Name("Modify Max Potion Duration & Level (Vanilla)")
    public boolean modifyMaxAmpDura = true;

    @Config.Comment({
            "Allows to modify whether potions will prioritise high amplifiers (default) or high durations when combining two of the same potions effects",
            "Use key \"prioritisesDuration\": true in the jsons to set longer effects of this potion to be prefered over stronger effects."
    })
    @Config.Name("Allow Prioritise Duration (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.potioncontrol.vanilla.prioritiseduration.json", defaultValue = true)
    public boolean prioritiseDuration = true;
}

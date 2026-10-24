package potioncontrol.config.folders;

import fermiumbooter.annotations.MixinConfig;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;
import potioncontrol.Tags;

public class VanillaPotionConfig {
    @Config.Comment("Modify how much Instant Health heals")
    @Config.Name("Instant Health")
    @Order(0)
    public InstantHealthConfig instantHealth = new InstantHealthConfig();

    @Config.Comment("Modify how much damage Instant Damage deals")
    @Config.Name("Instant Damage")
    @Order(1)
    public InstantDamageConfig instantDamage = new InstantDamageConfig();

    @Config.Comment("Modify how much Regeneration heals per heal tick")
    @Config.Name("Regeneration")
    @Order(2)
    public RegenerationConfig regeneration = new RegenerationConfig();

    @Config.Comment("Modify how much damage Poison deals per damage tick")
    @Config.Name("Poison")
    @Order(3)
    public PoisonConfig poison = new PoisonConfig();

    @Config.Comment("Modify how much damage Wither deals per damage tick")
    @Config.Name("Wither")
    @Order(4)
    public WitherConfig wither = new WitherConfig();

    @Config.Comment("Modify how much exhaustion Hunger adds")
    @Config.Name("Hunger")
    @Order(5)
    public HungerConfig hunger = new HungerConfig();

    @Config.Comment("Modify how much food and saturation the Saturation effect restores")
    @Config.Name("Saturation")
    @Order(6)
    public SaturationConfig saturation = new SaturationConfig();

    @Config.Comment("Modify how much absorption the Absorption effect grants")
    @Config.Name("Absorption")
    @Order(7)
    public AbsorptionConfig absorption = new AbsorptionConfig();

    @Config.Comment("Modify how much damage Resistance blocks")
    @Config.Name("Resistance")
    @Order(8)
    public ResistanceConfig resistance = new ResistanceConfig();

    @Config.Comment("Modify how high Jump Boost lets entities jump and how much fall damage it prevents")
    @Config.Name("Jump Boost")
    @Order(9)
    public JumpBoostConfig jumpBoost = new JumpBoostConfig();

    @Config.Comment("Modify how fast Levitation lifts entities")
    @Config.Name("Levitation")
    @Order(10)
    public LevitationConfig levitation = new LevitationConfig();

    @Config.Comment("Modify how much Haste speeds up mining and arm swings")
    @Config.Name("Haste")
    @Order(11)
    public HasteConfig haste = new HasteConfig();

    @Config.Comment("Modify how much Mining Fatigue slows down mining and arm swings")
    @Config.Name("Mining Fatigue")
    @Order(12)
    public MiningFatigueConfig miningFatigue = new MiningFatigueConfig();

    @MixinConfig(name = Tags.MODID)
    public static class InstantHealthConfig {
        @Config.Comment("Enables the options in this category")
        @Config.Name("Modify Instant Health")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.instanthealth.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment({
                "Health healed by level I Instant Health. Splash and lingering potions multiply this by how close the target was.",
                "Also used for undead mobs hit by Instant Damage, since they get healed by it."
        })
        @Config.Name("Heal Amount")
        @Config.RangeDouble(min = 0)
        @Order(1)
        public float healAmount = 4;

        @Config.Comment({
                "true: Heal Amount doubles every level (vanilla: 4, 8, 16, ...)",
                "false: Heal Amount is multiplied by the level (4, 8, 12, ...)"
        })
        @Config.Name("Exponential Scaling")
        @Order(2)
        public boolean exponential = true;
    }

    @MixinConfig(name = Tags.MODID)
    public static class InstantDamageConfig {
        @Config.Comment("Enables the options in this category")
        @Config.Name("Modify Instant Damage")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.instantdamage.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment({
                "Damage dealt by level I Instant Damage. Splash and lingering potions multiply this by how close the target was.",
                "Also used for undead mobs hit by Instant Health, since they get damaged by it."
        })
        @Config.Name("Damage Amount")
        @Config.RangeDouble(min = 0)
        @Order(1)
        public float damageAmount = 6;

        @Config.Comment({
                "true: Damage Amount doubles every level (vanilla: 6, 12, 24, ...)",
                "false: Damage Amount is multiplied by the level (6, 12, 18, ...)"
        })
        @Config.Name("Exponential Scaling")
        @Order(2)
        public boolean exponential = true;
    }

    @MixinConfig(name = Tags.MODID)
    public static class RegenerationConfig {
        @Config.Comment({
                "Enables the options in this category",
                "Note: How often Regeneration heals can be changed via potion jsons (repeatingPeriod, periodAmpModifier)"
        })
        @Config.Name("Modify Regeneration")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.regeneration.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment("Health healed every time Regeneration heals")
        @Config.Name("Heal Amount")
        @Config.RangeDouble(min = 0)
        @Order(1)
        public float healAmount = 1;
    }

    @MixinConfig(name = Tags.MODID)
    public static class PoisonConfig {
        @Config.Comment({
                "Enables the options in this category",
                "Note: How often Poison deals damage can be changed via potion jsons (repeatingPeriod, periodAmpModifier)"
        })
        @Config.Name("Modify Poison")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.poison.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment("Damage dealt every time Poison deals damage")
        @Config.Name("Damage Amount")
        @Config.RangeDouble(min = 0)
        @Order(1)
        public float damageAmount = 1;

        @Config.Comment("Poison will only deal damage while the entity has more health than this. Set to 0 to let poison kill.")
        @Config.Name("Minimum Health")
        @Config.RangeDouble(min = 0)
        @Order(2)
        public float minHealth = 1;
    }

    @MixinConfig(name = Tags.MODID)
    public static class WitherConfig {
        @Config.Comment({
                "Enables the options in this category",
                "Note: How often Wither deals damage can be changed via potion jsons (repeatingPeriod, periodAmpModifier)"
        })
        @Config.Name("Modify Wither")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.wither.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment("Damage dealt every time Wither deals damage")
        @Config.Name("Damage Amount")
        @Config.RangeDouble(min = 0)
        @Order(1)
        public float damageAmount = 1;
    }

    @MixinConfig(name = Tags.MODID)
    public static class HungerConfig {
        @Config.Comment("Enables the options in this category")
        @Config.Name("Modify Hunger")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.hunger.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment("Exhaustion added every tick per level of Hunger. Every 4 exhaustion removes one saturation or food point.")
        @Config.Name("Exhaustion per Level")
        @Config.RangeDouble(min = 0)
        @Order(1)
        public float exhaustionPerLevel = 0.005F;
    }

    @MixinConfig(name = Tags.MODID)
    public static class SaturationConfig {
        @Config.Comment("Enables the options in this category")
        @Config.Name("Modify Saturation")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.saturation.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment("Food points restored every tick per level of Saturation")
        @Config.Name("Food per Level")
        @Config.RangeInt(min = 0)
        @Order(1)
        public int foodPerLevel = 1;

        @Config.Comment("Saturation modifier of the restored food. Saturation gained = food * modifier * 2")
        @Config.Name("Saturation Modifier")
        @Config.RangeDouble(min = 0)
        @Order(2)
        public float saturationModifier = 1;
    }

    @MixinConfig(name = Tags.MODID)
    public static class AbsorptionConfig {
        @Config.Comment("Enables the options in this category")
        @Config.Name("Modify Absorption")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.absorption.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment({
                "Absorption health granted per level of Absorption",
                "Changing this while entities have Absorption active can leave them with too much or too little absorption when it runs out."
        })
        @Config.Name("Absorption per Level")
        @Config.RangeInt(min = 0)
        @Order(1)
        public int absorptionPerLevel = 4;
    }

    @MixinConfig(name = Tags.MODID)
    public static class ResistanceConfig {
        @Config.Comment({
                "Enables the options in this category",
                "Note: With PotionCore, its armor bar overlay still displays 20% per level.",
                "Note: With PotionCore + RLTweaker (\"patchPotionCoreResistance\", enabled by default), this does nothing.",
                "  RLTweaker replaces the vanilla resistance calculation, use its \"Resistance Potion Modifier Amount\" or the potioncore.damageResistance attributeModifier in the potion jsons instead.",
                "Note: With FirstAid, damage to players uses FirstAid's own resistance calculation, see \"Modify Resistance (FirstAid)\"."
        })
        @Config.Name("Modify Resistance")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.resistance.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment("Percent of incoming damage blocked per level of Resistance. 100% or more makes entities immune to most damage.")
        @Config.Name("Damage Reduction per Level")
        @Config.RangeInt(min = 0, max = 100)
        @Order(1)
        public int reductionPercentPerLevel = 20;

        @Config.Comment({
                "FirstAid calculates resistance for damage to players itself (\"resistanceReductionPercentPerLevel\").",
                "If this and \"Modify Resistance\" are enabled, FirstAid will use Damage Reduction per Level instead."
        })
        @Config.Name("Modify Resistance (FirstAid)")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.firstaid.resistance.json", defaultValue = true)
        @MixinConfig.CompatHandling(modid = "firstaid", desired = true, reason = "Requires mod to properly function", warnIngame = false)
        @Order(2)
        public boolean firstAidCompat = true;
    }

    @MixinConfig(name = Tags.MODID)
    public static class JumpBoostConfig {
        @Config.Comment({
                "Enables the options in this category",
                "Note: Player movement is calculated clientside, so the client needs the same values as the server.",
                "Note: With PotionCore's \"Controllable Jump Boost\" (enabled by default), Jump Motion per Level does nothing.",
                "  PotionCore overrides the jump motion, use its \"Vanilla Jump Boost Height\" or the potioncore.jumpHeight attributeModifier in the potion jsons instead.",
                "  PotionCore also reduces fall distance by its jump height attribute, Safe Fall Distance per Level is applied on top of that."
        })
        @Config.Name("Modify Jump Boost")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.jumpboost.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment("Upwards motion added to jumps per level of Jump Boost (also for horses). For reference, a normal jump has 0.42")
        @Config.Name("Jump Motion per Level")
        @Config.RangeDouble(min = 0)
        @Order(1)
        public float jumpMotionPerLevel = 0.1F;

        @Config.Comment("Blocks of fall distance that don't cause fall damage per level of Jump Boost")
        @Config.Name("Safe Fall Distance per Level")
        @Config.RangeDouble(min = 0)
        @Order(2)
        public float safeFallDistancePerLevel = 1;
    }

    @MixinConfig(name = Tags.MODID)
    public static class LevitationConfig {
        @Config.Comment({
                "Enables the options in this category",
                "Note: Player movement is calculated clientside, so the client needs the same values as the server."
        })
        @Config.Name("Modify Levitation")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.levitation.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment("Upwards speed per level of Levitation, in blocks per tick. Negative values make entities sink instead.")
        @Config.Name("Speed per Level")
        @Order(1)
        public double speedPerLevel = 0.05D;
    }

    @MixinConfig(name = Tags.MODID)
    public static class HasteConfig {
        @Config.Comment({
                "Enables the options in this category",
                "Note: Mining speed is also calculated clientside, so the client needs the same values as the server.",
                "Note: The attack speed bonus of Haste can be changed via potion jsons (attributeModifiers)"
        })
        @Config.Name("Modify Haste")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.haste.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment("Mining speed bonus per level of Haste. 0.2 = +20% per level")
        @Config.Name("Mining Speed per Level")
        @Config.RangeDouble(min = 0)
        @Order(1)
        public float miningSpeedPerLevel = 0.2F;

        @Config.Comment("Ticks an arm swing gets shorter per level of Haste. Normal arm swings take 6 ticks.")
        @Config.Name("Swing Duration Reduction per Level")
        @Config.RangeInt(min = 0)
        @Order(2)
        public int swingReductionPerLevel = 1;

        @Config.Comment({
                "Haste can't make arm swings shorter than this many ticks",
                "Set to 0 to disable the minimum (vanilla). Swings with a duration of 0 or below end after 1 tick."
        })
        @Config.Name("Minimum Swing Duration")
        @Config.RangeInt(min = 0)
        @Order(3)
        public int minSwingDuration = 2;
    }

    @MixinConfig(name = Tags.MODID)
    public static class MiningFatigueConfig {
        @Config.Comment({
                "Enables the options in this category",
                "By default this fixes MC-279819: Vanilla uses 0.3, 0.09, 0.0027 and 0.00081 as mining speed multipliers,",
                "  so from level III on mining is 10x slower than the intended 0.3 per level.",
                "  To keep elder guardians about as strong as before, they give Mining Fatigue V instead of III by default.",
                "Note: Mining speed is also calculated clientside, so the client needs the same values as the server.",
                "Note: The attack speed malus of Mining Fatigue can be changed via potion jsons (attributeModifiers)"
        })
        @Config.Name("Modify Mining Fatigue")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.potioncontrol.vanilla.values.miningfatigue.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment({
                "Mining speed multiplier for each level of Mining Fatigue, starting at level I.",
                "Levels above the amount of entries use the last entry.",
                "Default is 0.3^level. Vanilla is 0.3, 0.09, 0.0027, 8.1E-4"
        })
        @Config.Name("Mining Speed Multipliers")
        @Order(1)
        public double[] miningSpeedMultipliers = {0.3, 0.09, 0.027, 0.0081, 0.00243};

        @Config.Comment("Ticks an arm swing gets longer per level of Mining Fatigue. Normal arm swings take 6 ticks.")
        @Config.Name("Swing Duration Increase per Level")
        @Config.RangeInt(min = 0)
        @Order(2)
        public int swingIncreasePerLevel = 2;

        @Config.Comment({
                "Level of Mining Fatigue elder guardians give players nearby. Vanilla: 3",
                "Default 5 (0.00243 with default multipliers) is about as strong as vanilla level III (0.0027)"
        })
        @Config.Name("Elder Guardian Mining Fatigue Level")
        @Config.RangeInt(min = 1)
        @Order(3)
        public int elderGuardianLevel = 5;
    }
}

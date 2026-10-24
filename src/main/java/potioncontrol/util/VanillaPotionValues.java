package potioncontrol.util;

public class VanillaPotionValues {
    /**
     * Vanilla uses base << amplifier for instant effects, which is 0 for negative amplifiers
     */
    public static float getInstantAmount(float base, boolean exponential, int amplifier) {
        if (amplifier < 0) return 0;
        if (exponential) return (float) (base * Math.pow(2, amplifier));
        return base * (amplifier + 1);
    }

    /**
     * Splash and lingering potions scale instant effects by distance and round them to whole numbers
     */
    public static float getSplashAmount(float amount, double health) {
        return (int) (health * amount + 0.5D);
    }
}

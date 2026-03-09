package potioncontrol.core;

import net.minecraft.potion.Potion;

//Only needs to extends Potion to fix refmaps of PotionMixin
public class PotionDummy extends Potion {
    protected PotionDummy(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }
}
package potioncontrol.core;

import net.minecraft.potion.Potion;
import potioncontrol.util.PotionInfo;

//Only needs to extend Potion to fix refmaps of PotionMixin
public class PotionDummy extends Potion {

    public PotionDummy(PotionInfo info) {
        super(!info.isBeneficial, info.getLiquidColor());
        this.setRegistryName(info.id);
    }
}
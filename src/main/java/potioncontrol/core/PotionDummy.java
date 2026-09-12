package potioncontrol.core;

import net.minecraft.potion.Potion;
import potioncontrol.Tags;
import potioncontrol.util.PotionInfo;

//Only needs to extend Potion to fix refmaps of PotionMixin
//also used for replacing unregistered vanilla potions
public class PotionDummy extends Potion {
    //This is only used for de-registered vanilla potions
    public static final Potion dummy = new PotionDummy().setRegistryName("minecraft", "dummy").setPotionName("dummy");

    public PotionDummy() {
        super(false, 0x000000);
    }

    public PotionDummy(PotionInfo info) {
        super(!info.isBeneficial, info.getLiquidColor());
        this.setRegistryName(info.id);
    }
}
package potioncontrol.core;

import net.minecraft.potion.PotionType;

//only used for replacing unregistered vanilla potion types
public class PotionTypeDummy extends PotionType {
    public static final PotionTypeDummy dummy = new PotionTypeDummy();
}
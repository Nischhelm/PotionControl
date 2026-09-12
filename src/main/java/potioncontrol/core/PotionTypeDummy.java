package potioncontrol.core;

import net.minecraft.potion.PotionType;

//only used for replacing unregistered vanilla potion types
public class PotionTypeDummy extends PotionType {
    public PotionTypeDummy(String name){
        super(name);
//        this.setRegistryName(Tags.MODID, "dummy_"+name);
        this.setRegistryName("minecraft", name); //this is only used to de-register vanilla types
    }
}
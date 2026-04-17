package potioncontrol.mixin.vanilla.main;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionAttackDamage;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

@Mixin(PotionAttackDamage.class)
public abstract class PotionAttackDamageMixin extends Potion {
    protected PotionAttackDamageMixin(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    @WrapMethod(method = "getAttributeModifierAmount")
    private double pc_simplifyAmplifierAmount(int amplifier, AttributeModifier modifier, Operation<Double> original){
        //I got no clue why vanilla decided to do it differently here for no reason
        return super.getAttributeModifierAmount(amplifier, modifier);
    }

    @Override @Nonnull
    public Potion registerPotionAttributeModifier(@Nonnull IAttribute attr, @Nonnull String uuid, double amount, int op) {
        if(this.getName().equals("effect.weakness")) return super.registerPotionAttributeModifier(attr, uuid, -4, op);
        if(this.getName().equals("effect.damageBoost")) return super.registerPotionAttributeModifier(attr, uuid, +3, op);

        return super.registerPotionAttributeModifier(attr, uuid, amount, op);
    }

//    @Nonnull
//    public Potion func_111184_a(@Nonnull IAttribute attr, @Nonnull String uuid, double amount, int op) {
//        if(this.getName().equals("effect.weakness")) return super.registerPotionAttributeModifier(attr, uuid, -4, op);
//        if(this.getName().equals("effect.strength")) return super.registerPotionAttributeModifier(attr, uuid, +3, op);
//
//        return super.registerPotionAttributeModifier(attr, uuid, amount, op);
//    }
}

package potioncontrol.mixin.modded.neat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import vazkii.neat.HealthBarRenderer;

@Mixin(HealthBarRenderer.class)
public abstract class HealthBarRenderer_IllagersMixin {

    @ModifyArgs(
            method ="renderHealthBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;II)V")
    )
    public void fermiummixins_renderIllagers(Args args, @Local EnumCreatureAttribute attr) {
        if(attr == EnumCreatureAttribute.ILLAGER) {
            args.set(0, Items.EMERALD);
            args.set(2, 0);
        }
    }
}

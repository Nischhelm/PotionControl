package potioncontrol.mixin.vanilla.beacon;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.gui.inventory.GuiBeacon.PowerButton")
public abstract class BeaconPowerButtonMixin {
    @ModifyExpressionValue(
            method = "drawButtonForegroundLayer",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/inventory/GuiBeacon$PowerButton;effect:Lnet/minecraft/potion/Potion;", ordinal = 1)
    )
    private Potion pc$passThroughPotions(Potion original){
        for(Potion pot : TileEntityBeacon.EFFECTS_LIST[3]) //basically EFFECTS_LIST[3].contains(original)
            if(pot == original) return MobEffects.REGENERATION; //will write as normal upgrade not lvl 2
        return original;
    }
}

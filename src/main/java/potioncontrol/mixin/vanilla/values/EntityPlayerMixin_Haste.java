package potioncontrol.mixin.vanilla.values;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import potioncontrol.config.ConfigHandler;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin_Haste {
    //Forge method, not obfuscated
    @ModifyExpressionValue(
            method = "getDigSpeed(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)F",
            at = @At(value = "CONSTANT", args = "floatValue=0.2"),
            remap = false
    )
    private float potioncontrol_modifyHasteMiningSpeed(float original) {
        return ConfigHandler.vanillaPotions.haste.miningSpeedPerLevel;
    }
}

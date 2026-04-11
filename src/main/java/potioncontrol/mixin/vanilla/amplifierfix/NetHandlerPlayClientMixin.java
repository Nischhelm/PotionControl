package potioncontrol.mixin.vanilla.amplifierfix;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketEntityEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import potioncontrol.util.wrapper.IHasActualAmplifier;

@Mixin(NetHandlerPlayClient.class)
public abstract class NetHandlerPlayClientMixin {
    @ModifyArg(
            method = "handleEntityEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;<init>(Lnet/minecraft/potion/Potion;IIZZ)V"),
            index = 2
    )
    private int pc_useActualAmplifier(int ampIn, @Local(argsOnly = true) SPacketEntityEffect packet){
        return ((IHasActualAmplifier)packet).pc_getActualAmplifier();
    }
}

package potioncontrol.mixin.vanilla;

import net.minecraft.command.CommandEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CommandEffect.class)
public abstract class CommandEffectMixin {
    @ModifyArgs(
            method = "execute",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandEffect;parseInt(Ljava/lang/String;II)I", ordinal = 1)
    )
    private void pc_modifyAmplifierLimits(Args args){
        args.set(1, (int) Short.MIN_VALUE);
        args.set(2, (int) Short.MAX_VALUE);
    }
}

package potioncontrol.mixin.vanilla.values;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import potioncontrol.config.ConfigHandler;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin_MiningFatigue extends EntityLivingBase {
    public EntityPlayerMixin_MiningFatigue(World worldIn) {
        super(worldIn);
    }

    //Forge method, not obfuscated. float ordinal 0 is the dig speed, 1 is the mining fatigue multiplier
    @ModifyVariable(
            method = "getDigSpeed(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)F",
            at = @At("STORE"),
            ordinal = 1,
            remap = false
    )
    private float potioncontrol_modifyMiningFatigueMultiplier(float original) {
        double[] multipliers = ConfigHandler.vanillaPotions.miningFatigue.miningSpeedMultipliers;
        if (multipliers.length == 0) return original;
        //vanilla uses the last entry for negative amplifiers too
        int amplifier = this.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier();
        return (float) multipliers[amplifier >= 0 && amplifier < multipliers.length ? amplifier : multipliers.length - 1];
    }
}

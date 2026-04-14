package potioncontrol.mixin.vanilla.beacon;

import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.util.PotionInfo;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Mixin(TileEntityBeacon.class)
public abstract class TileEntityBeaconMixin {
    @Shadow @Final @Mutable public static Potion[][] EFFECTS_LIST;

    @Inject(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;")
    )
    private static void pc_modifyBeaconEffects(CallbackInfo ci) {
        Map<Integer, LinkedHashSet<Potion>> levels = new LinkedHashMap<>();

        //copy original
        for(int i = 0; i < EFFECTS_LIST.length; i++) {
            levels.put(i + 1, new LinkedHashSet<>());
            for(Potion pot : EFFECTS_LIST[i])
                levels.get(i + 1).add(pot);
        }

        //remove existing ones that are registered differently
        levels.forEach((level, potions) ->
                potions.removeIf(pot -> {
                    PotionInfo info = PotionInfo.get(pot);
                    if (info == null) return false;
                    if (info.beaconLevels == null) return false;
                    if (info.beaconLevels.contains(level)) return false;
                    return true;
                })
        );

        //apply jsons
        PotionInfo.getAll().stream()
                .filter(info -> info.beaconLevels != null)
                .forEach(info ->
                        info.beaconLevels.forEach(level ->
                                levels.computeIfAbsent(level, t -> new LinkedHashSet<>())
                                        .add(PotionInfo.getPotionObject(info))
                        )
                );

        //write to EFFECTS_LIST
        Potion[][] newEffectList = new Potion[levels.size()][];
        levels.forEach((level, potions) ->{
            Potion[] effects = potions.toArray(new Potion[0]);
            newEffectList[level - 1] = effects;
        });

        EFFECTS_LIST = newEffectList;
    }
}

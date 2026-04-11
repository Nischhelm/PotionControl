package potioncontrol.handlers;

import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import potioncontrol.mixin.accessor.PotionEffectAccessor;
import potioncontrol.util.PotionInfo;

public class PotionAddedHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        PotionEffect effect = event.getPotionEffect();
        PotionInfo info = PotionInfo.get(effect.getPotion());
        if(info == null) return;
        int maxAmp = info.maxLevel - 1;
        if(info.maxLevel != -1 && effect.getAmplifier() > maxAmp)
            ((PotionEffectAccessor)effect).setAmplifier(maxAmp);
        if(info.maxDur != -1 && effect.getDuration() > info.maxDur)
            ((PotionEffectAccessor)effect).setDuration(info.maxDur);
    }
}

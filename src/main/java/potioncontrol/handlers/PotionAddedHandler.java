package potioncontrol.handlers;

import net.minecraft.entity.EntityList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import potioncontrol.mixin.accessor.PotionEffectAccessor;
import potioncontrol.util.PotionInfo;

import java.util.ArrayList;
import java.util.List;

public class PotionAddedHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void applyMaxLvlDur(PotionEvent.PotionAddedEvent event) {
        PotionEffect effect = event.getPotionEffect();
        PotionInfo info = PotionInfo.get(effect.getPotion());
        if(info == null) return;
        int maxAmp = info.maxLevel - 1;
        if(info.maxLevel != -1 && effect.getAmplifier() > maxAmp)
            ((PotionEffectAccessor)effect).setAmplifier(maxAmp);
        if(info.maxDur != -1 && effect.getDuration() > info.maxDur)
            ((PotionEffectAccessor)effect).setDuration(info.maxDur);
    }

    @SubscribeEvent
    public static void applyBlacklists(PotionEvent.PotionApplicableEvent event) {
        if(event.getEntityLiving() == null) return;
        PotionEffect effect = event.getPotionEffect();
        PotionInfo info = PotionInfo.get(effect.getPotion());
        if(info == null) return;
        if(info.blacklistedEntities != null && !info.blacklistedEntities.isEmpty()) {
            ResourceLocation loc = EntityList.getKey(event.getEntityLiving());
            if(loc == null) return;
            if(info.blacklistedEntities.contains(loc.toString())){
                event.setResult(Event.Result.DENY);
                return;
            }
        }
        if(info.blacklistedTags != null && !info.blacklistedTags.isEmpty())
            if(info.blacklistedTags.stream().anyMatch(tag ->
                    event.getEntityLiving().getTags().contains(tag)
            )) {
                event.setResult(Event.Result.DENY);
                return;
            }
    }

    @SubscribeEvent
    public static void applyIncompats(PotionEvent.PotionApplicableEvent event) {
        if(event.getEntityLiving() == null) return;
        PotionEffect effect = event.getPotionEffect();
        for(PotionEffect pot : event.getEntityLiving().getActivePotionEffects()) {
            PotionInfo info = PotionInfo.get(pot.getPotion());
            if(info != null && info.blocksApplicationOf != null && !info.blocksApplicationOf.isEmpty()) {
                if(info.blocksApplicationOf.stream().anyMatch(p -> p.equals(effect.getPotion()))) {
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
        }
        PotionInfo infoNew = PotionInfo.get(effect.getPotion());
        List<PotionEffect> toRemove = new ArrayList<>();
        if(infoNew != null && infoNew.removesOnApplication != null && !infoNew.removesOnApplication.isEmpty())
            for(PotionEffect pot : event.getEntityLiving().getActivePotionEffects())
                if(infoNew.removesOnApplication.stream().anyMatch(p -> p.equals(pot.getPotion())))
                    toRemove.add(pot);

        toRemove.forEach(eff -> event.getEntityLiving().removePotionEffect(eff.getPotion()));
    }
}

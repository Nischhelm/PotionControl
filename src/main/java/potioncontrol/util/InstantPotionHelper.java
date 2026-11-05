package potioncontrol.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class InstantPotionHelper {
    /**
     * Vanilla applies instant potions (drinking, splash, lingering) by calling Potion.affectEntity directly,
     * which never fires PotionApplicableEvent. This fires it the same way addPotionEffect would,
     * so anything that denies an effect through the event also stops its instant application.
     */
    public static boolean isApplicable(Potion potion, EntityLivingBase entity, int amplifier) {
        if (entity == null) return true;
        PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(entity, new PotionEffect(potion, 1, amplifier));
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() != Event.Result.DENY;
    }
}

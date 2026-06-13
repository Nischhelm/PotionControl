package potioncontrol.mixin.modded.neat;

import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potioncontrol.mixin.accessor.GuiAccessor;
import vazkii.neat.HealthBarRenderer;

import java.util.Collection;
import java.util.List;

@Mixin(HealthBarRenderer.class)
public abstract class HealthBarRenderer_PotionsMixin {

    @Unique private static final int pc$zLevel = 0;

    @Inject(
            method ="renderHealthBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V", ordinal = 1)
    )
    public void pc_renderPotions(EntityLivingBase passedEntity, float partialTicks, Entity viewPoint, CallbackInfo ci, @Local Minecraft mc) {
        Collection<PotionEffect> effects = passedEntity.getActivePotionEffects();
        if (effects.isEmpty()) return;

        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75,0.75,1);

        List<PotionEffect> sorted = Ordering.natural().reverse().sortedCopy(effects);
        for(int effectCounter = 0; effectCounter < sorted.size(); effectCounter++) {
            PotionEffect effect = sorted.get(effectCounter);
            Potion potion = effect.getPotion();

            if (!potion.shouldRenderHUD(effect)) continue;
            mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
            if (!effect.doesShowParticles()) continue;

            int x = -(3 + 25 * effectCounter);
            int y = 57;

            //Background
            GlStateManager.color(1,1,1,1);
            this.pc$drawTexturedModalRect(x, y, 141, 166, 24, 24);

            GlStateManager.color(1, 1, 1, 1);
            // FORGE - Move status icon check down from above so renderHUDEffect will still be called without a status icon
            if (potion.hasStatusIcon()) {
                int iconIndex = potion.getStatusIconIndex();
                this.pc$drawTexturedModalRect(x + 3, y + 3, iconIndex % 8 * 18, 198 + iconIndex / 8 * 18, 18, 18);
            }
            float tmp = ((GuiAccessor)mc.ingameGUI).getZLevel();
            ((GuiAccessor)mc.ingameGUI).setZLevel(pc$zLevel);
            potion.renderHUDEffect(effect, mc.ingameGUI, x, y, pc$zLevel, 1);
            ((GuiAccessor)mc.ingameGUI).setZLevel(tmp);
        }
        GlStateManager.popMatrix();
    }

    @Unique
    public void pc$drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        double u1 = textureX / 256F;
        double v1 = textureY / 256F;
        double u2 = (textureX + width) / 256F;
        double v2 = (textureY + height) / 256F;
        bufferbuilder.pos(x, y + height, pc$zLevel).tex(u1, v2).endVertex();
        bufferbuilder.pos(x + width, y + height, pc$zLevel).tex(u2, v2).endVertex();
        bufferbuilder.pos(x + width, y, pc$zLevel).tex(u2, v1).endVertex();
        bufferbuilder.pos(x, y, pc$zLevel).tex(u1, v1).endVertex();
        tessellator.draw();
    }
}

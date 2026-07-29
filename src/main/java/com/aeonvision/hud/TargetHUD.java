package com.aeonvision.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;

public class TargetHUD {

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static float smoothHP = 0;
    private static String lastName = "";
    private static long time = 0;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (MC.player == null || MC.crosshairTarget == null) return;
        if (MC.crosshairTarget.getType() != HitResult.Type.ENTITY) return;
        
        Entity entity = ((EntityHitResult) MC.crosshairTarget).getEntity();
        if (!(entity instanceof PlayerEntity target)) return;
        
        time = System.currentTimeMillis();
        
        float hp = target.getHealth() + target.getAbsorptionAmount();
        float maxHp = target.getMaxHealth();
        float hpPercent = MathHelper.clamp(hp / maxHp, 0, 1);
        smoothHP += (hpPercent - smoothHP) * 0.15f;
        
        int armor = target.getArmor();
        String name = target.getName().getString();
        lastName = name;
        
        int cx = MC.getWindow().getScaledWidth() / 2;
        int y = 35;
        int w = 140;
        int x = cx - w / 2;
        
        // Фон (стекло)
        context.fill(x, y, x + w, y + 45, 0xB0101018);
        context.fill(x, y, x + w, y + 1, 0x50FFFFFF);
        context.fill(x, y + 44, x + w, y + 45, 0x30FFFFFF);
        
        // Никнейм
        context.drawText(MC.textRenderer, Text.literal(name), x + 5, y + 3, 0xFFFFFFFF, true);
        
        // HP
        String hpText = String.format("%.0f HP", hp);
        context.drawText(MC.textRenderer, Text.literal(hpText), x + w - MC.textRenderer.getWidth(hpText) - 5, y + 3, 0xCCCCCC, true);
        
        // HP-бар
        int barX = x + 5;
        int barY = y + 16;
        int barW = w - 10;
        int barH = 3;
        
        // Фон бара
        context.fill(barX, barY, barX + barW, barY + barH, 0x30FFFFFF);
        
        // Заполнение с градиентом
        int fillW = (int)(barW * smoothHP);
        if (fillW > 0) {
            for (int i = 0; i < fillW; i++) {
                float t = (float)i / barW;
                int r = (int)(255 * (1 - t));
                int g = (int)(255 * t);
                int color = 0xFF000000 | (r << 16) | (g << 8);
                context.fill(barX + i, barY, barX + i + 1, barY + barH, color);
            }
        }
        
        // Броня (иконки)
        if (armor > 0) {
            String armorText = "🛡 " + armor;
            context.drawText(MC.textRenderer, Text.literal(armorText), x + 5, y + 22, 0xAAAACC, true);
        }
        
        // Эффекты
        int fx = x + 5;
        int fy = y + 22;
        var effects = target.getStatusEffects();
        int count = 0;
        for (var effect : effects) {
            if (count > 4) break;
            String icon = getEffectIcon(effect.getEffectType().value().getCategory());
            context.drawText(MC.textRenderer, Text.literal(icon), fx + count * 14, fy + 12, 0x80FFFFFF, false);
            count++;
        }
    }
    
    private static String getEffectIcon(net.minecraft.entity.effect.StatusEffectCategory cat) {
        return switch(cat) {
            case BENEFICIAL -> "▲";
            case HARMFUL -> "▼";
            case NEUTRAL -> "●";
        };
    }
                             }

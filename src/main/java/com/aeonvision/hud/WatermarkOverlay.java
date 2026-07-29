package com.aeonvision.hud;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class WatermarkOverlay {

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static long lastShimmer = 0;
    private static float shimmerAlpha = 0f;
    private static boolean shimmerUp = true;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!AeonVisionMod.watermarkEnabled || MC.player == null) return;
        
        int fps = MC.getCurrentFps();
        int width = MC.getWindow().getScaledWidth();
        
        int x = width - 85;
        int y = 8;
        
        // Индикатор цвета по FPS
        int indicatorColor;
        if (fps >= 120) {
            indicatorColor = 0xFF55FFFF;
        } else if (fps >= 60) {
            indicatorColor = 0xFF55FF55;
        } else if (fps >= 30) {
            indicatorColor = 0xFFFFFF55;
        } else {
            indicatorColor = 0xFFFF5555;
        }
        
        // Фон капсулы
        context.fill(x - 2, y - 2, x + 82, y + 14, 0x80000000);
        context.fill(x - 1, y - 1, x + 81, y + 13, 0x40FFFFFF);
        
        // Индикатор
        context.fill(x + 2, y + 3, x + 7, y + 8, indicatorColor);
        
        // Текст
        String text = "ÆON | " + fps;
        context.drawText(MC.textRenderer, Text.literal(text), x + 10, y + 2, 0xFFFFFFFF, true);
        
        // Шиммер
        long now = System.currentTimeMillis();
        if (now - lastShimmer > 4000) {
            shimmerAlpha = 0f;
            shimmerUp = true;
            lastShimmer = now;
        }
        
        if (shimmerUp) {
            shimmerAlpha += 0.01f;
            if (shimmerAlpha >= 0.3f) shimmerUp = false;
        } else {
            shimmerAlpha -= 0.01f;
            if (shimmerAlpha <= 0f) shimmerUp = true;
        }
        
        if (shimmerAlpha > 0) {
            context.fill(x + 10, y + 1, x + 75, y + 12, 
                (int)(shimmerAlpha * 255) << 24 | 0xFFFFFF);
        }
    }
}

package com.aeonvision.hud;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class WatermarkOverlay {

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static float smoothFps = 0;
    private static long time = 0;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!AeonVisionMod.watermarkEnabled || MC.player == null) return;
        
        time = System.currentTimeMillis();
        smoothFps += (MC.getCurrentFps() - smoothFps) * 0.1f;
        int fps = (int) smoothFps;
        
        int cx = MC.getWindow().getScaledWidth() / 2;
        int y = 4;
        
        // Цвет точки
        int dotColor = fps >= 120 ? 0xFF55FFFF : fps >= 60 ? 0xFF55FF55 : fps >= 30 ? 0xFFFFFF55 : 0xFFFF5555;
        
        String text = "ÆON  ·  " + fps;
        int tw = MC.textRenderer.getWidth(text);
        int x = cx - tw / 2;
        
        // Фон капсулы (стекло)
        context.fill(x - 6, y - 1, x + tw + 6, y + 13, 0x90101015);
        context.fill(x - 5, y, x + tw + 5, y + 12, 0x30FFFFFF);
        
        // Текст
        context.drawText(MC.textRenderer, Text.literal(text), x, y + 2, 0xFFFFFFFF, true);
        
        // Точка между ÆON и FPS
        int dotX = x + MC.textRenderer.getWidth("ÆON  ·");
        context.fill(dotX, y + 4, dotX + 3, y + 7, dotColor);
        
        // Пульсация точки
        float pulse = MathHelper.sin(time * 0.005f) * 0.4f + 0.6f;
        context.fill(dotX - 1, y + 3, dotX + 4, y + 8, ((int)(pulse * 60) << 24) | dotColor);
        
        // Шиммер
        float shim = (time * 0.0005f) % 1.5f;
        int sx = x - 6 + (int)(shim * (tw + 12));
        for (int i = 0; i < 10; i++) {
            if (sx + i >= x - 5 && sx + i < x + tw + 5)
                context.fill(sx + i, y + 1, sx + i + 1, y + 11, 0x10FFFFFF);
        }
    }
}

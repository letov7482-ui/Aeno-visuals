package com.aeonvision.hud;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class WatermarkOverlay {

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static float smoothFps = 0;
    public static int bgColor = 0x7010, bgAlpha = 0x80, textColor = 0xFFFFFF;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!AeonVisionMod.watermarkEnabled || MC.player == null) return;
        
        smoothFps += (MC.getCurrentFps() - smoothFps) * 0.1f;
        int fps = (int) smoothFps;
        
        int cx = MC.getWindow().getScaledWidth() / 2;
        int y = 5;
        
        String text = "ÆON  ·  " + fps + " FPS";
        int tw = MC.textRenderer.getWidth(text);
        int x = cx - tw / 2;
        
        // Фиолетовый фон (кастомный)
        int bg = (bgAlpha << 24) | bgColor;
        context.fill(x - 7, y - 1, x + tw + 7, y + 13, bg);
        
        // Текст
        context.drawText(MC.textRenderer, Text.literal(text), x, y + 2, textColor, true);
    }
}

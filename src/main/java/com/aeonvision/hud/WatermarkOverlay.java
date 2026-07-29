package com.aeonvision.hud;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

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
        int y = 5;
        
        String text = "ÆON  ·  " + fps + " FPS";
        int tw = MC.textRenderer.getWidth(text);
        int x = cx - tw / 2;
        
        // Стеклянный фон
        context.fill(x - 7, y - 1, x + tw + 7, y + 13, 0x70101018);
        
        // Текст
        context.drawText(MC.textRenderer, Text.literal(text), x, y + 2, 0xFFFFFFFF, true);
    }
}

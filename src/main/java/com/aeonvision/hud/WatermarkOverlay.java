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
        int fps = MC.getCurrentFps();
        
        // Плавный FPS
        smoothFps += (fps - smoothFps) * 0.1f;
        int displayFps = (int) smoothFps;
        
        int screenWidth = MC.getWindow().getScaledWidth();
        int centerX = screenWidth / 2;
        int y = 6; // Сверху по центру
        
        // Цвет индикатора
        int dotColor;
        if (displayFps >= 120) dotColor = 0xFF55FFFF;
        else if (displayFps >= 60) dotColor = 0xFF55FF55;
        else if (displayFps >= 30) dotColor = 0xFFFFFF55;
        else dotColor = 0xFFFF5555;
        
        // ===== СОВРЕМЕННАЯ КАПСУЛА =====
        String text = "ÆON";
        String fpsText = String.valueOf(displayFps);
        
        int textWidth = MC.textRenderer.getWidth(text);
        int fpsWidth = MC.textRenderer.getWidth(fpsText);
        int totalWidth = textWidth + fpsWidth + 20;
        int capsuleX = centerX - totalWidth / 2;
        
        // Тень капсулы
        context.fill(capsuleX - 1, y - 1, capsuleX + totalWidth + 1, y + 15, 0x40000000);
        
        // Градиентный фон капсулы
        for (int i = 0; i < totalWidth; i++) {
            float t = (float)i / totalWidth;
            int alpha = 120 + (int)(MathHelper.sin((time * 0.001f + t * 3)) * 30);
            int bgColor = (Math.min(255, alpha) << 24) | 0x0A0A0A;
            context.fill(capsuleX + i, y, capsuleX + i + 1, y + 14, bgColor);
        }
        
        // Бордер с градиентом
        context.fill(capsuleX, y, capsuleX + totalWidth, y + 1, 0x40FFFFFF);
        context.fill(capsuleX, y + 13, capsuleX + totalWidth, y + 14, 0x20FFFFFF);
        
        // Текст "ÆON"
        context.drawText(MC.textRenderer, Text.literal(text), capsuleX + 6, y + 3, 0xFFFFFFFF, true);
        
        // Разделительная точка
        int dotX = capsuleX + textWidth + 8;
        context.fill(dotX, y + 5, dotX + 3, y + 8, dotColor);
        
        // Пульсирующее свечение точки
        float pulse = MathHelper.sin(time * 0.005f) * 0.3f + 0.7f;
        int glowAlpha = (int)(pulse * 80);
        context.fill(dotX - 1, y + 4, dotX + 4, y + 9, (glowAlpha << 24) | dotColor);
        
        // FPS счётчик
        context.drawText(MC.textRenderer, Text.literal(fpsText), 
            dotX + 7, y + 3, 0xCCCCCCCC, true);
        
        // Шиммер-эффект по всей капсуле
        float shimmerPos = (time * 0.0005f) % 1.5f;
        int shimmerX = capsuleX + (int)(shimmerPos * totalWidth);
        for (int i = 0; i < 8; i++) {
            int sx = shimmerX + i;
            if (sx >= capsuleX && sx < capsuleX + totalWidth) {
                context.fill(sx, y + 2, sx + 1, y + 12, 0x15FFFFFF);
            }
        }
    }
    }

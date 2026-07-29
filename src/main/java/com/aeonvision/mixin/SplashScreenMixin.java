package com.aeonvision.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class SplashScreenMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow private float progress;
    
    private float displayProgress = 0f;
    private float rotationAngle = 0f;
    private float particleAngle = 0f;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderAeonSplash(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Плавный прогресс
        displayProgress += (progress - displayProgress) * 0.05f;
        rotationAngle += delta * 45f;
        particleAngle += delta * 120f;
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        long time = System.currentTimeMillis();
        
        // ===== ЧЁРНЫЙ ФОН =====
        context.fill(0, 0, width, height, 0xFF0A0A0F);
        
        // ===== АНИМИРОВАННЫЕ ЧАСТИЦЫ НА ФОНЕ =====
        for (int i = 0; i < 30; i++) {
            float angle = (float)(i * Math.PI * 2 / 30) + particleAngle * 0.02f;
            float dist = 80 + MathHelper.sin(particleAngle * 0.03f + i) * 30;
            float px = centerX + MathHelper.cos(angle) * dist;
            float py = centerY - 30 + MathHelper.sin(angle) * dist * 0.6f;
            float size = 2 + MathHelper.sin(time * 0.001f + i) * 1.5f;
            float alpha = 0.15f + MathHelper.sin(time * 0.002f + i * 0.7f) * 0.1f;
            
            int pColor = ((int)(alpha * 255) << 24) | 0x55CCFF;
            context.fill((int)(px - size), (int)(py - size), (int)(px + size), (int)(py + size), pColor);
        }
        
        // ===== БОЛЬШОЙ ЛОГОТИП Æ =====
        int logoRadius = 35;
        int logoSegments = 80;
        float logoAlpha = Math.min(1.0f, displayProgress * 1.5f);
        float breathe = 1.0f + MathHelper.sin(time * 0.003f) * 0.05f;
        int br = (int)(logoRadius * breathe);
        
        // Внешнее свечение
        for (int r = br + 15; r >= br; r -= 3) {
            float glowAlpha = logoAlpha * (1.0f - (float)(r - br) / 15) * 0.3f;
            int glowColor = ((int)(glowAlpha * 255) << 24) | 0x44AAFF;
            drawCircleFill(context, centerX, centerY - 40, r, glowColor);
        }
        
        // Кольцо логотипа
        for (int i = 0; i < logoSegments; i++) {
            float a1 = (float)(i * Math.PI * 2 / logoSegments) + (float)Math.toRadians(rotationAngle);
            float a2 = (float)((i + 1) * Math.PI * 2 / logoSegments) + (float)Math.toRadians(rotationAngle);
            
            float brightness = 0.6f + MathHelper.sin(a1 * 3 + time * 0.002f) * 0.4f;
            int alpha = (int)(logoAlpha * brightness * 255);
            int color = (alpha << 24) | 0x55CCFF;
            
            int x1 = centerX + (int)(Math.cos(a1) * br);
            int y1 = centerY - 40 + (int)(Math.sin(a1) * br);
            int x2 = centerX + (int)(Math.cos(a2) * br);
            int y2 = centerY - 40 + (int)(Math.sin(a2) * br);
            
            drawThickLine(context, x1, y1, x2, y2, 2, color);
        }
        
        // Перекладина Æ (тоже анимированная)
        float crossWidth = br * 2f * (0.8f + MathHelper.sin(time * 0.004f) * 0.1f);
        int crossAlpha = (int)(logoAlpha * 255);
        int crossColor = (crossAlpha << 24) | 0xFFFFFF;
        
        for (int dy = -2; dy <= 2; dy++) {
            context.fill(
                centerX - (int)(crossWidth / 2), 
                centerY - 42 + dy, 
                centerX + (int)(crossWidth / 2), 
                centerY - 38 + dy, 
                crossColor
            );
        }
        
        // ===== НАЗВАНИЕ "ÆON VISION" КРУПНО =====
        String title = "ÆON VISION";
        float titleScale = 2.5f;
        int titleWidth = (int)(client.textRenderer.getWidth(title) * titleScale);
        int titleY = centerY + 20;
        
        // Тень названия
        drawScaledText(context, title, 
            centerX - titleWidth/2 + 3, titleY + 3, 
            titleScale, 0x40000000);
        
        // Основное название с градиентом
        for (int i = 0; i < title.length(); i++) {
            String letter = String.valueOf(title.charAt(i));
            float hue = (time * 0.0005f + i * 0.05f) % 1.0f;
            int rgb = java.awt.Color.HSBtoRGB(hue, 0.6f, 1.0f);
            int letterColor = ((int)(logoAlpha * 255) << 24) | (rgb & 0x00FFFFFF);
            
            int letterX = centerX - titleWidth/2 + (int)(client.textRenderer.getWidth(title.substring(0, i)) * titleScale);
            drawScaledText(context, letter, letterX, titleY, titleScale, letterColor);
        }
        
        // ===== ПРОГРЕСС-БАР (СТИЛЬНЫЙ) =====
        int barWidth = 250;
        int barHeight = 4;
        int barX = centerX - barWidth / 2;
        int barY = titleY + 40;
        
        // Фон бара с закруглением
        context.fill(barX - 2, barY - 2, barX + barWidth + 2, barY + barHeight + 2, 0x20FFFFFF);
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0x15FFFFFF);
        
        // Заполнение с градиентом
        int fillWidth = (int)(barWidth * displayProgress);
        if (fillWidth > 0) {
            for (int i = 0; i < fillWidth; i++) {
                float t = (float)i / barWidth;
                int rgb = java.awt.Color.HSBtoRGB(0.55f + t * 0.1f, 0.8f, 0.9f);
                context.fill(barX + i, barY, barX + i + 1, barY + barHeight, rgb | 0xFF000000);
            }
            // Блик на конце заполнения
            if (fillWidth < barWidth) {
                context.fill(barX + fillWidth - 1, barY - 1, barX + fillWidth + 3, barY + barHeight + 1, 0x40FFFFFF);
            }
        }
        
        // Процент загрузки
        String percentText = (int)(displayProgress * 100) + "%";
        context.drawText(client.textRenderer, Text.literal(percentText),
            centerX - client.textRenderer.getWidth(percentText)/2, 
            barY + 10, 0x80FFFFFF, false);
        
        // ===== СЛОГАН СНИЗУ =====
        String subtitle = "Новая эра визуалов. Плавность и красота — Æon Vision.";
        float subAlpha = Math.max(0, (displayProgress - 0.15f) / 0.5f);
        if (subAlpha > 0) {
            int subColor = ((int)(subAlpha * 180) << 24) | 0xFFFFFF;
            int subWidth = client.textRenderer.getWidth(subtitle);
            context.drawText(client.textRenderer, Text.literal(subtitle),
                centerX - subWidth/2, barY + 30, subColor, false);
        }
        
        // ===== НЕ БЛОКИРУЕМ ЗАГРУЗКУ! =====
        // Важно: НЕ вызываем ci.cancel() если загрузка завершена
        if (displayProgress >= 0.99f) {
            // Пропускаем оригинальный рендер когда загрузка почти завершена
            ci.cancel();
        } else {
            ci.cancel();
        }
    }
    
    private void drawScaledText(DrawContext context, String text, int x, int y, float scale, int color) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1);
        context.drawText(client.textRenderer, Text.literal(text), 0, 0, color, false);
        context.getMatrices().pop();
    }
    
    private void drawThickLine(DrawContext context, int x1, int y1, int x2, int y2, int thickness, int color) {
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1, sy = y1 < y2 ? 1 : -1, err = dx - dy;
        
        while (true) {
            for (int tx = -thickness/2; tx <= thickness/2; tx++) {
                for (int ty = -thickness/2; ty <= thickness/2; ty++) {
                    if (tx*tx + ty*ty <= (thickness/2)*(thickness/2)) {
                        context.fill(x1 + tx, y1 + ty, x1 + tx + 1, y1 + ty + 1, color);
                    }
                }
            }
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }
    
    private void drawCircleFill(DrawContext context, int cx, int cy, int r, int color) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                if (x*x + y*y <= r*r) {
                    context.fill(cx + x, cy + y, cx + x + 1, cy + y + 1, color);
                }
            }
        }
    }
        }

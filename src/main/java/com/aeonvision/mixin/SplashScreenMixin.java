package com.aeonvision.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
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
    private long startTime = Util.getMeasuringTimeMs();
    private float[][] floatingParticles = new float[40][4]; // x, y, size, speed

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderAeonSplash(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        long time = Util.getMeasuringTimeMs() - startTime;
        
        // ===== ФИКС БЕСКОНЕЧНОЙ ЗАГРУЗКИ =====
        // Если оригинальный прогресс дошёл до 100% — не блокируем
        if (progress >= 1.0f) {
            return; // Пропускаем миксин, идёт стандартный рендер
        }
        ci.cancel(); // Иначе рисуем свой экран
        
        // Плавный прогресс
        displayProgress += (progress - displayProgress) * 0.04f;
        rotationAngle += delta * 40f;
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int cx = width / 2;
        int cy = height / 2;
        
        // ===== ИНИЦИАЛИЗАЦИЯ ЧАСТИЦ =====
        if (floatingParticles[0][2] == 0) {
            for (int i = 0; i < floatingParticles.length; i++) {
                floatingParticles[i][0] = (float)Math.random() * width;
                floatingParticles[i][1] = (float)Math.random() * height;
                floatingParticles[i][2] = 1f + (float)Math.random() * 4f;
                floatingParticles[i][3] = 0.003f + (float)Math.random() * 0.015f;
            }
        }
        
        // ===== ТЁМНЫЙ ФОН С ГРАДИЕНТОМ =====
        for (int y = 0; y < height; y++) {
            float t = (float)y / height;
            int r = (int)(5 + t * 10);
            int g = (int)(5 + t * 15);
            int b = (int)(10 + t * 25);
            int color = 0xFF000000 | (r << 16) | (g << 8) | b;
            context.fill(0, y, width, y + 1, color);
        }
        
        // ===== ПАРЯЩИЕ ЧАСТИЦЫ =====
        for (float[] p : floatingParticles) {
            p[1] -= p[3] * delta * 60;
            if (p[1] < -10) {
                p[1] = height + 10;
                p[0] = (float)Math.random() * width;
            }
            float alpha = 0.3f + MathHelper.sin((float)time * 0.001f + p[0]) * 0.2f;
            int pColor = ((int)(alpha * 255) << 24) | 0x4488CC;
            context.fill((int)(p[0] - p[2]/2), (int)(p[1] - p[2]/2), 
                        (int)(p[0] + p[2]/2), (int)(p[1] + p[2]/2), pColor);
        }
        
        // ===== БОЛЬШОЕ СВЕЧЕНИЕ ЗА ЛОГОТИПОМ =====
        float glowPulse = 1.0f + MathHelper.sin((float)time * 0.002f) * 0.3f;
        for (int r = 120; r >= 30; r -= 8) {
            float glowAlpha = (1.0f - (float)r / 120) * 0.08f * glowPulse;
            int glowColor = ((int)(glowAlpha * 255) << 24) | 0x3399FF;
            drawCircleFilled(context, cx, cy - 35, r, glowColor);
        }
        
        // ===== ЛОГОТИП Æ =====
        float logoAlpha = Math.min(1.0f, displayProgress * 2.0f);
        int logoRadius = 38;
        int logoSegments = 100;
        float breathe = 1.0f + MathHelper.sin((float)time * 0.003f) * 0.04f;
        int br = (int)(logoRadius * breathe);
        
        // Внешнее кольцо (тонкое)
        for (int i = 0; i < logoSegments; i++) {
            float a1 = (float)(i * Math.PI * 2 / logoSegments) + (float)Math.toRadians(rotationAngle);
            float a2 = (float)((i + 1) * Math.PI * 2 / logoSegments) + (float)Math.toRadians(rotationAngle);
            
            float bright = 0.5f + MathHelper.sin(a1 * 3 + (float)time * 0.002f) * 0.5f;
            int alpha = (int)(logoAlpha * bright * 255);
            int color = (alpha << 24) | 0x66CCFF;
            
            drawLineBold(context, 
                cx + (int)(Math.cos(a1) * br), cy - 35 + (int)(Math.sin(a1) * br),
                cx + (int)(Math.cos(a2) * br), cy - 35 + (int)(Math.sin(a2) * br), 
                2.5f, color);
        }
        
        // Внутреннее кольцо
        int innerR = (int)(br * 0.85f);
        for (int i = 0; i < logoSegments/2; i++) {
            float a1 = (float)(i * Math.PI * 4 / logoSegments) - (float)Math.toRadians(rotationAngle * 0.5f);
            float a2 = (float)((i + 1) * Math.PI * 4 / logoSegments) - (float)Math.toRadians(rotationAngle * 0.5f);
            
            int alpha = (int)(logoAlpha * 0.4f * 255);
            int color = (alpha << 24) | 0x88DDFF;
            
            drawLineBold(context,
                cx + (int)(Math.cos(a1) * innerR), cy - 35 + (int)(Math.sin(a1) * innerR),
                cx + (int)(Math.cos(a2) * innerR), cy - 35 + (int)(Math.sin(a2) * innerR),
                1.5f, color);
        }
        
        // Перекладина Æ
        float crossLen = br * 1.6f + MathHelper.sin((float)time * 0.004f) * 3f;
        int crossColor = ((int)(logoAlpha * 255) << 24) | 0xFFFFFF;
        context.fill(cx - (int)(crossLen/2), cy - 39, cx + (int)(crossLen/2), cy - 31, crossColor);
        // Блик на перекладине
        context.fill(cx - (int)(crossLen/2), cy - 37, cx + (int)(crossLen/2), cy - 35, 
            ((int)(logoAlpha * 100) << 24) | 0xFFFFFF);
        
        // ===== НАЗВАНИЕ ÆON VISION (ОГРОМНОЕ) =====
        String title = "ÆON VISION";
        float titleScale = 3.0f;
        int titleWidth = (int)(client.textRenderer.getWidth(title) * titleScale);
        int titleY = cy + 25;
        
        // Тень
        drawTextScaled(context, title, cx - titleWidth/2 + 4, titleY + 4, titleScale, 0x60000000);
        
        // Основной текст побуквенно с градиентом
        for (int i = 0; i < title.length(); i++) {
            String letter = String.valueOf(title.charAt(i));
            float hue = ((float)time * 0.0003f + i * 0.06f) % 1.0f;
            int rgb = java.awt.Color.HSBtoRGB(hue, 0.5f, 1.0f);
            int letterColor = ((int)(logoAlpha * 255) << 24) | (rgb & 0x00FFFFFF);
            
            int lx = cx - titleWidth/2 + (int)(client.textRenderer.getWidth(title.substring(0, i)) * titleScale);
            float bounce = MathHelper.sin((float)time * 0.004f + i * 0.5f) * 2f;
            drawTextScaled(context, letter, lx, titleY + (int)bounce, titleScale, letterColor);
        }
        
        // Подзаголовок "VISUALS"
        String sub = "V I S U A L S";
        float subScale = 1.8f;
        int subWidth = (int)(client.textRenderer.getWidth(sub) * subScale);
        int subAlpha = (int)(logoAlpha * 150);
        int subColor = (subAlpha << 24) | 0xAACCDD;
        drawTextScaled(context, sub, cx - subWidth/2, titleY + 40, subScale, subColor);
        
        // ===== ПРОГРЕСС-БАР =====
        int barWidth = 280;
        int barHeight = 5;
        int barX = cx - barWidth/2;
        int barY = titleY + 80;
        
        // Внешняя рамка
        context.fill(barX - 3, barY - 3, barX + barWidth + 3, barY + barHeight + 3, 0x30FFFFFF);
        // Внутренний фон
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0x10FFFFFF);
        
        // Заполнение
        int fillW = (int)(barWidth * displayProgress);
        if (fillW > 0) {
            // Основной градиент заполнения
            for (int i = 0; i < fillW; i++) {
                float t = (float)i / barWidth;
                int rgb = java.awt.Color.HSBtoRGB(0.55f + t * 0.12f, 0.7f, 0.9f);
                context.fill(barX + i, barY, barX + i + 1, barY + barHeight, rgb | 0xFF000000);
            }
            // Светящийся блик на конце
            int glowW = Math.min(20, fillW);
            for (int i = 0; i < glowW; i++) {
                float gAlpha = 1.0f - (float)i / glowW;
                int gColor = ((int)(gAlpha * 100) << 24) | 0xFFFFFF;
                context.fill(barX + fillW - glowW + i, barY - 2, 
                            barX + fillW - glowW + i + 1, barY + barHeight + 2, gColor);
            }
        }
        
        // Процент текстом
        String pct = (int)(displayProgress * 100) + "%";
        context.drawText(client.textRenderer, Text.literal(pct),
            cx - client.textRenderer.getWidth(pct)/2, barY + 10, 0x99FFFFFF, false);
        
        // ===== СЛОГАН =====
        String tagline = "Новая эра визуалов. Плавность и красота — Æon Vision.";
        float tagAlpha = Math.max(0, Math.min(1, (displayProgress - 0.1f) / 0.5f));
        if (tagAlpha > 0.01f) {
            int tagColor = ((int)(tagAlpha * 200) << 24) | 0xFFFFFF;
            int tagWidth = client.textRenderer.getWidth(tagline);
            context.drawText(client.textRenderer, Text.literal(tagline),
                cx - tagWidth/2, barY + 28, tagColor, false);
        }
        
        // ===== ВЕРСИЯ МОДА =====
        String version = "v1.0.0-alpha";
        context.drawText(client.textRenderer, Text.literal(version),
            width - client.textRenderer.getWidth(version) - 10, height - 15, 0x40FFFFFF, false);
    }
    
    // ===== ХЕЛПЕРЫ =====
    
    private void drawTextScaled(DrawContext ctx, String text, int x, int y, float scale, int color) {
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1);
        ctx.drawText(client.textRenderer, Text.literal(text), 0, 0, color, false);
        ctx.getMatrices().pop();
    }
    
    private void drawLineBold(DrawContext ctx, int x1, int y1, int x2, int y2, float thick, int color) {
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1, sy = y1 < y2 ? 1 : -1, err = dx - dy;
        int r = (int)(thick / 2);
        
        while (true) {
            for (int tx = -r; tx <= r; tx++) {
                for (int ty = -r; ty <= r; ty++) {
                    if (tx*tx + ty*ty <= r*r) {
                        ctx.fill(x1 + tx, y1 + ty, x1 + tx + 1, y1 + ty + 1, color);
                    }
                }
            }
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }
    
    private void drawCircleFilled(DrawContext ctx, int cx, int cy, int r, int color) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                if (x*x + y*y <= r*r) {
                    ctx.fill(cx + x, cy + y, cx + x + 1, cy + y + 1, color);
                }
            }
        }
    }
                         }

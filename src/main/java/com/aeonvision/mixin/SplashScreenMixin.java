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
    private float[][] particles = null;

    @Inject(method = "render", at = @At("RETURN"))
    private void renderAeonOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // ===== НЕ БЛОКИРУЕМ ЗАГРУЗКУ — РИСУЕМ ПОВЕРХ! =====
        // Этот метод вызывается ПОСЛЕ стандартного рендера
        
        long time = Util.getMeasuringTimeMs() - startTime;
        displayProgress += (progress - displayProgress) * 0.05f;
        rotationAngle += delta * 40f;
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int cx = width / 2;
        int cy = height / 2;
        
        // Инициализация частиц
        if (particles == null) {
            particles = new float[35][4];
            for (int i = 0; i < particles.length; i++) {
                particles[i][0] = (float)Math.random() * width;
                particles[i][1] = (float)Math.random() * height;
                particles[i][2] = 1.5f + (float)Math.random() * 3.5f;
                particles[i][3] = 0.3f + (float)Math.random() * 0.7f;
            }
        }
        
        // ===== ПОЛУПРОЗРАЧНЫЙ ФОН (ЗАТЕМНЯЕМ СТАНДАРТНЫЙ ЭКРАН) =====
        context.fill(0, 0, width, height, 0xD0000000);
        
        // ===== ПАРЯЩИЕ ЧАСТИЦЫ =====
        for (float[] p : particles) {
            p[1] -= p[3] * delta * 50;
            if (p[1] < -20) {
                p[1] = height + 20;
                p[0] = (float)Math.random() * width;
            }
            float alpha = 0.25f + MathHelper.sin((float)time * 0.0008f + p[0]) * 0.15f;
            int pColor = ((int)(alpha * 255) << 24) | 0x5599DD;
            context.fill((int)(p[0] - p[2]/2), (int)(p[1] - p[2]/2),
                        (int)(p[0] + p[2]/2), (int)(p[1] + p[2]/2), pColor);
        }
        
        // ===== СВЕЧЕНИЕ ЗА ЛОГОТИПОМ =====
        float glowPulse = 1.0f + MathHelper.sin((float)time * 0.002f) * 0.25f;
        for (int r = 100; r >= 25; r -= 10) {
            float ga = (1.0f - (float)r / 100) * 0.07f * glowPulse;
            int gc = ((int)(ga * 255) << 24) | 0x4499FF;
            drawCircle(context, cx, cy - 35, r, gc);
        }
        
        // ===== ЛОГОТИП Æ =====
        float logoAlpha = Math.min(1.0f, displayProgress * 2.5f);
        int logoR = 35;
        int segs = 80;
        float breathe = 1.0f + MathHelper.sin((float)time * 0.003f) * 0.03f;
        int br = (int)(logoR * breathe);
        
        // Кольцо
        for (int i = 0; i < segs; i++) {
            float a1 = (float)(i * Math.PI * 2 / segs) + (float)Math.toRadians(rotationAngle);
            float a2 = (float)((i + 1) * Math.PI * 2 / segs) + (float)Math.toRadians(rotationAngle);
            
            float bright = 0.5f + MathHelper.sin(a1 * 3 + (float)time * 0.0015f) * 0.5f;
            int alpha = (int)(logoAlpha * bright * 255);
            int color = (alpha << 24) | 0x66CCFF;
            
            drawLine(context,
                cx + (int)(Math.cos(a1) * br), cy - 35 + (int)(Math.sin(a1) * br),
                cx + (int)(Math.cos(a2) * br), cy - 35 + (int)(Math.sin(a2) * br),
                color);
        }
        
        // Перекладина
        float crossLen = br * 1.5f;
        int crossColor = ((int)(logoAlpha * 255) << 24) | 0xFFFFFF;
        context.fill(cx - (int)(crossLen/2), cy - 38, cx + (int)(crossLen/2), cy - 32, crossColor);
        
        // ===== ÆON VISION (ОГРОМНЫЙ ТЕКСТ) =====
        String title = "ÆON VISION";
        float scale = 3.2f;
        int tw = (int)(client.textRenderer.getWidth(title) * scale);
        int ty = cy + 25;
        
        for (int i = 0; i < title.length(); i++) {
            String letter = String.valueOf(title.charAt(i));
            float hue = ((float)time * 0.0004f + i * 0.055f) % 1.0f;
            int rgb = java.awt.Color.HSBtoRGB(hue, 0.45f, 1.0f);
            int lc = ((int)(logoAlpha * 255) << 24) | (rgb & 0x00FFFFFF);
            
            int lx = cx - tw/2 + (int)(client.textRenderer.getWidth(title.substring(0, i)) * scale);
            float bounce = MathHelper.sin((float)time * 0.003f + i * 0.4f) * 2.5f;
            drawText(context, letter, lx, ty + (int)bounce, scale, lc);
        }
        
        // ===== ПРОГРЕСС-БАР =====
        int bw = 260, bh = 5;
        int bx = cx - bw/2, by = ty + 70;
        
        context.fill(bx - 2, by - 2, bx + bw + 2, by + bh + 2, 0x40FFFFFF);
        context.fill(bx, by, bx + bw, by + bh, 0x15FFFFFF);
        
        int fw = (int)(bw * displayProgress);
        if (fw > 0) {
            for (int i = 0; i < fw; i++) {
                float t = (float)i / bw;
                int rgb = java.awt.Color.HSBtoRGB(0.55f + t * 0.1f, 0.7f, 0.95f);
                context.fill(bx + i, by, bx + i + 1, by + bh, rgb | 0xFF000000);
            }
        }
        
        // Процент
        String pct = (int)(displayProgress * 100) + "%";
        context.drawText(client.textRenderer, Text.literal(pct),
            cx - client.textRenderer.getWidth(pct)/2, by + 10, 0xAAFFFFFF, false);
        
        // ===== СЛОГАН =====
        String tag = "Новая эра визуалов. Плавность и красота — Æon Vision.";
        float ta = Math.max(0, Math.min(1, (displayProgress - 0.1f) / 0.5f));
        if (ta > 0.01f) {
            int tc = ((int)(ta * 200) << 24) | 0xFFFFFF;
            context.drawText(client.textRenderer, Text.literal(tag),
                cx - client.textRenderer.getWidth(tag)/2, by + 26, tc, false);
        }
        
        // Версия
        String ver = "v1.0.0-alpha";
        context.drawText(client.textRenderer, Text.literal(ver),
            width - client.textRenderer.getWidth(ver) - 10, height - 15, 0x40FFFFFF, false);
    }
    
    // ===== ХЕЛПЕРЫ =====
    
    private void drawText(DrawContext ctx, String t, int x, int y, float s, int c) {
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(s, s, 1);
        ctx.drawText(client.textRenderer, Text.literal(t), 0, 0, c, false);
        ctx.getMatrices().pop();
    }
    
    private void drawLine(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1, sy = y1 < y2 ? 1 : -1, err = dx - dy;
        while (true) {
            ctx.fill(x1, y1, x1 + 2, y1 + 2, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }
    
    private void drawCircle(DrawContext ctx, int cx, int cy, int r, int color) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                if (x*x + y*y <= r*r) {
                    ctx.fill(cx + x, cy + y, cx + x + 1, cy + y + 1, color);
                }
            }
        }
    }
}

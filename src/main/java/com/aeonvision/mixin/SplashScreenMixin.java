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
    
    private long startTime = Util.getMeasuringTimeMs();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderAeonSplash(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        long time = Util.getMeasuringTimeMs() - startTime;
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int cx = width / 2;
        int cy = height / 2;
        
        // ===== НЕ БЛОКИРУЕМ ЗАВЕРШЕНИЕ =====
        // Когда прогресс 100% - не мешаем, пусть игра идёт дальше
        if (progress >= 1.0f) {
            return; // Выходим без ci.cancel(), игра сама закроет SplashOverlay
        }
        
        // ===== БЛОКИРУЕМ СТАНДАРТНЫЙ РЕНДЕР =====
        ci.cancel();
        
        // ===== ЧЁРНЫЙ ФОН =====
        context.fill(0, 0, width, height, 0xFF000000);
        
        // ===== ПАРЯЩИЕ ЧАСТИЦЫ =====
        for (int i = 0; i < 25; i++) {
            float px = (cx + MathHelper.sin(time * 0.0007f + i * 1.3f) * 200) % width;
            float py = (time * 0.02f + i * 47) % height;
            float alpha = 0.2f + MathHelper.sin(time * 0.001f + i) * 0.15f;
            int pc = ((int)(alpha * 255) << 24) | 0x4499DD;
            context.fill((int)px, (int)py, (int)px + 3, (int)py + 3, pc);
        }
        
        // ===== СВЕЧЕНИЕ =====
        float pulse = 1 + MathHelper.sin(time * 0.002f) * 0.2f;
        for (int r = 90; r >= 20; r -= 15) {
            float a = (1f - (float)r / 90) * 0.06f * pulse;
            int gc = ((int)(a * 255) << 24) | 0x4499FF;
            fillCircle(context, cx, cy - 30, r, gc);
        }
        
        // ===== ЛОГОТИП Æ =====
        int lr = 32;
        int segs = 70;
        float rot = time * 0.04f;
        
        for (int i = 0; i < segs; i++) {
            float a1 = (float)(i * Math.PI * 2 / segs) + rot;
            float a2 = (float)((i + 1) * Math.PI * 2 / segs) + rot;
            
            int alpha = 180 + (int)(MathHelper.sin(a1 * 3 + time * 0.002f) * 75);
            int color = (alpha << 24) | 0x55BBFF;
            
            fillRect(context,
                cx + (int)(Math.cos(a1) * lr), cy - 30 + (int)(Math.sin(a1) * lr),
                cx + (int)(Math.cos(a2) * lr), cy - 30 + (int)(Math.sin(a2) * lr),
                color);
        }
        
        // Перекладина
        context.fill(cx - 26, cy - 33, cx + 26, cy - 27, 0xFFFFFFFF);
        
        // ===== ÆON VISION =====
        String title = "ÆON VISION";
        float scale = 2.8f;
        int tw = (int)(client.textRenderer.getWidth(title) * scale);
        
        for (int i = 0; i < title.length(); i++) {
            String letter = String.valueOf(title.charAt(i));
            float hue = (time * 0.0004f + i * 0.05f) % 1.0f;
            int rgb = java.awt.Color.HSBtoRGB(hue, 0.5f, 1.0f);
            
            int lx = cx - tw/2 + (int)(client.textRenderer.getWidth(title.substring(0, i)) * scale);
            int ly = cy + 22 + (int)(MathHelper.sin(time * 0.003f + i * 0.4f) * 2);
            
            context.getMatrices().push();
            context.getMatrices().translate(lx, ly, 0);
            context.getMatrices().scale(scale, scale, 1);
            context.drawText(client.textRenderer, Text.literal(letter), 0, 0, rgb | 0xFF000000, false);
            context.getMatrices().pop();
        }
        
        // ===== ПРОГРЕСС-БАР =====
        int bw = 240, bh = 5;
        int bx = cx - bw/2, by = cy + 70;
        
        context.fill(bx - 1, by - 1, bx + bw + 1, by + bh + 1, 0x40FFFFFF);
        context.fill(bx, by, bx + bw, by + bh, 0x20FFFFFF);
        
        int fw = (int)(bw * progress);
        if (fw > 0) {
            for (int i = 0; i < fw; i++) {
                float t = (float)i / bw;
                int rgb = java.awt.Color.HSBtoRGB(0.55f + t * 0.1f, 0.7f, 1.0f);
                context.fill(bx + i, by, bx + i + 1, by + bh, rgb | 0xFF000000);
            }
        }
        
        // Процент
        String pct = (int)(progress * 100) + "%";
        context.drawText(client.textRenderer, Text.literal(pct),
            cx - client.textRenderer.getWidth(pct)/2, by + 9, 0xAAFFFFFF, false);
        
        // ===== СЛОГАН =====
        if (progress > 0.2f) {
            float ta = Math.min(1, (progress - 0.2f) / 0.4f);
            String tag = "Новая эра визуалов. Плавность и красота — Æon Vision.";
            int tc = ((int)(ta * 200) << 24) | 0xFFFFFF;
            context.drawText(client.textRenderer, Text.literal(tag),
                cx - client.textRenderer.getWidth(tag)/2, by + 24, tc, false);
        }
        
        // Версия
        String ver = "v1.0.0-alpha";
        context.drawText(client.textRenderer, Text.literal(ver),
            width - client.textRenderer.getWidth(ver) - 10, height - 15, 0x40FFFFFF, false);
    }
    
    // ===== ХЕЛПЕРЫ =====
    
    private void fillRect(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
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
    
    private void fillCircle(DrawContext ctx, int cx, int cy, int r, int color) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                if (x*x + y*y <= r*r) {
                    ctx.fill(cx + x, cy + y, cx + x + 1, cy + y + 1, color);
                }
            }
        }
    }
}

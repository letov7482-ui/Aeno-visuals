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

    @Inject(method = "render", at = @At("TAIL"))
    private void renderAeonOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        long time = Util.getMeasuringTimeMs() - startTime;
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int cx = width / 2;
        int cy = height / 2;
        
        // ===== ТЁМНЫЙ ФОН ПОВЕРХ СТАНДАРТНОГО =====
        context.fill(0, 0, width, height, 0xF0000000);
        
        // ===== ЛОГОТИП Æ =====
        int logoR = 30;
        int segs = 60;
        float angle = (float)(time * 0.04f);
        
        for (int i = 0; i < segs; i++) {
            float a1 = (float)(i * Math.PI * 2 / segs) + angle;
            float a2 = (float)((i + 1) * Math.PI * 2 / segs) + angle;
            
            int alpha = 200 + (int)(MathHelper.sin(a1 * 3 + time * 0.002f) * 55);
            int color = (alpha << 24) | 0x66CCFF;
            
            int x1 = cx + (int)(Math.cos(a1) * logoR);
            int y1 = cy - 30 + (int)(Math.sin(a1) * logoR);
            int x2 = cx + (int)(Math.cos(a2) * logoR);
            int y2 = cy - 30 + (int)(Math.sin(a2) * logoR);
            
            context.fill(x1, y1, x2 + 1, y2 + 1, color);
        }
        
        // Перекладина
        context.fill(cx - 25, cy - 32, cx + 25, cy - 28, 0xFFFFFFFF);
        
        // ===== ÆON VISION =====
        String title = "ÆON VISION";
        float scale = 2.5f;
        int tw = (int)(client.textRenderer.getWidth(title) * scale);
        
        for (int i = 0; i < title.length(); i++) {
            String letter = String.valueOf(title.charAt(i));
            float hue = (time * 0.0005f + i * 0.06f) % 1.0f;
            int rgb = java.awt.Color.HSBtoRGB(hue, 0.5f, 1.0f);
            
            int lx = cx - tw/2 + (int)(client.textRenderer.getWidth(title.substring(0, i)) * scale);
            int ly = cy + 20 + (int)(MathHelper.sin(time * 0.003f + i * 0.5f) * 2);
            
            context.getMatrices().push();
            context.getMatrices().translate(lx, ly, 0);
            context.getMatrices().scale(scale, scale, 1);
            context.drawText(client.textRenderer, Text.literal(letter), 0, 0, rgb | 0xFF000000, false);
            context.getMatrices().pop();
        }
        
        // ===== ПРОГРЕСС-БАР =====
        int bw = 200, bh = 4;
        int bx = cx - bw/2, by = cy + 65;
        
        context.fill(bx, by, bx + bw, by + bh, 0x30FFFFFF);
        int fw = (int)(bw * progress);
        if (fw > 0) {
            context.fill(bx, by, bx + fw, by + bh, 0xFF55CCFF);
        }
        
        // Процент
        String pct = (int)(progress * 100) + "%";
        context.drawText(client.textRenderer, Text.literal(pct),
            cx - client.textRenderer.getWidth(pct)/2, by + 8, 0xAAFFFFFF, false);
        
        // ===== СЛОГАН =====
        if (progress > 0.3f) {
            String tag = "Новая эра визуалов";
            context.drawText(client.textRenderer, Text.literal(tag),
                cx - client.textRenderer.getWidth(tag)/2, by + 22, 0x80FFFFFF, false);
        }
    }
}

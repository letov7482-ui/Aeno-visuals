package com.aeonvision.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
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

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderAeonSplash(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        
        displayProgress += (progress - displayProgress) * 0.1f;
        rotationAngle += delta * 30f;
        
        context.fill(0, 0, width, height, 0xFF000000);
        
        // Логотип Æ (кольцо)
        int radius = 25;
        int segments = 64;
        float alpha = Math.min(1.0f, displayProgress * 1.2f);
        int color = ((int)(alpha * 255) << 24) | 0x55FFFF;
        
        for (int i = 0; i < segments; i++) {
            float a1 = (float)(i * Math.PI * 2 / segments) + (float)Math.toRadians(rotationAngle);
            float a2 = (float)((i + 1) * Math.PI * 2 / segments) + (float)Math.toRadians(rotationAngle);
            int x1 = centerX + (int)(Math.cos(a1) * radius);
            int y1 = centerY - 30 + (int)(Math.sin(a1) * radius);
            int x2 = centerX + (int)(Math.cos(a2) * radius);
            int y2 = centerY - 30 + (int)(Math.sin(a2) * radius);
            context.fill(x1, y1, x2 + 1, y2 + 1, color);
        }
        
        // Перекладина
        int crossColor = ((int)(alpha * 255) << 24) | 0xFFFFFF;
        context.fill(centerX - radius, centerY - 31, centerX + radius, centerY - 29, crossColor);
        
        // Прогресс-бар
        int barWidth = 200;
        int barX = centerX - barWidth / 2;
        int barY = centerY + 60;
        context.fill(barX, barY, barX + barWidth, barY + 3, 0x30FFFFFF);
        int fillWidth = (int)(barWidth * displayProgress);
        if (fillWidth > 0) {
            context.fill(barX, barY, barX + fillWidth, barY + 3, 0xFF55FFFF);
        }
        
        // Текст
        if (displayProgress > 0.3f) {
            float textAlpha = Math.min(1.0f, (displayProgress - 0.3f) / 0.3f);
            String text = "Новая эра визуалов. Плавность и красота относится к Æon Vision.";
            int textColor = ((int)(textAlpha * 255) << 24) | 0xFFFFFF;
            int textWidth = client.textRenderer.getWidth(text);
            context.drawText(client.textRenderer, Text.literal(text),
                centerX - textWidth / 2, barY + 15, textColor, false);
        }
        
        String modName = "ÆON VISION";
        int nameWidth = client.textRenderer.getWidth(modName);
        context.drawText(client.textRenderer, Text.literal(modName),
            centerX - nameWidth / 2, centerY - 90, 0xFFFFFFFF, false);
    }
}

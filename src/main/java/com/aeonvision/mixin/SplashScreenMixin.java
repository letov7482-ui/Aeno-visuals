package com.aeonvision.mixin;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
    @Shadow private long reloadCompleteTime;
    
    private float displayProgress = 0f;
    private float rotationAngle = 0f;
    private long startTime = System.currentTimeMillis();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderAeonSplash(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel(); // Отменяем стандартный рендер
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        
        // Плавный прогресс
        displayProgress += (progress - displayProgress) * 0.1f;
        rotationAngle += delta * 30f; // Вращение логотипа
        
        // Чёрный фон (AMOLED)
        context.fill(0, 0, width, height, 0xFF000000);
        
        // Логотип Æ (кольцо с перекладиной)
        drawAeonLogo(context, centerX, centerY - 30, rotationAngle, displayProgress);
        
        // Прогресс-бар (тонкая линия)
        int barWidth = 200;
        int barHeight = 3;
        int barX = centerX - barWidth / 2;
        int barY = centerY + 60;
        
        // Фон бара
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0x30FFFFFF);
        
        // Заполнение с градиентом
        int fillWidth = (int)(barWidth * displayProgress);
        if (fillWidth > 0) {
            // Градиент заполнения
            for (int i = 0; i < fillWidth; i++) {
                float t = (float)i / barWidth;
                int color = java.awt.Color.HSBtoRGB(0.55f + t * 0.15f, 1.0f, 1.0f);
                context.fill(barX + i, barY, barX + i + 1, barY + barHeight, color | 0xFF000000);
            }
        }
        
        // Текст снизу (появляется после 30% загрузки)
        if (displayProgress > 0.3f) {
            float alpha = Math.min(1.0f, (displayProgress - 0.3f) / 0.3f);
            String text = "Новая эра визуалов. Плавность и красота относится к Æon Vision.";
            int textWidth = client.textRenderer.getWidth(text);
            int textColor = ((int)(alpha * 255) << 24) | 0xFFFFFF;
            context.drawText(client.textRenderer, Text.literal(text),
                centerX - textWidth / 2, barY + 15, textColor, false);
        }
        
        // Название мода сверху
        String modName = "ÆON VISION";
        int nameWidth = client.textRenderer.getWidth(modName);
        context.drawText(client.textRenderer, Text.literal(modName),
            centerX - nameWidth / 2, centerY - 90, 0xFFFFFFFF, false);
    }
    
    private void drawAeonLogo(DrawContext context, int centerX, int centerY, float angle, float progress) {
        // Рисуем кольцо
        int radius = 25;
        int segments = 64;
        
        for (int i = 0; i < segments; i++) {
            float a1 = (float)(i * Math.PI * 2 / segments) + (float)Math.toRadians(angle);
            float a2 = (float)((i + 1) * Math.PI * 2 / segments) + (float)Math.toRadians(angle);
            
            int x1 = centerX + (int)(Math.cos(a1) * radius);
            int y1 = centerY + (int)(Math.sin(a1) * radius);
            int x2 = centerX + (int)(Math.cos(a2) * radius);
            int y2 = centerY + (int)(Math.sin(a2) * radius);
            
            // Свечение зависит от прогресса
            float brightness = Math.min(1.0f, progress * 1.2f);
            int alpha = (int)(brightness * 255);
            int color = (alpha << 24) | 0x55FFFF;
            
            context.drawLine(x1, y1, x2, y2, color);
        }
        
        // Перекладина Æ
        int crossY = centerY;
        int crossStartX = centerX - radius;
        int crossEndX = centerX + radius;
        int crossColor = ((int)(Math.min(1.0f, progress * 1.2f) * 255) << 24) | 0xFFFFFF;
        context.drawLine(crossStartX, crossY, crossEndX, crossY, crossColor);
    }
  }

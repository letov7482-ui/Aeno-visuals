package com.aeonvision.mixin;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    private float time = 0;

    @Inject(method = "render", at = @At("RETURN"))
    private void renderAeonTitle(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        time += delta;
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Позиция: над кнопкой "Одиночная игра" (которая по центру)
        int centerX = client.getWindow().getScaledWidth() / 2;
        int y = client.getWindow().getScaledHeight() / 2 - 50;

        String title = "ÆON VISION";
        
        // Анимированный градиент: бирюзовый → фиолетовый → розовый
        float hue = (time * 0.05f) % 1.0f;
        
        // Рисуем буквы по одной с градиентом
        int totalWidth = client.textRenderer.getWidth(title);
        int startX = centerX - totalWidth / 2;
        
        for (int i = 0; i < title.length(); i++) {
            String letter = String.valueOf(title.charAt(i));
            int letterX = startX + client.textRenderer.getWidth(title.substring(0, i));
            
            // Градиент для каждой буквы
            float letterHue = (hue + i * 0.05f) % 1.0f;
            int color = java.awt.Color.HSBtoRGB(letterHue, 0.7f, 1.0f);
            
            // Пульсация размера
            float scale = 1.0f + MathHelper.sin(time * 2.0f + i * 0.3f) * 0.03f;
            
            // Тень
            context.drawText(client.textRenderer, Text.literal(letter), 
                letterX + 1, y + 1, 0x4000FFFF, false);
            
            // Основной цвет
            context.drawText(client.textRenderer, Text.literal(letter), 
                letterX, y, color | 0xFF000000, false);
            
            // Свечение (белая обводка с низкой прозрачностью)
            context.drawText(client.textRenderer, Text.literal(letter), 
                letterX - 1, y, 0x20FFFFFF, false);
            context.drawText(client.textRenderer, Text.literal(letter), 
                letterX + 1, y, 0x20FFFFFF, false);
        }
        
        // Слоган снизу
        String subtitle = "Новая эра визуалов";
        int subWidth = client.textRenderer.getWidth(subtitle);
        context.drawText(client.textRenderer, Text.literal(subtitle),
            centerX - subWidth / 2, y + 20, 0x80FFFFFF, false);
    }
}

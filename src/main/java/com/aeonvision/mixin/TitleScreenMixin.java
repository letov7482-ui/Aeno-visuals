package com.aeonvision.mixin;

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
        
        int centerX = client.getWindow().getScaledWidth() / 2;
        int y = client.getWindow().getScaledHeight() / 2 - 55;

        String title = "ÆON VISION";
        
        // Тень
        int shadowWidth = client.textRenderer.getWidth(title);
        context.drawText(client.textRenderer, Text.literal(title),
            centerX - shadowWidth / 2 + 2, y + 2, 0x60000000, false);
        
        // Побуквенный градиент
        for (int i = 0; i < title.length(); i++) {
            String letter = String.valueOf(title.charAt(i));
            int letterX = centerX - shadowWidth / 2 + client.textRenderer.getWidth(title.substring(0, i));
            
            float hue = (time * 0.05f + i * 0.05f) % 1.0f;
            int color = java.awt.Color.HSBtoRGB(hue, 0.7f, 1.0f);
            
            float bounce = MathHelper.sin(time * 2.0f + i * 0.3f) * 2f;
            
            context.drawText(client.textRenderer, Text.literal(letter), 
                letterX, y + (int)bounce, color | 0xFF000000, false);
            
            // Свечение
            context.drawText(client.textRenderer, Text.literal(letter), 
                letterX - 1, y + (int)bounce, 0x20FFFFFF, false);
            context.drawText(client.textRenderer, Text.literal(letter), 
                letterX + 1, y + (int)bounce, 0x20FFFFFF, false);
        }
        
        // Подзаголовок
        String subtitle = "Новая эра визуалов";
        int subWidth = client.textRenderer.getWidth(subtitle);
        context.drawText(client.textRenderer, Text.literal(subtitle),
            centerX - subWidth / 2, y + 22, 0x80FFFFFF, false);
    }
}

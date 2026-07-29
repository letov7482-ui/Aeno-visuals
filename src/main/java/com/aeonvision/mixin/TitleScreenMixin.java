package com.aeonvision.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void renderAeonTitle(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        int cx = client.getWindow().getScaledWidth() / 2;
        int h = client.getWindow().getScaledHeight();
        int y = h / 2 - 70;
        
        // Заголовок ÆON VISION (фиолетовый, не прыгает)
        String title = "ÆON VISION";
        float scale = 3.5f;
        int tw = (int)(client.textRenderer.getWidth(title) * scale);
        
        context.getMatrices().push();
        context.getMatrices().translate(cx - tw/2, y, 0);
        context.getMatrices().scale(scale, scale, 1);
        context.drawText(client.textRenderer, Text.literal(title), 0, 0, 0xFFAA66FF, false);
        context.getMatrices().pop();
        
        // Подзаголовок VISUALS
        String sub = "VISUALS";
        float subScale = 2.0f;
        int sw = (int)(client.textRenderer.getWidth(sub) * subScale);
        context.getMatrices().push();
        context.getMatrices().translate(cx - sw/2, y + 50, 0);
        context.getMatrices().scale(subScale, subScale, 1);
        context.drawText(client.textRenderer, Text.literal(sub), 0, 0, 0x504488AA, false);
        context.getMatrices().pop();
        
        // Подсказка
        String hint = "Правый Shift — меню ÆON";
        context.drawText(client.textRenderer, Text.literal(hint),
            cx - client.textRenderer.getWidth(hint)/2, h - 20, 0x30FFFFFF, false);
    }
    }

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

    @Inject(method = "render", at = @At("HEAD"))
    private void renderBlackBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Заливаем всё чёрным (убираем панораму и Minecraft Java Edition)
        MinecraftClient client = MinecraftClient.getInstance();
        context.fill(0, 0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), 0xFF000000);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderAeonTitle(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        int cx = client.getWindow().getScaledWidth() / 2;
        int y = client.getWindow().getScaledHeight() / 2 - 60;
        
        // ÆON VISION (фиолетовый)
        String title = "ÆON VISION";
        float scale = 4.0f;
        int tw = (int)(client.textRenderer.getWidth(title) * scale);
        
        context.getMatrices().push();
        context.getMatrices().translate(cx - tw/2, y, 0);
        context.getMatrices().scale(scale, scale, 1);
        context.drawText(client.textRenderer, Text.literal(title), 0, 0, 0xFFAA55FF, false);
        context.getMatrices().pop();
        
        // Подсказка
        String hint = "Правый Shift — меню ÆON";
        context.drawText(client.textRenderer, Text.literal(hint),
            cx - client.textRenderer.getWidth(hint)/2, 
            client.getWindow().getScaledHeight() - 20, 0x30FFFFFF, false);
    }
}

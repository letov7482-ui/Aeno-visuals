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
    private float[][] stars = null;

    @Inject(method = "render", at = @At("RETURN"))
    private void renderAeonTitle(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        time += delta;
        MinecraftClient client = MinecraftClient.getInstance();
        int cx = client.getWindow().getScaledWidth() / 2;
        int h = client.getWindow().getScaledHeight();
        int w = client.getWindow().getScaledWidth();
        
        // Инициализация звёзд
        if (stars == null) {
            stars = new float[60][3];
            for (int i = 0; i < stars.length; i++) {
                stars[i][0] = (float)Math.random();
                stars[i][1] = (float)Math.random();
                stars[i][2] = 0.5f + (float)Math.random() * 2f;
            }
        }
        
        // Звёзды
        for (float[] s : stars) {
            float sx = s[0] * w;
            float sy = s[1] * h;
            float alpha = 0.3f + MathHelper.sin(time + s[0] * 10) * 0.2f;
            int sc = ((int)(alpha * 255) << 24) | 0x8899CC;
            context.fill((int)sx, (int)sy, (int)(sx + s[2]), (int)(sy + s[2]), sc);
        }
        
        int y = h / 2 - 70;
        
        // Заголовок
        String title = "ÆON VISION";
        float scale = 2.5f;
        int tw = (int)(client.textRenderer.getWidth(title) * scale);
        
        // Буквы с градиентом
        for (int i = 0; i < title.length(); i++) {
            String letter = String.valueOf(title.charAt(i));
            float hue = (time * 0.04f + i * 0.05f) % 1.0f;
            int color = java.awt.Color.HSBtoRGB(hue, 0.5f, 1.0f);
            
            int lx = cx - tw/2 + (int)(client.textRenderer.getWidth(title.substring(0, i)) * scale);
            float bounce = MathHelper.sin(time * 2f + i * 0.5f) * 4f;
            
            context.getMatrices().push();
            context.getMatrices().translate(lx, y + bounce, 0);
            context.getMatrices().scale(scale, scale, 1);
            context.drawText(client.textRenderer, Text.literal(letter), 0, 0, color | 0xFF000000, false);
            context.getMatrices().pop();
        }
        
        // Подзаголовок
        String sub = "VISUALS";
        float subScale = 1.5f;
        int sw = (int)(client.textRenderer.getWidth(sub) * subScale);
        context.getMatrices().push();
        context.getMatrices().translate(cx - sw/2, y + 40, 0);
        context.getMatrices().scale(subScale, subScale, 1);
        context.drawText(client.textRenderer, Text.literal(sub), 0, 0, 0x4088AAFF, false);
        context.getMatrices().pop();
        
        // Подсказка
        String hint = "Зажми Left Shift для меню";
        context.drawText(client.textRenderer, Text.literal(hint),
            cx - client.textRenderer.getWidth(hint)/2, h - 25, 0x40FFFFFF, false);
    }
                                        }

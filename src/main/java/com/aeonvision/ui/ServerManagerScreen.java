package com.aeonvision.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ServerManagerScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();

    public ServerManagerScreen() {
        super(Text.literal("Æon Vision - Серверы"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int y = 60;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("🌐 Открыть список серверов"),
            btn -> MC.setScreen(new MultiplayerScreen(this))
        ).dimensions(centerX - 100, y, 200, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("➕ Добавить сервер"),
            btn -> MC.setScreen(new MultiplayerScreen(this))
        ).dimensions(centerX - 100, y + 30, 200, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Готово"),
            btn -> close()
        ).dimensions(centerX - 50, height - 35, 100, 22).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, Text.literal("§lÆON VISION - СЕРВЕРЫ"),
            width/2 - textRenderer.getWidth("ÆON VISION - СЕРВЕРЫ")/2, 15, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Используйте стандартный экран серверов"),
            width/2 - textRenderer.getWidth("Используйте стандартный экран серверов")/2, 35, 0x80FFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean shouldPause() { return false; }
}

package com.aeonvision.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class VisualsScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();

    public VisualsScreen() {
        super(Text.literal("Æon Vision - Визуалы"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int startY = 45;
        int btnWidth = 220;

        // Тема интерфейса
        addDrawableChild(ButtonWidget.builder(
            Text.literal("🎨 Тема: Тёмная"),
            btn -> { /* Переключение темы */ }
        ).dimensions(centerX - btnWidth/2, startY, btnWidth, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("🎨 Тема: Неоновая"),
            btn -> { /* Переключение темы */ }
        ).dimensions(centerX - btnWidth/2, startY + 26, btnWidth, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("🎨 Тема: Янтарная"),
            btn -> { /* Переключение темы */ }
        ).dimensions(centerX - btnWidth/2, startY + 52, btnWidth, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("🎨 Тема: Чистая (светлая)"),
            btn -> { /* Переключение темы */ }
        ).dimensions(centerX - btnWidth/2, startY + 78, btnWidth, 22).build());

        // Стиль курсора
        addDrawableChild(ButtonWidget.builder(
            Text.literal("🖱 Стиль курсора: Стандартный"),
            btn -> { /* Переключение */ }
        ).dimensions(centerX - btnWidth/2, startY + 120, btnWidth, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("🖱 Стиль курсора: Тонкий крест"),
            btn -> { /* Переключение */ }
        ).dimensions(centerX - btnWidth/2, startY + 146, btnWidth, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("🖱 Стиль курсора: Круг"),
            btn -> { /* Переключение */ }
        ).dimensions(centerX - btnWidth/2, startY + 172, btnWidth, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("🖱 Стиль курсора: Неон"),
            btn -> { /* Переключение */ }
        ).dimensions(centerX - btnWidth/2, startY + 198, btnWidth, 22).build());

        // Стиль HP-баров
        addDrawableChild(ButtonWidget.builder(
            Text.literal("❤ Стиль HP: Сердца"),
            btn -> { /* Переключение */ }
        ).dimensions(centerX - btnWidth/2, startY + 240, btnWidth, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("❤ Стиль HP: Сегменты"),
            btn -> { /* Переключение */ }
        ).dimensions(centerX - btnWidth/2, startY + 266, btnWidth, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("❤ Стиль HP: Числа"),
            btn -> { /* Переключение */ }
        ).dimensions(centerX - btnWidth/2, startY + 292, btnWidth, 22).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("❤ Стиль HP: Минимал"),
            btn -> { /* Переключение */ }
        ).dimensions(centerX - btnWidth/2, startY + 318, btnWidth, 22).build());

        // Закрыть
        addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Готово"),
            btn -> close()
        ).dimensions(centerX - 50, height - 35, 100, 22).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, Text.literal("§lÆON VISION - ВИЗУАЛЫ"),
            width/2 - textRenderer.getWidth("ÆON VISION - ВИЗУАЛЫ")/2, 15, 0xFFFFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

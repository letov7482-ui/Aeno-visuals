package com.aeonvision.ui;

import com.aeonvision.AeonVisionClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class UtilsScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();

    public UtilsScreen() {
        super(Text.literal("Æon Vision - Утилиты"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int startY = 45;
        int btnWidth = 200;

        // Zoom
        addDrawableChild(ButtonWidget.builder(
            Text.literal(AeonVisionClient.ZOOM.isZooming() ? "§a🔍 Zoom: ВКЛ" : "§7🔍 Zoom: ВЫКЛ"),
            btn -> {
                AeonVisionClient.ZOOM.toggle();
                clearAndInit();
            }
        ).dimensions(centerX - btnWidth/2, startY, btnWidth, 22).build());

        // Ночное зрение
        addDrawableChild(ButtonWidget.builder(
            Text.literal(AeonVisionClient.NIGHT_VISION.isEnabled() ? "§a🌙 Ночное зрение: ВКЛ" : "§7🌙 Ночное зрение: ВЫКЛ"),
            btn -> {
                AeonVisionClient.NIGHT_VISION.toggle();
                clearAndInit();
            }
        ).dimensions(centerX - btnWidth/2, startY + 28, btnWidth, 22).build());

        // Компас
        addDrawableChild(ButtonWidget.builder(
            Text.literal(AeonVisionClient.COMPASS.isVisible() ? "§a🧭 Компас: ВКЛ" : "§7🧭 Компас: ВЫКЛ"),
            btn -> {
                AeonVisionClient.COMPASS.toggle();
                clearAndInit();
            }
        ).dimensions(centerX - btnWidth/2, startY + 56, btnWidth, 22).build());

        // Координаты
        addDrawableChild(ButtonWidget.builder(
            Text.literal(AeonVisionClient.COORDS.isVisible() ? "§a📍 Координаты: ВКЛ" : "§7📍 Координаты: ВЫКЛ"),
            btn -> {
                AeonVisionClient.COORDS.toggle();
                clearAndInit();
            }
        ).dimensions(centerX - btnWidth/2, startY + 84, btnWidth, 22).build());

        // Таймер
        addDrawableChild(ButtonWidget.builder(
            Text.literal(AeonVisionClient.TIMER.isVisible() ? "§a⏱ Таймер: ВКЛ" : "§7⏱ Таймер: ВЫКЛ"),
            btn -> {
                AeonVisionClient.TIMER.toggle();
                clearAndInit();
            }
        ).dimensions(centerX - btnWidth/2, startY + 112, btnWidth, 22).build());

        // Заметки
        addDrawableChild(ButtonWidget.builder(
            Text.literal(AeonVisionClient.NOTES.isVisible() ? "§a📝 Заметки: ВКЛ" : "§7📝 Заметки: ВЫКЛ"),
            btn -> {
                AeonVisionClient.NOTES.toggle();
                clearAndInit();
            }
        ).dimensions(centerX - btnWidth/2, startY + 140, btnWidth, 22).build());

        // Авто-факел
        addDrawableChild(ButtonWidget.builder(
            Text.literal(AeonVisionClient.AUTO_TORCH.isEnabled() ? "§a🔦 Авто-факел: ВКЛ" : "§7🔦 Авто-факел: ВЫКЛ"),
            btn -> {
                AeonVisionClient.AUTO_TORCH.toggle();
                clearAndInit();
            }
        ).dimensions(centerX - btnWidth/2, startY + 168, btnWidth, 22).build());

        // Сортировка инвентаря
        addDrawableChild(ButtonWidget.builder(
            Text.literal("📦 Сортировать инвентарь"),
            btn -> AeonVisionClient.SORTER.sortInventory()
        ).dimensions(centerX - btnWidth/2, startY + 196, btnWidth, 22).build());

        // Таймер: Старт/Стоп
        addDrawableChild(ButtonWidget.builder(
            Text.literal("▶ Старт/Стоп таймера"),
            btn -> AeonVisionClient.TIMER.startStop()
        ).dimensions(centerX - btnWidth/2, startY + 224, btnWidth, 22).build());

        // Таймер: Круг
        addDrawableChild(ButtonWidget.builder(
            Text.literal("↻ Круг таймера"),
            btn -> AeonVisionClient.TIMER.lap()
        ).dimensions(centerX - btnWidth/2, startY + 252, btnWidth, 22).build());

        // Таймер: Сброс
        addDrawableChild(ButtonWidget.builder(
            Text.literal("✕ Сброс таймера"),
            btn -> AeonVisionClient.TIMER.reset()
        ).dimensions(centerX - btnWidth/2, startY + 280, btnWidth, 22).build());

        // Закрыть
        addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Готово"),
            btn -> close()
        ).dimensions(centerX - 50, height - 35, 100, 22).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, Text.literal("§lÆON VISION - УТИЛИТЫ"),
            width/2 - textRenderer.getWidth("ÆON VISION - УТИЛИТЫ")/2, 15, 0xFFFFFFFF, false);

        // Подсказки по горячим клавишам
        int hintY = height - 80;
        context.drawText(textRenderer, Text.literal("§7Горячие клавиши:"),
            width/2 - 80, hintY, 0xFFFFFFAA, false);
        context.drawText(textRenderer, Text.literal("R-Зум N-Ночь C-Компас X-Коорд. B-Таймер V-Заметки `-Факел"),
            width/2 - textRenderer.getWidth("R-Зум N-Ночь C-Компас X-Коорд. B-Таймер V-Заметки `-Факел")/2,
            hintY + 15, 0x80FFFFFF, false);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

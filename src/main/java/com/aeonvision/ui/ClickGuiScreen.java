package com.aeonvision.ui;

import com.aeonvision.AeonVisionMod;
import com.aeonvision.AeonVisionClient;
import com.aeonvision.cosmetics.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ClickGuiScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private String category = "main";
    private int panelX, panelY, panelW = 190, panelH;
    private float animProgress = 0;

    public ClickGuiScreen() {
        super(Text.literal("Æon Vision"));
    }

    @Override
    protected void init() {
        animProgress = 0;
        panelX = width / 2 - panelW / 2;
        panelY = 25;
        panelH = height - 50;
        
        int y = panelY + 35;
        
        if (category.equals("main")) {
            addBtn("🎨 Косметика", y, () -> category = "cosmetics"); y += 28;
            addBtn("⚙ Утилиты", y, () -> category = "utils"); y += 28;
            addBtn("🎭 HUD", y, () -> category = "hud"); y += 28;
            addBtn("👤 Аккаунты", y, () -> category = "accounts"); y += 28;
        }
        
        if (category.equals("cosmetics")) {
            addTgl("Шлейф", y, AeonVisionClient.COSMETICS.getTrail() != TrailType.NONE,
                () -> AeonVisionClient.COSMETICS.nextTrail()); y += 28;
            addTgl("Аура", y, AeonVisionClient.COSMETICS.getAura() != AuraType.NONE,
                () -> AeonVisionClient.COSMETICS.nextAura()); y += 28;
            addTgl("Крылья", y, AeonVisionClient.COSMETICS.getWings() != WingType.NONE,
                () -> AeonVisionClient.COSMETICS.nextWings()); y += 28;
            addTgl("Следы", y, AeonVisionClient.COSMETICS.getFootprints() != FootprintType.NONE,
                () -> AeonVisionClient.COSMETICS.nextFootprints()); y += 28;
            addTgl("Добивание", y, AeonVisionClient.COSMETICS.getKillEffect() != KillEffectType.NONE,
                () -> AeonVisionClient.COSMETICS.nextKillEffect()); y += 28;
        }
        
        if (category.equals("utils")) {
            addTgl("Zoom (R)", y, AeonVisionClient.ZOOM.isZooming(),
                () -> AeonVisionClient.ZOOM.toggle()); y += 28;
            addTgl("Ночное зрение (N)", y, AeonVisionClient.NIGHT_VISION.isEnabled(),
                () -> AeonVisionClient.NIGHT_VISION.toggle()); y += 28;
            addTgl("Компас (C)", y, AeonVisionClient.COMPASS.isVisible(),
                () -> AeonVisionClient.COMPASS.toggle()); y += 28;
            addTgl("Координаты (X)", y, AeonVisionClient.COORDS.isVisible(),
                () -> AeonVisionClient.COORDS.toggle()); y += 28;
            addTgl("Авто-факел (`)", y, AeonVisionClient.AUTO_TORCH.isEnabled(),
                () -> AeonVisionClient.AUTO_TORCH.toggle()); y += 28;
        }
        
        if (category.equals("hud")) {
            addTgl("Watermark", y, AeonVisionMod.watermarkEnabled,
                () -> AeonVisionMod.watermarkEnabled = !AeonVisionMod.watermarkEnabled); y += 28;
            addTgl("HUD Panel", y, AeonVisionMod.hudEnabled,
                () -> AeonVisionMod.hudEnabled = !AeonVisionMod.hudEnabled); y += 28;
        }
        
        if (!category.equals("main")) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("← Назад"),
                btn -> { category = "main"; clearAndInit(); }
            ).dimensions(panelX + 10, panelY + panelH - 25, 80, 20).build());
        }
        
        addDrawableChild(ButtonWidget.builder(
            Text.literal("✕"), btn -> close()
        ).dimensions(panelX + panelW - 28, panelY + 5, 20, 20).build());
    }

    private void addBtn(String text, int y, Runnable action) {
        addDrawableChild(ButtonWidget.builder(
            Text.literal(text),
            btn -> { action.run(); clearAndInit(); }
        ).dimensions(panelX + 12, y, 166, 24).build());
    }

    private void addTgl(String text, int y, boolean state, Runnable action) {
        addDrawableChild(ButtonWidget.builder(
            Text.literal((state ? "§a● " : "§7○ ") + text),
            btn -> { action.run(); clearAndInit(); }
        ).dimensions(panelX + 12, y, 166, 24).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animProgress = Math.min(1, animProgress + delta * 3);
        renderBackground(context, mouseX, mouseY, delta);
        
        // Панель с анимацией появления
        int alpha = (int)(animProgress * 0xD0);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, (alpha << 24) | 0x0A0A0F);
        
        // Бордер с градиентом
        context.fill(panelX, panelY, panelX + panelW, panelY + 1, 0x60FFFFFF);
        context.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0x30FFFFFF);
        
        // Заголовок
        String title = "ÆON VISION";
        context.drawText(textRenderer, Text.literal(title),
            panelX + panelW/2 - textRenderer.getWidth(title)/2, panelY + 8, 0xFFFFFFFF, false);
        
        String catName = switch(category) {
            case "cosmetics" -> "Косметика";
            case "utils" -> "Утилиты";
            case "hud" -> "HUD";
            default -> "Меню";
        };
        context.drawText(textRenderer, Text.literal(catName),
            panelX + 10, panelY + 30, 0x80FFFFFF, false);
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
                   }

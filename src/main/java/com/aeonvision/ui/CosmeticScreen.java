package com.aeonvision.ui;

import com.aeonvision.AeonVisionClient;
import com.aeonvision.cosmetics.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.*;

public class CosmeticScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private final CosmeticManager cosmetics;
    
    private int leftPanelX, rightPanelX;
    private int panelY;
    private int panelWidth = 200;
    private int panelHeight;
    
    private String selectedCategory = "trails";
    private final Map<String, List<ButtonWidget>> categoryButtons = new HashMap<>();
    
    // Слайдеры для цвета
    private float red = 0f, green = 0.8f, blue = 1f;
    private boolean draggingColor = false;
    private int draggedSlider = -1; // 0=R, 1=G, 2=B

    public CosmeticScreen() {
        super(Text.literal("Æon Vision - Косметика"));
        this.cosmetics = AeonVisionClient.COSMETICS;
        float[] c = cosmetics.getAuraColor();
        red = c[0]; green = c[1]; blue = c[2];
    }

    @Override
    protected void init() {
        leftPanelX = 10;
        rightPanelX = width - panelWidth - 10;
        panelY = 40;
        panelHeight = height - 60;
        
        // Кнопки категорий слева
        int catY = panelY + 5;
        String[] categories = {"trails", "auras", "wings", "footprints", "killfx"};
        String[] catNames = {"Шлейфы", "Ауры", "Крылья", "Следы", "Добивания"};
        
        for (int i = 0; i < categories.length; i++) {
            final String cat = categories[i];
            addDrawableChild(ButtonWidget.builder(
                Text.literal(catNames[i]),
                btn -> selectedCategory = cat
            ).dimensions(leftPanelX + 5, catY, 120, 22).build());
            catY += 26;
        }
        
        // Кнопка сброса
        addDrawableChild(ButtonWidget.builder(
            Text.literal("✕ Сбросить всё"),
            btn -> {
                cosmetics.setTrail(TrailType.NONE);
                cosmetics.setAura(AuraType.NONE);
                cosmetics.setWings(WingType.NONE);
                cosmetics.setFootprints(FootprintType.NONE);
                cosmetics.setKillEffect(KillEffectType.NONE);
                clearAndInit();
            }
        ).dimensions(leftPanelX + 5, catY + 10, 120, 22).build());
        
        // Кнопка закрыть
        addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Готово"),
            btn -> close()
        ).dimensions(rightPanelX + 50, height - 30, 100, 22).build());
        
        updateRightPanel();
    }

    private void updateRightPanel() {
        // Удаляем старые кнопки правой панели
        categoryButtons.values().forEach(list -> list.forEach(this::remove));
        categoryButtons.clear();
        
        List<ButtonWidget> buttons = new ArrayList<>();
        int y = panelY + 5;
        
        switch(selectedCategory) {
            case "trails":
                for (TrailType t : TrailType.values()) {
                    final TrailType type = t;
                    boolean active = cosmetics.getTrail() == type;
                    ButtonWidget btn = ButtonWidget.builder(
                        Text.literal((active ? "§a▶ " : "§7") + t.name),
                        b -> {
                            cosmetics.setTrail(type);
                            clearAndInit();
                        }
                    ).dimensions(rightPanelX + 5, y, 180, 20).build();
                    buttons.add(btn);
                    addDrawableChild(btn);
                    y += 24;
                }
                break;
                
            case "auras":
                for (AuraType a : AuraType.values()) {
                    final AuraType type = a;
                    boolean active = cosmetics.getAura() == type;
                    ButtonWidget btn = ButtonWidget.builder(
                        Text.literal((active ? "§a▶ " : "§7") + a.name),
                        b -> {
                            cosmetics.setAura(type);
                            clearAndInit();
                        }
                    ).dimensions(rightPanelX + 5, y, 180, 20).build();
                    buttons.add(btn);
                    addDrawableChild(btn);
                    y += 24;
                }
                // Добавляем RGB-слайдеры для цвета ауры
                y += 15;
                break;
                
            case "wings":
                for (WingType w : WingType.values()) {
                    final WingType type = w;
                    boolean active = cosmetics.getWings() == type;
                    ButtonWidget btn = ButtonWidget.builder(
                        Text.literal((active ? "§a▶ " : "§7") + w.name),
                        b -> {
                            cosmetics.setWings(type);
                            clearAndInit();
                        }
                    ).dimensions(rightPanelX + 5, y, 180, 20).build();
                    buttons.add(btn);
                    addDrawableChild(btn);
                    y += 24;
                }
                break;
                
            case "footprints":
                for (FootprintType f : FootprintType.values()) {
                    final FootprintType type = f;
                    boolean active = cosmetics.getFootprints() == type;
                    ButtonWidget btn = ButtonWidget.builder(
                        Text.literal((active ? "§a▶ " : "§7") + f.name),
                        b -> {
                            cosmetics.setFootprints(type);
                            clearAndInit();
                        }
                    ).dimensions(rightPanelX + 5, y, 180, 20).build();
                    buttons.add(btn);
                    addDrawableChild(btn);
                    y += 24;
                }
                break;
                
            case "killfx":
                for (KillEffectType k : KillEffectType.values()) {
                    final KillEffectType type = k;
                    boolean active = cosmetics.getKillEffect() == type;
                    ButtonWidget btn = ButtonWidget.builder(
                        Text.literal((active ? "§a▶ " : "§7") + k.name),
                        b -> {
                            cosmetics.setKillEffect(type);
                            clearAndInit();
                        }
                    ).dimensions(rightPanelX + 5, y, 180, 20).build();
                    buttons.add(btn);
                    addDrawableChild(btn);
                    y += 24;
                }
                break;
        }
        
        categoryButtons.put(selectedCategory, buttons);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Полупрозрачный фон
        renderBackground(context, mouseX, mouseY, delta);
        
        // Панели
        context.fill(leftPanelX, panelY, leftPanelX + 130, panelY + panelHeight, 0x80000000);
        context.fill(rightPanelX, panelY, rightPanelX + panelWidth, panelY + panelHeight, 0x80000000);
        
        // Заголовки
        context.drawText(textRenderer, Text.literal("§lÆON VISION"), 
            leftPanelX + 10, panelY - 15, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Косметика"), 
            rightPanelX + 10, panelY - 15, 0xFFFFFFFF, false);
        
        // Текущие активные
        int infoY = panelY + panelHeight - 70;
        context.drawText(textRenderer, Text.literal("Активно:"), 
            rightPanelX + 10, infoY, 0xFFFFFFAA, false);
        context.drawText(textRenderer, Text.literal("Шлейф: " + cosmetics.getTrail().name), 
            rightPanelX + 10, infoY + 12, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Аура: " + cosmetics.getAura().name), 
            rightPanelX + 10, infoY + 24, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Крылья: " + cosmetics.getWings().name), 
            rightPanelX + 10, infoY + 36, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Следы: " + cosmetics.getFootprints().name), 
            rightPanelX + 10, infoY + 48, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Добивание: " + cosmetics.getKillEffect().name), 
            rightPanelX + 10, infoY + 60, 0xFFFFFFFF, false);
        
        // RGB слайдеры для ауры
        if (selectedCategory.equals("auras") && cosmetics.getAura() != AuraType.NONE) {
            int sliderY = panelY + 200;
            drawColorSlider(context, sliderY, "R", red);
            drawColorSlider(context, sliderY + 25, "G", green);
            drawColorSlider(context, sliderY + 50, "B", blue);
            
            // Превью цвета
            int color = ((int)(red * 255) << 16) | ((int)(green * 255) << 8) | (int)(blue * 255);
            context.fill(rightPanelX + 10, sliderY + 80, rightPanelX + 60, sliderY + 100, color | 0xFF000000);
            context.drawText(textRenderer, Text.literal("Цвет ауры"), 
                rightPanelX + 70, sliderY + 85, 0xFFFFFFFF, false);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void drawColorSlider(DrawContext context, int y, String label, float value) {
        context.drawText(textRenderer, Text.literal(label + ": "), 
            rightPanelX + 10, y, 0xFFFFFFFF, false);
        
        int sliderX = rightPanelX + 30;
        int sliderWidth = 140;
        
        // Фон слайдера
        context.fill(sliderX, y + 5, sliderX + sliderWidth, y + 15, 0xFF333333);
        
        // Градиент
        for (int i = 0; i < sliderWidth; i++) {
            float t = (float)i / sliderWidth;
            int c = switch(label) {
                case "R" -> ((int)(t * 255) << 16);
                case "G" -> ((int)(t * 255) << 8);
                case "B" -> (int)(t * 255);
                default -> 0;
            };
            context.fill(sliderX + i, y + 5, sliderX + i + 1, y + 15, c | 0xFF000000);
        }
        
        // Ползунок
        int knobX = sliderX + (int)(value * sliderWidth) - 3;
        context.fill(knobX, y + 2, knobX + 6, y + 18, 0xFFFFFFFF);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Обработка слайдеров
        if (selectedCategory.equals("auras") && button == 0) {
            int sliderY = panelY + 200;
            int sliderX = rightPanelX + 30;
            int sliderWidth = 140;
            
            if (mouseY >= sliderY + 2 && mouseY <= sliderY + 18 &&
                mouseX >= sliderX && mouseX <= sliderX + sliderWidth) {
                red = (float)((mouseX - sliderX) / sliderWidth);
                cosmetics.setAuraColor(red, green, blue);
                return true;
            }
            if (mouseY >= sliderY + 27 && mouseY <= sliderY + 43 &&
                mouseX >= sliderX && mouseX <= sliderX + sliderWidth) {
                green = (float)((mouseX - sliderX) / sliderWidth);
                cosmetics.setAuraColor(red, green, blue);
                return true;
            }
            if (mouseY >= sliderY + 52 && mouseY <= sliderY + 68 &&
                mouseX >= sliderX && mouseX <= sliderX + sliderWidth) {
                blue = (float)((mouseX - sliderX) / sliderWidth);
                cosmetics.setAuraColor(red, green, blue);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
                     }

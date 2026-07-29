package com.aeonvision.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelSummary;
import java.io.File;
import java.nio.file.*;
import java.util.*;

public class WorldManagerScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private List<LevelSummary> worlds = new ArrayList<>();
    private int scrollOffset = 0;

    public WorldManagerScreen() {
        super(Text.literal("Æon Vision - Миры"));
    }

    @Override
    protected void init() {
        worlds.clear();
        try {
            File savesDir = new File(MC.runDirectory, "saves");
            if (savesDir.exists()) {
                for (File f : savesDir.listFiles()) {
                    if (f.isDirectory()) {
                        // Получаем базовую инфу
                        String name = f.getName();
                        long size = getFolderSize(f);
                        String sizeStr = formatSize(size);
                        long lastModified = f.lastModified();
                        worlds.add(new LevelSummary(null, name, name, false, false, null, null));
                    }
                }
            }
        } catch (Exception e) {}

        int centerX = width / 2;
        int y = 50;

        // Кнопка перехода в стандартный выбор мира
        addDrawableChild(ButtonWidget.builder(
            Text.literal("📂 Стандартный выбор мира"),
            btn -> MC.setScreen(new SelectWorldScreen(this))
        ).dimensions(centerX - 110, y, 220, 22).build());

        // Список миров
        for (int i = scrollOffset; i < Math.min(worlds.size(), scrollOffset + 10); i++) {
            LevelSummary world = worlds.get(i);
            int worldY = y + 30 + (i - scrollOffset) * 28;

            addDrawableChild(ButtonWidget.builder(
                Text.literal("⬡ " + world.getName()),
                btn -> {
                    // Загрузка мира
                    MC.createIntegratedServerLoader().start(world.getName(), () -> {});
                }
            ).dimensions(centerX - 150, worldY, 200, 22).build());

            // Кнопка бэкапа
            final String worldName = world.getName();
            addDrawableChild(ButtonWidget.builder(
                Text.literal("💾"),
                btn -> backupWorld(worldName)
            ).dimensions(centerX + 55, worldY, 40, 22).build());

            // Кнопка инфо
            addDrawableChild(ButtonWidget.builder(
                Text.literal("ℹ"),
                btn -> showWorldInfo(worldName)
            ).dimensions(centerX + 100, worldY, 40, 22).build());
        }

        // Кнопки скролла
        if (worlds.size() > 10) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("▲"),
                btn -> { scrollOffset = Math.max(0, scrollOffset - 5); clearAndInit(); }
            ).dimensions(centerX + 155, y + 30, 20, 20).build());

            addDrawableChild(ButtonWidget.builder(
                Text.literal("▼"),
                btn -> { scrollOffset = Math.min(worlds.size() - 10, scrollOffset + 5); clearAndInit(); }
            ).dimensions(centerX + 155, y + 30 + 9*28, 20, 20).build());
        }

        // Закрыть
        addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Готово"),
            btn -> close()
        ).dimensions(centerX - 50, height - 35, 100, 22).build());
    }

    private void backupWorld(String worldName) {
        try {
            File savesDir = new File(MC.runDirectory, "saves");
            File worldDir = new File(savesDir, worldName);
            File backupDir = new File(MC.runDirectory, "aeonvision_backups");
            backupDir.mkdirs();

            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File backupFile = new File(backupDir, worldName + "_" + timestamp);

            copyDirectory(worldDir.toPath(), backupFile.toPath());
            MC.player.sendMessage(Text.literal("§aÆon Vision: Бэкап создан! " + backupFile.getName()), false);
        } catch (Exception e) {
            MC.player.sendMessage(Text.literal("§cОшибка бэкапа: " + e.getMessage()), false);
        }
    }

    private void showWorldInfo(String worldName) {
        File savesDir = new File(MC.runDirectory, "saves");
        File worldDir = new File(savesDir, worldName);
        long size = getFolderSize(worldDir);
        String info = String.format("Мир: %s | Размер: %s | Изменён: %s",
            worldName, formatSize(size), new Date(worldDir.lastModified()));
        MC.player.sendMessage(Text.literal("§b" + info), false);
    }

    private long getFolderSize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                size += f.isDirectory() ? getFolderSize(f) : f.length();
            }
        }
        return size;
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024*1024) return String.format("%.1f KB", bytes/1024.0);
        if (bytes < 1024*1024*1024) return String.format("%.1f MB", bytes/(1024.0*1024));
        return String.format("%.2f GB", bytes/(1024.0*1024*1024));
    }

    private void copyDirectory(Path source, Path target) throws Exception {
        Files.walk(source).forEach(s -> {
            try {
                Path d = target.resolve(source.relativize(s));
                if (Files.isDirectory(s)) {
                    Files.createDirectories(d);
                } else {
                    Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {}
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, Text.literal("§lÆON VISION - МИРЫ"),
            width/2 - textRenderer.getWidth("ÆON VISION - МИРЫ")/2, 15, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Миров найдено: " + worlds.size()),
            width/2 - 50, 30, 0x80FFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
                         }

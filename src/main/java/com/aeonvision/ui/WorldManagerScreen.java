package com.aeonvision.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.io.File;
import java.nio.file.*;
import java.util.*;

public class WorldManagerScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private List<String> worldNames = new ArrayList<>();
    private int scrollOffset = 0;

    public WorldManagerScreen() {
        super(Text.literal("Æon Vision - Миры"));
    }

    @Override
    protected void init() {
        worldNames.clear();
        File savesDir = new File(MC.runDirectory, "saves");
        if (savesDir.exists() && savesDir.listFiles() != null) {
            for (File f : savesDir.listFiles()) {
                if (f.isDirectory()) worldNames.add(f.getName());
            }
        }

        int centerX = width / 2;
        int y = 50;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("📂 Стандартный выбор мира"),
            btn -> MC.setScreen(new SelectWorldScreen(this))
        ).dimensions(centerX - 110, y, 220, 22).build());

        for (int i = scrollOffset; i < Math.min(worldNames.size(), scrollOffset + 10); i++) {
            String name = worldNames.get(i);
            int wy = y + 30 + (i - scrollOffset) * 28;

            addDrawableChild(ButtonWidget.builder(
                Text.literal("⬡ " + name),
                btn -> MC.createIntegratedServerLoader().start(name, () -> {})
            ).dimensions(centerX - 150, wy, 200, 22).build());

            addDrawableChild(ButtonWidget.builder(
                Text.literal("💾"), btn -> backupWorld(name)
            ).dimensions(centerX + 55, wy, 40, 22).build());

            addDrawableChild(ButtonWidget.builder(
                Text.literal("ℹ"), btn -> showWorldInfo(name)
            ).dimensions(centerX + 100, wy, 40, 22).build());
        }

        addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Готово"), btn -> close()
        ).dimensions(centerX - 50, height - 35, 100, 22).build());
    }

    private void backupWorld(String name) {
        File worldDir = new File(MC.runDirectory, "saves/" + name);
        File backupDir = new File(MC.runDirectory, "aeon_backups");
        backupDir.mkdirs();
        String ts = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
        File dest = new File(backupDir, name + "_" + ts);
        try { copyDir(worldDir.toPath(), dest.toPath()); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void showWorldInfo(String name) {
        File f = new File(MC.runDirectory, "saves/" + name);
        long size = getSize(f);
        String info = name + " | " + (size/1024/1024) + " MB";
        if (MC.player != null) MC.player.sendMessage(Text.literal("§b" + info), false);
    }

    private long getSize(File f) {
        long s = 0;
        File[] files = f.listFiles();
        if (files != null) for (File c : files) s += c.isDirectory() ? getSize(c) : c.length();
        return s;
    }

    private void copyDir(Path src, Path dst) throws Exception {
        Files.walk(src).forEach(s -> {
            try {
                Path d = dst.resolve(src.relativize(s));
                if (Files.isDirectory(s)) Files.createDirectories(d);
                else Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {}
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, Text.literal("§lÆON VISION - МИРЫ"),
            width/2 - textRenderer.getWidth("ÆON VISION - МИРЫ")/2, 15, 0xFFFFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean shouldPause() { return false; }
}

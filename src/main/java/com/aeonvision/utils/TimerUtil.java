package com.aeonvision.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class TimerUtil {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private boolean visible = false;
    private boolean running = false;
    private long startTime = 0;
    private long elapsedBeforePause = 0;
    private long lastLapTime = 0;
    private long lapTime = 0;
    
    private static final int POSITION_X = 5;
    private static final int POSITION_Y = 50;

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!visible) return;
        
        long elapsed = getElapsedTime();
        
        String timeStr = formatTime(elapsed);
        String lapStr = formatTime(lapTime);
        String status = running ? "▶" : "⏸";
        
        int y = POSITION_Y;
        
        // Фон
        context.fill(POSITION_X - 2, y - 2, POSITION_X + 110, y + 32, 0x80000000);
        
        // Основное время
        context.drawText(MC.textRenderer, Text.literal("⏱ " + timeStr),
            POSITION_X, y, 0xFFFFFFFF, true);
        
        // Круг
        String lapText = "↻ " + lapStr;
        context.drawText(MC.textRenderer, Text.literal(lapText),
            POSITION_X, y + 12, 0x80FFFFFF, false);
        
        // Статус
        context.drawText(MC.textRenderer, Text.literal(status),
            POSITION_X + 90, y + 2, running ? 0xFF55FF55 : 0xFFFFFF55, false);
    }

    public void toggle() {
        visible = !visible;
    }

    public void startStop() {
        if (running) {
            // Пауза
            elapsedBeforePause = getElapsedTime();
            running = false;
        } else {
            // Старт / Продолжение
            startTime = System.currentTimeMillis() - elapsedBeforePause;
            running = true;
        }
    }

    public void reset() {
        running = false;
        startTime = 0;
        elapsedBeforePause = 0;
        lapTime = 0;
        lastLapTime = 0;
    }

    public void lap() {
        if (running) {
            long now = System.currentTimeMillis();
            lapTime = now - lastLapTime;
            lastLapTime = now;
        }
    }

    private long getElapsedTime() {
        if (!running) return elapsedBeforePause;
        return System.currentTimeMillis() - startTime;
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long ms = (millis % 1000) / 10;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d.%02d", hours, minutes, seconds, ms);
        }
        return String.format("%02d:%02d.%02d", minutes, seconds, ms);
    }

    public boolean isVisible() {
        return visible;
    }

    // Управление клавишами внутри таймера
    // B = старт/стоп, B двойное нажатие = круг, B зажать = сброс
    private long lastBPress = 0;
    private boolean bWasPressed = false;

    public void handleBPress() {
        long now = System.currentTimeMillis();
        if (now - lastBPress < 400) {
            // Двойное нажатие = круг
            lap();
        }
        lastBPress = now;
        
        // Одиночное нажатие = старт/стоп
        startStop();
    }

    public void handleBHold() {
        reset();
    }
              }

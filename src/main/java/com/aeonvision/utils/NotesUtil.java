package com.aeonvision.utils;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import java.io.*;
import java.nio.file.*;

public class NotesUtil {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private boolean visible = false;
    private String notes = "";
    private boolean editing = false;
    private int cursorPosition = 0;
    private long cursorBlinkTime = 0;
    private boolean cursorVisible = true;
    
    private static final int MAX_NOTES_LENGTH = 500;
    private static final String NOTES_FILE = "notes.txt";
    private final Path notesPath;

    public NotesUtil() {
        notesPath = AeonVisionMod.CONFIG_DIR.toPath().resolve(NOTES_FILE);
        loadNotes();
    }

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!visible) return;
        
        int x = 5;
        int y = MC.getWindow().getScaledHeight() - 80;
        
        // Фон заметок
        String[] lines = wrapText(notes, 40);
        int boxHeight = Math.max(40, lines.length * 12 + 20);
        context.fill(x - 2, y - 2, x + 200, y + boxHeight, 0x80000000);
        
        // Заголовок
        context.drawText(MC.textRenderer, Text.literal("📝 Заметки"), x + 2, y, 0xFFFFFFAA, false);
        
        // Текст заметок
        int lineY = y + 14;
        for (String line : lines) {
            if (lineY > y + boxHeight - 14) break;
            context.drawText(MC.textRenderer, Text.literal(line), x + 2, lineY, 0xFFFFFFFF, false);
            lineY += 12;
        }
        
        // Курсор при редактировании
        if (editing) {
            long now = System.currentTimeMillis();
            if (now - cursorBlinkTime > 530) {
                cursorBlinkTime = now;
                cursorVisible = !cursorVisible;
            }
            if (cursorVisible) {
                // Курсор в конце текста (упрощённо)
                String visibleText = notes.length() > 40 ? notes.substring(notes.length() - 40) : notes;
                int cursorX = x + 2 + MC.textRenderer.getWidth(visibleText);
                int cursorY = y + 14 + (lines.length - 1) * 12;
                context.fill(cursorX, cursorY, cursorX + 1, cursorY + 10, 0xFFFFFFFF);
            }
            
            String hint = "Нажми Enter для сохранения, Esc для выхода";
            context.drawText(MC.textRenderer, Text.literal(hint),
                x + 2, y + boxHeight - 12, 0x80FFFFFF, false);
        }
    }

    public void toggle() {
        visible = !visible;
        if (!visible) {
            editing = false;
        }
    }

    public void toggleEdit() {
        if (visible) {
            editing = !editing;
            if (!editing) {
                saveNotes();
            }
        }
    }

    public boolean isEditing() {
        return editing;
    }

    public void typeChar(char c) {
        if (editing && notes.length() < MAX_NOTES_LENGTH) {
            notes += c;
        }
    }

    public void backspace() {
        if (editing && notes.length() > 0) {
            notes = notes.substring(0, notes.length() - 1);
        }
    }

    public void clear() {
        notes = "";
        saveNotes();
    }

    private void loadNotes() {
        try {
            if (Files.exists(notesPath)) {
                notes = Files.readString(notesPath);
                if (notes.length() > MAX_NOTES_LENGTH) {
                    notes = notes.substring(0, MAX_NOTES_LENGTH);
                }
            }
        } catch (IOException e) {
            AeonVisionMod.LOGGER.warn("Не удалось загрузить заметки: {}", e.getMessage());
        }
    }

    private void saveNotes() {
        try {
            Files.createDirectories(notesPath.getParent());
            Files.writeString(notesPath, notes);
        } catch (IOException e) {
            AeonVisionMod.LOGGER.warn("Не удалось сохранить заметки: {}", e.getMessage());
        }
    }

    private String[] wrapText(String text, int maxChars) {
        if (text.isEmpty()) return new String[]{""};
        String[] words = text.split(" ");
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxChars) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            }
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString());
        
        return lines.toArray(new String[0]);
    }

    public boolean isVisible() {
        return visible;
    }
}

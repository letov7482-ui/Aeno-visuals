package com.aeonvision.cosmetics;

public enum FootprintType {
    NONE("Нет"),
    GLOWING("Светящиеся"),
    FIRE("Огненные"),
    ICE("Ледяные"),
    GHOST("Призрачные"),
    RUNIC("Рунические");
    
    public final String name;
    
    FootprintType(String name) {
        this.name = name;
    }
}

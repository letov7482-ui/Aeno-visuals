package com.aeonvision.cosmetics;

public enum WingType {
    NONE("Нет", "Выключено"),
    ANGEL("Ангельские", "Белые перья с сиянием"),
    DEMON("Демонические", "Красные перепонки с огнём"),
    CYBER("Кибер", "Голографические с глитчем"),
    DRAGON("Драконьи", "Тёмные кожистые с шипами"),
    PHOENIX("Феникс", "Огненные с искрами"),
    ENDER("Эндер", "Фиолетовые полупрозрачные");
    
    public final String name;
    public final String description;
    
    WingType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

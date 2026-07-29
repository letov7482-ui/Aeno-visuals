package com.aeonvision.cosmetics;

public enum AuraType {
    NONE("Нет", "Выключено"),
    BASIC("Базовая", "Однотонное свечение"),
    PULSE("Пульсар", "Ритмичная пульсация"),
    SPIRAL("Спираль", "Закрученные линии"),
    CRYSTAL("Кристалл", "Гексагональные щиты"),
    PLASMA("Плазма", "Электрические дуги"),
    DIVINE("Божество", "Лучи света и нимб");
    
    public final String name;
    public final String description;
    
    AuraType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

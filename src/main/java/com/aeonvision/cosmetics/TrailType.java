package com.aeonvision.cosmetics;

public enum TrailType {
    NONE("Нет", "Выключено"),
    FIRE("Огненный", "Языки пламени позади"),
    STARS("Звёздная пыль", "Сверкающие искры"),
    SAKURA("Сакура", "Розовые лепестки"),
    ELECTRIC("Электричество", "Мелкие молнии"),
    AQUA("Акватика", "Голубые капли"),
    ENDER("Эндер", "Частицы портала"),
    RAINBOW("Радужный", "Меняет цвет"),
    SCULK("Скверна", "Тёмный дым с черепами");
    
    public final String name;
    public final String description;
    
    TrailType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

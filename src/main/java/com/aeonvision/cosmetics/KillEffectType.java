package com.aeonvision.cosmetics;

public enum KillEffectType {
    NONE("Нет"),
    CONFETTI("Конфетти"),
    STAR_BURST("Звёздный взрыв"),
    SMOKE("Дымка"),
    CRYSTAL("Кристаллы"),
    LIGHTNING("Молния"),
    BLOOD("Кровавый фонтан");
    
    public final String name;
    
    KillEffectType(String name) {
        this.name = name;
    }
}

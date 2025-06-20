package com.EggTart.dyst.EggTart.model.enums;

public enum TaskType {
    MEAL("Meal Time", 30),
    DRINK("Drink Time", 15),
    WALK("Walk Time", 60);
    
    private final String displayName;
    private final int defaultDurationMinutes;
    
    TaskType(String displayName, int defaultDurationMinutes) {
        this.displayName = displayName;
        this.defaultDurationMinutes = defaultDurationMinutes;
    }
    
    public String getDisplayName() { return displayName; }
    public int getDefaultDurationMinutes() { return defaultDurationMinutes; }
} 
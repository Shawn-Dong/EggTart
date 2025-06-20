package com.EggTart.dyst.EggTart.model.enums;

public enum Mood {
    HAPPY("Happy"),
    NEUTRAL("Neutral"),
    TIRED("Tired"),
    EXCITED("Excited"),
    ANXIOUS("Anxious");
    
    private final String displayName;
    
    Mood(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() { return displayName; }
} 
package com.EggTart.dyst.EggTart.model.enums;

public enum TaskStatus {
    PENDING("Pending", "status-pending"),
    IN_PROGRESS("In Progress", "status-in-progress"),
    COMPLETED("Completed", "status-completed"),
    MISSED("Missed", "status-missed"),
    RESCUED("Rescued", "status-rescued"),
    SKIPPED("Skipped", "status-skipped");
    
    private final String displayName;
    private final String cssClass;
    
    TaskStatus(String displayName, String cssClass) {
        this.displayName = displayName;
        this.cssClass = cssClass;
    }
    
    public String getDisplayName() { return displayName; }
    public String getCssClass() { return cssClass; }
    
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS;
    }
    
    public boolean isCompleted() {
        return this == COMPLETED || this == RESCUED;
    }
} 
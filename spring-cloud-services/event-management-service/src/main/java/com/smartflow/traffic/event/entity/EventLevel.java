package com.smartflow.traffic.event.entity;

/**
 * 事件级别枚举
 */
public enum EventLevel {
    /**
     * 低级别 - 一般交通事件
     */
    LOW("低", 1),

    /**
     * 中级别 - 重要交通事件
     */
    MEDIUM("中", 2),

    /**
     * 高级别 - 严重交通事件
     */
    HIGH("高", 3),

    /**
     * 紧急级别 - 紧急交通事件
     */
    URGENT("紧急", 4),

    /**
     * 灾难级别 - 灾难性交通事件
     */
    CRITICAL("灾难", 5);

    private final String description;
    private final int priority;

    EventLevel(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * 根据优先级获取级别
     */
    public static EventLevel fromPriority(int priority) {
        for (EventLevel level : values()) {
            if (level.priority == priority) {
                return level;
            }
        }
        return LOW;
    }

    /**
     * 根据置信度自动确定级别
     */
    public static EventLevel fromConfidence(Double confidence) {
        if (confidence == null) {
            return LOW;
        }
        
        if (confidence >= 0.9) {
            return CRITICAL;
        } else if (confidence >= 0.8) {
            return URGENT;
        } else if (confidence >= 0.7) {
            return HIGH;
        } else if (confidence >= 0.6) {
            return MEDIUM;
        } else {
            return LOW;
        }
    }
}


package com.smartflow.traffic.event.entity;

/**
 * 事件状态枚举
 */
public enum EventStatus {
    /**
     * 待确认 - 新检测到的事件，等待确认
     */
    PENDING_CONFIRMATION("待确认"),

    /**
     * 已确认 - 事件已确认，等待处理
     */
    CONFIRMED("已确认"),

    /**
     * 处理中 - 事件正在处理
     */
    IN_PROGRESS("处理中"),

    /**
     * 已解决 - 事件已解决
     */
    RESOLVED("已解决"),

    /**
     * 已关闭 - 事件已关闭
     */
    CLOSED("已关闭"),

    /**
     * 误报 - 检测错误，非真实事件
     */
    FALSE_ALARM("误报"),

    /**
     * 已取消 - 事件已取消
     */
    CANCELLED("已取消");

    private final String description;

    EventStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查状态是否可以转换到目标状态
     */
    public boolean canTransitionTo(EventStatus targetStatus) {
        switch (this) {
            case PENDING_CONFIRMATION:
                return targetStatus == CONFIRMED || targetStatus == FALSE_ALARM || targetStatus == CANCELLED;
            case CONFIRMED:
                return targetStatus == IN_PROGRESS || targetStatus == FALSE_ALARM || targetStatus == CANCELLED;
            case IN_PROGRESS:
                return targetStatus == RESOLVED || targetStatus == CANCELLED;
            case RESOLVED:
                return targetStatus == CLOSED;
            case CLOSED:
            case FALSE_ALARM:
            case CANCELLED:
                return false; // 终态，不能转换
            default:
                return false;
        }
    }

    /**
     * 检查是否为终态
     */
    public boolean isFinalStatus() {
        return this == CLOSED || this == FALSE_ALARM || this == CANCELLED;
    }
}


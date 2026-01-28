package com.smartflow.auth.entity;

/**
 * 用户角色枚举
 */
public enum Role {
    ADMIN("管理员"),
    USER("普通用户"),
    OPERATOR("操作员");
    
    private final String description;
    
    Role(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}


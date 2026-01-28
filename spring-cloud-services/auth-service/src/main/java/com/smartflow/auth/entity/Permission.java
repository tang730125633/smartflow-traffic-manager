package com.smartflow.auth.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 权限实体
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 权限代码
     */
    @Column(name = "permission_code", unique = true, nullable = false)
    private String permissionCode;
    
    /**
     * 权限名称
     */
    @Column(name = "permission_name", nullable = false)
    private String permissionName;
    
    /**
     * 权限描述
     */
    @Column(name = "description")
    private String description;
    
    /**
     * 资源类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;
    
    /**
     * 资源路径
     */
    @Column(name = "resource_path")
    private String resourcePath;
    
    /**
     * 操作类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 资源类型枚举
     */
    public enum ResourceType {
        MODULE("模块"),
        MENU("菜单"),
        BUTTON("按钮"),
        API("API接口"),
        DATA("数据");
        
        private final String description;
        
        ResourceType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 操作类型枚举
     */
    public enum ActionType {
        CREATE("创建"),
        READ("读取"),
        UPDATE("更新"),
        DELETE("删除"),
        EXECUTE("执行"),
        MANAGE("管理");
        
        private final String description;
        
        ActionType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

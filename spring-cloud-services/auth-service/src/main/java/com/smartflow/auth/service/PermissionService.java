package com.smartflow.auth.service;

import com.smartflow.auth.entity.Permission;
import com.smartflow.auth.entity.Role;
import com.smartflow.auth.entity.RolePermission;

import java.util.List;
import java.util.Set;

/**
 * 权限服务接口
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
public interface PermissionService {
    
    /**
     * 创建权限
     */
    Permission createPermission(Permission permission);
    
    /**
     * 更新权限
     */
    Permission updatePermission(Long permissionId, Permission permission);
    
    /**
     * 删除权限
     */
    void deletePermission(Long permissionId);
    
    /**
     * 获取所有权限
     */
    List<Permission> getAllPermissions();
    
    /**
     * 根据角色获取权限
     */
    List<Permission> getPermissionsByRole(Long roleId);
    
    /**
     * 根据用户获取权限
     */
    List<Permission> getPermissionsByUser(Long userId);
    
    /**
     * 为角色分配权限
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);
    
    /**
     * 移除角色的权限
     */
    void removePermissionsFromRole(Long roleId, List<Long> permissionIds);
    
    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(Long userId, String permissionCode);
    
    /**
     * 检查用户是否有指定资源的操作权限
     */
    boolean hasResourcePermission(Long userId, String resourcePath, String actionType);
    
    /**
     * 获取用户的所有权限代码
     */
    Set<String> getUserPermissionCodes(Long userId);
    
    /**
     * 获取角色的所有权限代码
     */
    Set<String> getRolePermissionCodes(Long roleId);
    
    /**
     * 初始化默认权限
     */
    void initializeDefaultPermissions();
}

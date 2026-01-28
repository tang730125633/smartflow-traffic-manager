package com.smartflow.auth.controller;

import com.smartflow.auth.entity.Permission;
import com.smartflow.auth.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限管理控制器
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    
    private final PermissionService permissionService;
    
    /**
     * 创建权限
     */
    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        log.info("创建权限: {}", permission.getPermissionCode());
        Permission createdPermission = permissionService.createPermission(permission);
        return ResponseEntity.ok(createdPermission);
    }
    
    /**
     * 更新权限
     */
    @PutMapping("/{permissionId}")
    public ResponseEntity<Permission> updatePermission(
            @PathVariable Long permissionId,
            @RequestBody Permission permission) {
        
        log.info("更新权限: {}", permissionId);
        Permission updatedPermission = permissionService.updatePermission(permissionId, permission);
        return ResponseEntity.ok(updatedPermission);
    }
    
    /**
     * 删除权限
     */
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<String> deletePermission(@PathVariable Long permissionId) {
        log.info("删除权限: {}", permissionId);
        permissionService.deletePermission(permissionId);
        return ResponseEntity.ok("权限删除成功");
    }
    
    /**
     * 获取所有权限
     */
    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        log.info("获取所有权限");
        List<Permission> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }
    
    /**
     * 根据角色获取权限
     */
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<Permission>> getPermissionsByRole(@PathVariable Long roleId) {
        log.info("根据角色获取权限: {}", roleId);
        List<Permission> permissions = permissionService.getPermissionsByRole(roleId);
        return ResponseEntity.ok(permissions);
    }
    
    /**
     * 根据用户获取权限
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Permission>> getPermissionsByUser(@PathVariable Long userId) {
        log.info("根据用户获取权限: {}", userId);
        List<Permission> permissions = permissionService.getPermissionsByUser(userId);
        return ResponseEntity.ok(permissions);
    }
    
    /**
     * 为角色分配权限
     */
    @PostMapping("/role/{roleId}/assign")
    public ResponseEntity<String> assignPermissionsToRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        
        log.info("为角色分配权限: {} -> {}", roleId, permissionIds);
        permissionService.assignPermissionsToRole(roleId, permissionIds);
        return ResponseEntity.ok("权限分配成功");
    }
    
    /**
     * 移除角色的权限
     */
    @PostMapping("/role/{roleId}/remove")
    public ResponseEntity<String> removePermissionsFromRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        
        log.info("移除角色权限: {} -> {}", roleId, permissionIds);
        permissionService.removePermissionsFromRole(roleId, permissionIds);
        return ResponseEntity.ok("权限移除成功");
    }
    
    /**
     * 检查用户权限
     */
    @GetMapping("/user/{userId}/check")
    public ResponseEntity<Map<String, Object>> checkUserPermission(
            @PathVariable Long userId,
            @RequestParam String permissionCode) {
        
        log.info("检查用户权限: {} -> {}", userId, permissionCode);
        boolean hasPermission = permissionService.hasPermission(userId, permissionCode);
        
        Map<String, Object> result = Map.of(
            "userId", userId,
            "permissionCode", permissionCode,
            "hasPermission", hasPermission
        );
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 检查用户资源权限
     */
    @GetMapping("/user/{userId}/resource-check")
    public ResponseEntity<Map<String, Object>> checkUserResourcePermission(
            @PathVariable Long userId,
            @RequestParam String resourcePath,
            @RequestParam String actionType) {
        
        log.info("检查用户资源权限: {} -> {}:{}", userId, resourcePath, actionType);
        boolean hasPermission = permissionService.hasResourcePermission(userId, resourcePath, actionType);
        
        Map<String, Object> result = Map.of(
            "userId", userId,
            "resourcePath", resourcePath,
            "actionType", actionType,
            "hasPermission", hasPermission
        );
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取用户权限代码
     */
    @GetMapping("/user/{userId}/codes")
    public ResponseEntity<Set<String>> getUserPermissionCodes(@PathVariable Long userId) {
        log.info("获取用户权限代码: {}", userId);
        Set<String> permissionCodes = permissionService.getUserPermissionCodes(userId);
        return ResponseEntity.ok(permissionCodes);
    }
    
    /**
     * 获取角色权限代码
     */
    @GetMapping("/role/{roleId}/codes")
    public ResponseEntity<Set<String>> getRolePermissionCodes(@PathVariable Long roleId) {
        log.info("获取角色权限代码: {}", roleId);
        Set<String> permissionCodes = permissionService.getRolePermissionCodes(roleId);
        return ResponseEntity.ok(permissionCodes);
    }
    
    /**
     * 初始化默认权限
     */
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeDefaultPermissions() {
        log.info("初始化默认权限");
        permissionService.initializeDefaultPermissions();
        return ResponseEntity.ok("默认权限初始化完成");
    }
}

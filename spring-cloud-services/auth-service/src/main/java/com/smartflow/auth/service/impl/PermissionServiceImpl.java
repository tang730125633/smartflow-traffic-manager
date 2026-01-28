package com.smartflow.auth.service.impl;

import com.smartflow.auth.entity.Permission;
import com.smartflow.auth.entity.Role;
import com.smartflow.auth.entity.RolePermission;
import com.smartflow.auth.entity.User;
import com.smartflow.auth.repository.PermissionRepository;
import com.smartflow.auth.repository.RolePermissionRepository;
import com.smartflow.auth.repository.RoleRepository;
import com.smartflow.auth.repository.UserRepository;
import com.smartflow.auth.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        log.info("创建权限: {}", permission.getPermissionCode());
        
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        
        return permissionRepository.save(permission);
    }
    
    @Override
    @Transactional
    public Permission updatePermission(Long permissionId, Permission permission) {
        log.info("更新权限: {}", permissionId);
        
        Permission existingPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("权限不存在: " + permissionId));
        
        existingPermission.setPermissionName(permission.getPermissionName());
        existingPermission.setDescription(permission.getDescription());
        existingPermission.setResourceType(permission.getResourceType());
        existingPermission.setResourcePath(permission.getResourcePath());
        existingPermission.setActionType(permission.getActionType());
        existingPermission.setEnabled(permission.getEnabled());
        existingPermission.setUpdatedAt(LocalDateTime.now());
        
        return permissionRepository.save(existingPermission);
    }
    
    @Override
    @Transactional
    public void deletePermission(Long permissionId) {
        log.info("删除权限: {}", permissionId);
        
        if (!permissionRepository.existsById(permissionId)) {
            throw new RuntimeException("权限不存在: " + permissionId);
        }
        
        // 删除角色权限关联
        rolePermissionRepository.deleteByPermissionId(permissionId);
        
        // 删除权限
        permissionRepository.deleteById(permissionId);
    }
    
    @Override
    public List<Permission> getAllPermissions() {
        log.debug("获取所有权限");
        return permissionRepository.findAll();
    }
    
    @Override
    public List<Permission> getPermissionsByRole(Long roleId) {
        log.debug("根据角色获取权限: {}", roleId);
        
        return rolePermissionRepository.findByRoleIdAndEnabledTrue(roleId)
                .stream()
                .map(rolePermission -> permissionRepository.findById(rolePermission.getPermissionId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Permission> getPermissionsByUser(Long userId) {
        log.debug("根据用户获取权限: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        
        return getPermissionsByRole(user.getRole().getId());
    }
    
    @Override
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        log.info("为角色分配权限: {} -> {}", roleId, permissionIds);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + roleId));
        
        // 删除现有权限
        rolePermissionRepository.deleteByRoleId(roleId);
        
        // 分配新权限
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("权限不存在: " + permissionId));
            
            RolePermission rolePermission = RolePermission.builder()
                    .roleId(roleId)
                    .permissionId(permissionId)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            rolePermissionRepository.save(rolePermission);
        }
    }
    
    @Override
    @Transactional
    public void removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        log.info("移除角色权限: {} -> {}", roleId, permissionIds);
        
        for (Long permissionId : permissionIds) {
            rolePermissionRepository.deleteByRoleIdAndPermissionId(roleId, permissionId);
        }
    }
    
    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        log.debug("检查用户权限: {} -> {}", userId, permissionCode);
        
        Set<String> userPermissions = getUserPermissionCodes(userId);
        return userPermissions.contains(permissionCode);
    }
    
    @Override
    public boolean hasResourcePermission(Long userId, String resourcePath, String actionType) {
        log.debug("检查用户资源权限: {} -> {}:{}", userId, resourcePath, actionType);
        
        List<Permission> userPermissions = getPermissionsByUser(userId);
        
        return userPermissions.stream()
                .anyMatch(permission -> 
                    permission.getResourcePath() != null && 
                    permission.getResourcePath().equals(resourcePath) &&
                    permission.getActionType().name().equals(actionType) &&
                    permission.getEnabled()
                );
    }
    
    @Override
    public Set<String> getUserPermissionCodes(Long userId) {
        log.debug("获取用户权限代码: {}", userId);
        
        List<Permission> permissions = getPermissionsByUser(userId);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }
    
    @Override
    public Set<String> getRolePermissionCodes(Long roleId) {
        log.debug("获取角色权限代码: {}", roleId);
        
        List<Permission> permissions = getPermissionsByRole(roleId);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }
    
    @Override
    @Transactional
    public void initializeDefaultPermissions() {
        log.info("初始化默认权限");
        
        // 检查是否已初始化
        if (permissionRepository.count() > 0) {
            log.info("权限已初始化，跳过");
            return;
        }
        
        // 创建默认权限
        List<Permission> defaultPermissions = Arrays.asList(
            // 用户管理权限
            Permission.builder()
                .permissionCode("USER_CREATE")
                .permissionName("创建用户")
                .description("创建新用户")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/users")
                .actionType(Permission.ActionType.CREATE)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("USER_READ")
                .permissionName("查看用户")
                .description("查看用户信息")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/users")
                .actionType(Permission.ActionType.READ)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("USER_UPDATE")
                .permissionName("更新用户")
                .description("更新用户信息")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/users")
                .actionType(Permission.ActionType.UPDATE)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("USER_DELETE")
                .permissionName("删除用户")
                .description("删除用户")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/users")
                .actionType(Permission.ActionType.DELETE)
                .enabled(true)
                .build(),
            
            // 信号调度权限
            Permission.builder()
                .permissionCode("SIGNAL_READ")
                .permissionName("查看信号灯")
                .description("查看信号灯状态")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/signals")
                .actionType(Permission.ActionType.READ)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("SIGNAL_UPDATE")
                .permissionName("更新信号灯")
                .description("更新信号灯状态")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/signals")
                .actionType(Permission.ActionType.UPDATE)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("SIGNAL_EXECUTE")
                .permissionName("执行信号调度")
                .description("执行信号调度")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/signals/scheduling")
                .actionType(Permission.ActionType.EXECUTE)
                .enabled(true)
                .build(),
            
            // 规则引擎权限
            Permission.builder()
                .permissionCode("RULE_CREATE")
                .permissionName("创建规则")
                .description("创建调度规则")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/rules")
                .actionType(Permission.ActionType.CREATE)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("RULE_READ")
                .permissionName("查看规则")
                .description("查看调度规则")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/rules")
                .actionType(Permission.ActionType.READ)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("RULE_UPDATE")
                .permissionName("更新规则")
                .description("更新调度规则")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/rules")
                .actionType(Permission.ActionType.UPDATE)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("RULE_DELETE")
                .permissionName("删除规则")
                .description("删除调度规则")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/rules")
                .actionType(Permission.ActionType.DELETE)
                .enabled(true)
                .build(),
            
            // 交通诱导权限
            Permission.builder()
                .permissionCode("GUIDANCE_CREATE")
                .permissionName("创建诱导信息")
                .description("创建交通诱导信息")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/guidance")
                .actionType(Permission.ActionType.CREATE)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("GUIDANCE_READ")
                .permissionName("查看诱导信息")
                .description("查看交通诱导信息")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/guidance")
                .actionType(Permission.ActionType.READ)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("GUIDANCE_UPDATE")
                .permissionName("更新诱导信息")
                .description("更新交通诱导信息")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/guidance")
                .actionType(Permission.ActionType.UPDATE)
                .enabled(true)
                .build(),
            
            Permission.builder()
                .permissionCode("GUIDANCE_DELETE")
                .permissionName("删除诱导信息")
                .description("删除交通诱导信息")
                .resourceType(Permission.ResourceType.API)
                .resourcePath("/api/guidance")
                .actionType(Permission.ActionType.DELETE)
                .enabled(true)
                .build()
        );
        
        // 保存权限
        for (Permission permission : defaultPermissions) {
            permission.setCreatedAt(LocalDateTime.now());
            permission.setUpdatedAt(LocalDateTime.now());
            permissionRepository.save(permission);
        }
        
        log.info("默认权限初始化完成，共创建 {} 个权限", defaultPermissions.size());
    }
}

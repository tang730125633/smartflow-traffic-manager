package com.smartflow.auth.repository;

import com.smartflow.auth.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限关联数据访问层
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    
    /**
     * 根据角色ID查找
     */
    List<RolePermission> findByRoleId(Long roleId);
    
    /**
     * 根据权限ID查找
     */
    List<RolePermission> findByPermissionId(Long permissionId);
    
    /**
     * 根据角色ID和权限ID查找
     */
    Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
    
    /**
     * 查找启用的角色权限关联
     */
    List<RolePermission> findByRoleIdAndEnabledTrue(Long roleId);
    
    /**
     * 根据角色ID删除
     */
    void deleteByRoleId(Long roleId);
    
    /**
     * 根据权限ID删除
     */
    void deleteByPermissionId(Long permissionId);
    
    /**
     * 根据角色ID和权限ID删除
     */
    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
    
    /**
     * 检查角色是否有指定权限
     */
    boolean existsByRoleIdAndPermissionIdAndEnabledTrue(Long roleId, Long permissionId);
}

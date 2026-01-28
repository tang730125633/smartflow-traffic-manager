package com.smartflow.auth.repository;

import com.smartflow.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限数据访问层
 * 
 * @author SmartFlow Team
 * @version 1.0.0
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * 根据权限代码查找
     */
    Optional<Permission> findByPermissionCode(String permissionCode);
    
    /**
     * 查找启用的权限
     */
    List<Permission> findByEnabledTrue();
    
    /**
     * 根据资源类型查找权限
     */
    List<Permission> findByResourceTypeAndEnabledTrue(Permission.ResourceType resourceType);
    
    /**
     * 根据资源路径查找权限
     */
    List<Permission> findByResourcePathAndEnabledTrue(String resourcePath);
    
    /**
     * 根据操作类型查找权限
     */
    List<Permission> findByActionTypeAndEnabledTrue(Permission.ActionType actionType);
    
    /**
     * 根据用户ID查找权限
     */
    @Query("SELECT p FROM Permission p " +
           "JOIN RolePermission rp ON p.id = rp.permissionId " +
           "JOIN User u ON rp.roleId = u.role.id " +
           "WHERE u.id = :userId AND p.enabled = true AND rp.enabled = true")
    List<Permission> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID查找权限
     */
    @Query("SELECT p FROM Permission p " +
           "JOIN RolePermission rp ON p.id = rp.permissionId " +
           "WHERE rp.roleId = :roleId AND p.enabled = true AND rp.enabled = true")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);
}

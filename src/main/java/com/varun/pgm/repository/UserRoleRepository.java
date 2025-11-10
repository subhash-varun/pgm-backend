package com.varun.pgm.repository;

import com.varun.pgm.entity.Permission;
import com.varun.pgm.entity.UserRole;
import com.varun.pgm.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findById_UserIdAndUserType(Long userId, UserRole.UserType userType);
    List<UserRole> findById_RoleId(Long roleId);

    @Query("SELECT DISTINCT p FROM UserRole ur JOIN ur.role r JOIN r.permissions p WHERE ur.id.userId = :userId AND ur.userType = :userType")
    List<Permission> findPermissionsByUserIdAndUserType(@Param("userId") Long userId, @Param("userType") UserRole.UserType userType);
}

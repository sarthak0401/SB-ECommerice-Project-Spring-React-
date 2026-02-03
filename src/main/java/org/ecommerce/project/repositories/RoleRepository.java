package org.ecommerce.project.repositories;

import org.ecommerce.project.model.AppRoles;
import org.ecommerce.project.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRolesByRoleName(AppRoles roleName);
}

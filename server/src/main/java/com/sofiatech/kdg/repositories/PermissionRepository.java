package com.sofiatech.kdg.repositories;

import com.sofiatech.kdg.entities.Permission;
import com.sofiatech.kdg.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission,Integer> {
    Permission findByHttpMethodAndRoute(String httpMethod, String route);
}

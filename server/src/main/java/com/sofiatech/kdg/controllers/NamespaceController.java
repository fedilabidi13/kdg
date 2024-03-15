package com.sofiatech.kdg.controllers;

import com.sofiatech.kdg.dto.HttpResponse;
import com.sofiatech.kdg.entities.Permission;
import com.sofiatech.kdg.repositories.PermissionRepository;
import com.sofiatech.kdg.repositories.UserRepository;
import com.sofiatech.kdg.services.AuthenticationService;
import com.sofiatech.kdg.services.PodService;
import io.kubernetes.client.openapi.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/namespace/default")
@RequiredArgsConstructor
@CrossOrigin
public class NamespaceController {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final AuthenticationService authenticationService;
    private final PodService podService;
    @GetMapping
    public ResponseEntity<?> demoFun() throws FileNotFoundException, ApiException {
        Permission permission = permissionRepository.findByHttpMethodAndRoute("GET","/namespace/default");
        if (!permission.getUserList().contains(authenticationService.currentlyAuthenticatedUser())){
            return ResponseEntity.status(403).body(new HttpResponse("You don't have the permission to access this endpoint."));
        }
        return ResponseEntity.status(200).body(podService.listPods("default"));

    }
}

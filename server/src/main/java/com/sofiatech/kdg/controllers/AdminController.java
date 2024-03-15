package com.sofiatech.kdg.controllers;

import com.sofiatech.kdg.dto.*;
import com.sofiatech.kdg.entities.Permission;
import com.sofiatech.kdg.entities.User;
import com.sofiatech.kdg.enumerations.Role;
import com.sofiatech.kdg.repositories.PermissionRepository;
import com.sofiatech.kdg.repositories.UserRepository;
import com.sofiatech.kdg.services.AuthenticationService;
import com.sofiatech.kdg.services.NodeService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {
    private final NodeService nodeService;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final AuthenticationService authenticationService;

    @PostMapping("/register-user")
    public ResponseEntity<RegisterResponse> register(@ModelAttribute RegisterRequest registerRequest){
        return ResponseEntity.status(201).body(authenticationService.register(registerRequest));
    }
    @PostMapping("/permission/add/{id}")
    @Transactional
    public ResponseEntity<HttpResponse> addPermission(@PathVariable Integer id,@RequestParam String route
                                            ,@RequestParam String method){
        Permission permission = permissionRepository.findByHttpMethodAndRoute(method, route);
        permission.getUserList().add(userRepository.findById(id).orElse(null));
        permissionRepository.save(permission);
        String message = "Permission for: "+ method+ " method on route: "+route+" added for user with email: "
                + userRepository.findById(id).orElse(null).getEmail();
        return ResponseEntity.status(201).body(new HttpResponse(message));
    }
    @GetMapping("/list-users")
    public ResponseEntity<List<UserDto>> listUsers(){
        List<UserDto> list = new ArrayList<>();
        for (User user: userRepository.findAll()){
            var dto = UserDto.builder()
                    .id(String.valueOf(user.getId()))
                    .firstName(user.getFirstname())
                    .lastName(user.getLastname())
                    .email(user.getEmail())
                    .isActive(user.isActive())
                    .build();
            list.add(dto);
        }
        return ResponseEntity.status(200).body(list);
    }
    @PostMapping("/ban-user/{email}")
    public ResponseEntity<HttpResponse> banUser (@PathVariable("email")String email){
        User user = userRepository.findByEmail(email).get();
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.status(200).body(new HttpResponse("User with email: "+ user.getEmail()+" is now banned."));
    }
    @PostMapping("/unban-user/{email}")
    public ResponseEntity<HttpResponse> unbanUser (@PathVariable("email")String email){
        User user = userRepository.findByEmail(email).get();
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.status(200).body(new HttpResponse("User with email: "+ user.getEmail()+" is now unbanned."));
    }

}

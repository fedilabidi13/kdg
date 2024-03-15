package com.sofiatech.kdg.controllers;


import com.sofiatech.kdg.dto.*;
import com.sofiatech.kdg.entities.User;
import com.sofiatech.kdg.enumerations.Role;
import com.sofiatech.kdg.repositories.ClusterConnectionCheckerRepository;
import com.sofiatech.kdg.repositories.UserRepository;
import com.sofiatech.kdg.services.AuthenticationService;
import com.sofiatech.kdg.services.NodeService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
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
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClusterConnectionCheckerRepository repository;
    private final NodeService nodeService;
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@ModelAttribute AuthenticationRequest authenticationRequest){
        return ResponseEntity.status(200).body(authenticationService.authenticate(authenticationRequest));
    }
    @PostMapping("/connect-cluster")
    public ResponseEntity<HttpResponse> uploadKubeConfig(@RequestParam("file") MultipartFile file, @ModelAttribute AdminRegisterRequest adminRegisterRequest) {
        try {
            String fileName = "k8s-config.yaml";
            Path filePath = Paths.get("src", "main", "resources", fileName);
            Files.copy(file.getInputStream(), filePath);}
        catch (IOException e){
            return ResponseEntity.status(400).body(new HttpResponse("Application already associated with kubernetes cluster."));
        }
        try {
            ApiClient client = Config.fromConfig("src/main/resources/k8s-config.yaml");

            // Set the global default api client to the one above
            Configuration.setDefaultApiClient(client);

            // Create an instance of the CoreV1Api class
            CoreV1Api coreV1Api = new CoreV1Api(client);
            List<V1Pod> v1PodList = coreV1Api.listNamespacedPod("default").execute().getItems();
            for (V1Pod v1Pod : v1PodList) {
                PodDto podDTO = new PodDto();
                podDTO.setName(Objects.requireNonNull(v1Pod.getMetadata()).getName());
                podDTO.setNamespace(v1Pod.getMetadata().getNamespace());
                podDTO.setStatus(Objects.requireNonNull(v1Pod.getStatus()).getPhase());
                log.info(String.valueOf(podDTO));
            }
        } catch (IOException | ApiException ex) {
            return ResponseEntity.status(400).body(new HttpResponse("Error connecting to cluster."));
        }
        var user = User.builder()
                .email(adminRegisterRequest.getEmail())
                .firstname(adminRegisterRequest.getFirstName())
                .lastname(adminRegisterRequest.getLastName())
                .password(passwordEncoder.encode(adminRegisterRequest.getPassword()))
                .role(Role.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(user);
        HttpResponse response = new HttpResponse("KubeConfig file uploaded successfully and configured.");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/is-cluster-connected")
    public ResponseEntity<HttpResponse> isClusterConnected(){
        var data= repository.findById(1).orElse(null);
        return ResponseEntity.status(200).body(new HttpResponse(String.valueOf(data.isConnected())));
    }
    @PostMapping("/change-cluster-state")
    public ResponseEntity<HttpResponse> changeClusterState(){
        var data= repository.findById(1).orElse(null);
        data.setConnected(true);
        repository.save(data);
        return ResponseEntity.status(200).body(new HttpResponse(String.valueOf(data.isConnected())));
    }
    @GetMapping("/cluster-info")
    public ResponseEntity<?> testtmp() throws ApiException {
        return ResponseEntity.ok(nodeService.getCLusterBasicData());
    }
}

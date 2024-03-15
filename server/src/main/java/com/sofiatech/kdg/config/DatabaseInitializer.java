package com.sofiatech.kdg.config;


import com.sofiatech.kdg.entities.ClusterConnectionChecker;
import com.sofiatech.kdg.entities.Permission;
import com.sofiatech.kdg.repositories.ClusterConnectionCheckerRepository;
import com.sofiatech.kdg.repositories.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository repository;
    private final ClusterConnectionCheckerRepository clusterConnectionCheckerRepository;
    private static final String[] ROUTES = {"/namespaces",
            "/pods",
            "/deployments",
            "/services",
            "/pv",
            "/pvc"};
    @Override
    public void run(String... args) throws Exception {
        var k8sChecker = ClusterConnectionChecker
                .builder()
                .isConnected(false)
                .build();
        clusterConnectionCheckerRepository.save(k8sChecker);
        for (String route: ROUTES){
            var permissionGET = Permission.builder()
                    .httpMethod("GET")
                    .route(route)
                    .build();
            repository.save(permissionGET);
            var permissionPOST = Permission.builder()
                    .httpMethod("POST")
                    .route(route)
                    .build();
            repository.save(permissionPOST);
            var permissionUPDATE = Permission.builder()
                    .httpMethod("UPDATE")
                    .route(route)
                    .build();
            repository.save(permissionUPDATE);
            var permissionDELETE = Permission.builder()
                    .httpMethod("DELETE")
                    .route(route)
                    .build();
            repository.save(permissionDELETE);


        }
    }
}

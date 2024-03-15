package com.sofiatech.kdg.services;


import com.sofiatech.kdg.dto.PodDto;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PodService {

    private CoreV1Api coreV1Api;
    private ApiClient apiClient;

    public void createPod(String namespace, V1Pod pod) throws ApiException {
        coreV1Api.createNamespacedPod(namespace, pod);
    }

    public List<PodDto> listPods(String namespace) throws ApiException, FileNotFoundException {

        try (InputStream inputStream = Files.newInputStream(Paths.get("src/main/resources/k8s-config.yaml"))) {
            apiClient = Config.fromConfig(inputStream);
            Configuration.setDefaultApiClient(apiClient);
        } catch (IOException e) {
            log.error("Failed to initialize Kubernetes client configuration", e);
        }
        coreV1Api = new CoreV1Api(apiClient);
        List<PodDto> podDTOList = new ArrayList<>();
        List<V1Pod> v1PodList = coreV1Api.listNamespacedPod(namespace).execute().getItems();
        for (V1Pod v1Pod : v1PodList) {
            PodDto podDTO = new PodDto();
            podDTO.setName(Objects.requireNonNull(v1Pod.getMetadata()).getName());
            podDTO.setNamespace(v1Pod.getMetadata().getNamespace());
            podDTO.setStatus(Objects.requireNonNull(v1Pod.getStatus()).getPhase());
            podDTOList.add(podDTO);
        }
        return podDTOList;
    }
    public void updatePod(String name, String namespace, V1Pod pod) throws ApiException {
        coreV1Api.replaceNamespacedPod(name, namespace, pod);
    }

    public void deletePod(String name, String namespace) throws ApiException {
        coreV1Api.deleteNamespacedPod(name, namespace);
    }
    public String execTerminal(String namespace, String podName, String command) throws ApiException, IOException, InterruptedException {
        // Create an instance of Exec using the API client
        Exec exec = new Exec(coreV1Api.getApiClient());

        // Define the command to execute
        String[] commandArray = {"/bin/sh", "-c", command};

        // Execute the command within the pod
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        Process process = exec.exec(namespace, podName, commandArray, true); // Modified line

        // Copy process output to stdout and stderr streams
        Streams.copy(process.getInputStream(), stdout);
        //Streams.copy(process.getErrorStream(), stderr);

        // Wait for the command execution to complete
        int statusCode = process.waitFor();

        // Close the streams
        stdout.close();
        stderr.close();

        // Get the command output from stdout and stderr
        String output = stdout.toString() + stderr.toString();

        // Check if there's any output
        return output.isEmpty() ? "\n" : output;
    }
}

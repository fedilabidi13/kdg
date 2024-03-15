package com.sofiatech.kdg.services;

import com.sofiatech.kdg.dto.NodeInfo;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1NodeSystemInfo;
import io.kubernetes.client.util.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NodeService {
    private CoreV1Api coreV1Api;
    private ApiClient apiClient;

    public  List<NodeInfo> getCLusterBasicData() throws ApiException {
        try (InputStream inputStream = Files.newInputStream(Paths.get("src/main/resources/k8s-config.yaml"))) {
            apiClient = Config.fromConfig(inputStream);
            Configuration.setDefaultApiClient(apiClient);
        } catch (IOException e) {
            log.error("Failed to initialize Kubernetes client configuration", e);
        }
        coreV1Api = new CoreV1Api(apiClient);
        V1NodeList nodeList = coreV1Api.listNode().execute();
        List<NodeInfo> nodeInfoList = new ArrayList<>();

        for (V1Node node : nodeList.getItems()) {
            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo.setNodeName(Objects.requireNonNull(node.getMetadata()).getName());
            String linuxDistribution = getLinuxDistribution(node);

            nodeInfo.setOperatingSystem(linuxDistribution);
            nodeInfo.setKubernetesVersion(getNodeKubernetesVersion(node));
            nodeInfo.setCri(getNodeCRI(node));
            nodeInfo.setPodNetworkPlugin(getPodNetworkPlugin(node));
            nodeInfo.setHostname(getNodeHostname(node));
            nodeInfo.setClusterAge(getClusterAge(nodeList));
            nodeInfoList.add(nodeInfo);
        }

        // You can process the nodeInfoList further as per your requirement
        // For now, just printing the node info

    return nodeInfoList;
}
    private String getNodeOS(V1Node node) {
        Map<String, String> labels = Objects.requireNonNull(node.getMetadata()).getLabels();
        return labels != null ? labels.get("kubernetes.io/os") : "Unknown";
    }
    private static String getLinuxDistribution(V1Node node) {
        if (node != null && node.getStatus() != null && node.getStatus().getNodeInfo() != null) {
            V1NodeSystemInfo nodeInfo = node.getStatus().getNodeInfo();
            String osImage = nodeInfo.getOsImage();
            // Extract Linux distribution from the osImage field
            return extractLinuxDistribution(osImage);
        }
        return "Unknown";
    }
    private static String extractLinuxDistribution(String osImage) {
        if (osImage != null) {
            // Splitting the osImage to extract the Linux distribution
            return osImage.trim(); // Return the full osImage
        }
        return "Unknown";
    }
    private String getNodeKubernetesVersion(V1Node node) {
        return Objects.requireNonNull(Objects.requireNonNull(node.getStatus()).getNodeInfo()).getKubeletVersion();
    }

    private String getNodeCRI(V1Node node) {
        return Objects.requireNonNull(Objects.requireNonNull(node.getStatus()).getNodeInfo()).getContainerRuntimeVersion();
    }

    private static String getPodNetworkPlugin(V1Node node) {
        // Check node annotations for pod network plugin information
        Map<String, String> annotations = node.getMetadata().getAnnotations();
        if (annotations != null) {
            // Check for annotations that indicate the pod network plugin
            for (Map.Entry<String, String> entry : annotations.entrySet()) {
                String annotationKey = entry.getKey();
                if (annotationKey.startsWith("projectcalico.org/")) {
                    return "Calico";
                } else if (annotationKey.startsWith("io.cilium/")) {
                    return "Cilium";
                } else if (annotationKey.startsWith("networking.weave.works/")) {
                    return "WeaveNet";
                } else if (annotationKey.startsWith("flannel.alpha.coreos.com/")) {
                    return "Flannel";
                }
                // Add more conditions to detect other pod network plugins as needed
            }
        }
        return "Default";
    }
    private String getNodeHostname(V1Node node) {
        return Objects.requireNonNull(node.getMetadata()).getName();
    }

    private String getClusterAge(V1NodeList nodeList) {
        Instant earliestCreationTime = Instant.now();
        for (V1Node node : nodeList.getItems()) {
            String creationTimestamp = String.valueOf(Objects.requireNonNull(node.getMetadata()).getCreationTimestamp());
            if (creationTimestamp != null) {
                Instant nodeCreationTime = Instant.parse(creationTimestamp);
                if (nodeCreationTime.isBefore(earliestCreationTime)) {
                    earliestCreationTime = nodeCreationTime;
                }
            }
        }
        Duration age = Duration.between(earliestCreationTime, Instant.now());
        long days = age.toDays();
        return days + " days";
    }
}

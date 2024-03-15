package com.sofiatech.kdg.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NodeInfo {
    private String nodeName;
    private String operatingSystem;
    private String kubernetesVersion;
    private String cri;
    private String podNetworkPlugin;
    private String hostname;
    private String clusterAge;

}
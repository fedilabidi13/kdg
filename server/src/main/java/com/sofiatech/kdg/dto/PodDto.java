package com.sofiatech.kdg.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PodDto {
    private String name;
    private String namespace;
    private String status;}

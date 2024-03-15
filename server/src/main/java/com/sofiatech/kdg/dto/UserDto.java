package com.sofiatech.kdg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isActive;
}

package com.projects.intrustion_detection.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String messageGenerated;
    private String email;
    private String role;
    private String jwt;


}

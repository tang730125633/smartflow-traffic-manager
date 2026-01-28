package com.smartflow.incident.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveIncidentRequest {

    @NotBlank
    private String description;
}

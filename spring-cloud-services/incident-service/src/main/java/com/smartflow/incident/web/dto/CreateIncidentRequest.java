package com.smartflow.incident.web.dto;

import com.smartflow.incident.domain.IncidentLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateIncidentRequest {

    @NotBlank
    private String type;

    @NotNull
    private IncidentLevel level;

    @NotBlank
    private String location;

    @NotNull
    private Instant occurredAt;

    private String source;
}

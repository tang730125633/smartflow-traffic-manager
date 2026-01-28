package com.smartflow.incident.web.dto;

import com.smartflow.incident.domain.IncidentLevel;
import com.smartflow.incident.domain.IncidentStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class IncidentResponse {
    Long id;
    String type;
    IncidentLevel level;
    String location;
    IncidentStatus status;
    Instant occurredAt;
    Instant resolvedAt;
    Instant createdAt;
    Instant updatedAt;
    String source;
}

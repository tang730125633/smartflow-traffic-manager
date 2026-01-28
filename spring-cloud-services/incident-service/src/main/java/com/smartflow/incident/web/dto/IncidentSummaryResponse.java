package com.smartflow.incident.web.dto;

import com.smartflow.incident.domain.IncidentLevel;
import com.smartflow.incident.domain.IncidentStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class IncidentSummaryResponse {
    Long id;
    String type;
    IncidentLevel level;
    IncidentStatus status;
    String location;
    Instant occurredAt;
    Instant updatedAt;
}

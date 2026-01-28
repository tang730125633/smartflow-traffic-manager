package com.smartflow.incident.messaging;

import com.smartflow.incident.domain.IncidentLevel;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class IncidentCreatedEvent {
    Long id;
    String type;
    IncidentLevel level;
    String location;
    Instant occurredAt;
    String source;
    Instant createdAt;
}

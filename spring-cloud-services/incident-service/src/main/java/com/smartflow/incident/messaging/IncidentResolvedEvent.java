package com.smartflow.incident.messaging;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class IncidentResolvedEvent {
    Long id;
    Instant resolvedAt;
    String description;
}

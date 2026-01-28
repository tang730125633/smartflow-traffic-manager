package com.smartflow.incident.messaging;

import com.smartflow.incident.web.dto.CreateIncidentRequest;

public final class IncidentEventKeys {
    private IncidentEventKeys() {
    }

    public static String createdKey(String source, long occurredAtMillis) {
        return "INCIDENT_CREATED:" + source + ':' + occurredAtMillis;
    }

    public static String createdKey(CreateIncidentRequest request) {
        return createdKey(request.getSource(), request.getOccurredAt().toEpochMilli());
    }

    public static String resolvedKey(Long incidentId) {
        return "INCIDENT_RESOLVED:" + incidentId;
    }
}

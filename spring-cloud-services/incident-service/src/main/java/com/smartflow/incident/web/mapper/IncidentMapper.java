package com.smartflow.incident.web.mapper;

import com.smartflow.incident.domain.Incident;
import com.smartflow.incident.domain.IncidentStatus;
import com.smartflow.incident.web.dto.CreateIncidentRequest;
import com.smartflow.incident.web.dto.IncidentResponse;
import com.smartflow.incident.web.dto.IncidentSummaryResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    Incident toEntity(CreateIncidentRequest request);

    IncidentResponse toResponse(Incident incident);

    IncidentSummaryResponse toSummary(Incident incident);

    @AfterMapping
    default void fillAudit(CreateIncidentRequest request, @MappingTarget Incident incident) {
        Instant now = Instant.now();
        incident.setCreatedAt(now);
        incident.setUpdatedAt(now);
        incident.setStatus(IncidentStatus.OPEN);
    }
}

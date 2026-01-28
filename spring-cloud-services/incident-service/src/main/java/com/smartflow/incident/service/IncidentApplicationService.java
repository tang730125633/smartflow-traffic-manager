package com.smartflow.incident.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartflow.incident.domain.Incident;
import com.smartflow.incident.domain.IncidentTimeline;
import com.smartflow.incident.domain.IncidentTimelineEvent;
import com.smartflow.incident.messaging.IncidentCreatedEvent;
import com.smartflow.incident.messaging.IncidentEventKeys;
import com.smartflow.incident.messaging.IncidentResolvedEvent;
import com.smartflow.incident.messaging.IncidentTopics;
import com.smartflow.incident.repository.IncidentRepository;
import com.smartflow.incident.repository.IncidentTimelineRepository;
import com.smartflow.incident.web.dto.CreateIncidentRequest;
import com.smartflow.incident.web.dto.IncidentResponse;
import com.smartflow.incident.web.dto.IncidentSummaryResponse;
import com.smartflow.incident.web.dto.ResolveIncidentRequest;
import com.smartflow.incident.web.mapper.IncidentMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IncidentApplicationService {

    private final IncidentRepository incidentRepository;
    private final IncidentTimelineRepository timelineRepository;
    private final IncidentMapper incidentMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    private Counter incidentsCreatedCounter;
    private DistributionSummary lifecycleHistogram;

    @Transactional
    public IncidentResponse createIncident(CreateIncidentRequest request, String traceId) {
        ensureMetersInitialized();

        String eventKey = IncidentEventKeys.createdKey(request);
        Optional<IncidentTimeline> existing = timelineRepository.findByEventKey(eventKey);
        if (existing.isPresent()) {
            IncidentResponse cached = incidentMapper.toResponse(existing.get().getIncident());
            log.info("Duplicate incident creation detected for key={} traceId={}", eventKey, traceId);
            return cached;
        }

        Incident incident = incidentMapper.toEntity(request);
        incident.setOccurredAt(request.getOccurredAt());
        incident.setSource(request.getSource());
        incident.setCreatedAt(Instant.now());
        incident.setUpdatedAt(incident.getCreatedAt());
        Incident saved = incidentRepository.saveAndFlush(incident);

        persistTimeline(saved, IncidentTimelineEvent.CREATED, eventKey, request, traceId);
        publishCreatedEvent(saved, traceId);
        incidentsCreatedCounter.increment();

        log.info("Incident created id={} traceId={}", saved.getId(), traceId);
        return incidentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public IncidentResponse getIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        return incidentMapper.toResponse(incident);
    }

    @Transactional(readOnly = true)
    public List<IncidentSummaryResponse> listIncidents(String status) {
        return incidentRepository.findAll().stream()
                .filter(incident -> !StringUtils.hasText(status) || incident.getStatus().name().equalsIgnoreCase(status))
                .map(incidentMapper::toSummary)
                .collect(Collectors.toList());
    }

    public IncidentResponse resolveIncident(Long id, ResolveIncidentRequest request, String traceId) {
        ensureMetersInitialized();

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        if (incident.isResolved()) {
            log.info("Incident already resolved id={} traceId={}", id, traceId);
            return incidentMapper.toResponse(incident);
        }

        String eventKey = IncidentEventKeys.resolvedKey(id);
        Optional<IncidentTimeline> existing = timelineRepository.findByEventKey(eventKey);
        if (existing.isPresent()) {
            log.info("Duplicate resolve request ignored for incident={} traceId={}", id, traceId);
            return incidentMapper.toResponse(existing.get().getIncident());
        }

        Instant resolvedAt = Instant.now();
        incident.resolve(resolvedAt);
        Incident saved = incidentRepository.save(incident);

        persistTimeline(saved, IncidentTimelineEvent.RESOLVED, eventKey, request, traceId);
        publishResolvedEvent(saved, request, traceId);
        lifecycleHistogram.record(Duration.between(saved.getOccurredAt(), resolvedAt).toMillis() / 1000.0);

        log.info("Incident resolved id={} traceId={}", id, traceId);
        return incidentMapper.toResponse(saved);
    }

    private void publishCreatedEvent(Incident incident, String traceId) {
        IncidentCreatedEvent event = IncidentCreatedEvent.builder()
                .id(incident.getId())
                .type(incident.getType())
                .level(incident.getLevel())
                .location(incident.getLocation())
                .occurredAt(incident.getOccurredAt())
                .source(incident.getSource())
                .createdAt(incident.getCreatedAt())
                .build();
        kafkaTemplate.send(IncidentTopics.INCIDENT_CREATED, incident.getId().toString(), event);
        log.debug("Published IncidentCreatedEvent id={} traceId={}", incident.getId(), traceId);
    }

    private void publishResolvedEvent(Incident incident, ResolveIncidentRequest request, String traceId) {
        IncidentResolvedEvent event = IncidentResolvedEvent.builder()
                .id(incident.getId())
                .resolvedAt(incident.getResolvedAt())
                .description(request.getDescription())
                .build();
        kafkaTemplate.send(IncidentTopics.INCIDENT_RESOLVED, incident.getId().toString(), event);
        log.debug("Published IncidentResolvedEvent id={} traceId={}", incident.getId(), traceId);
    }

    private void persistTimeline(Incident incident,
                                 IncidentTimelineEvent event,
                                 String eventKey,
                                 Object payload,
                                 String traceId) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            IncidentTimeline timeline = IncidentTimeline.builder()
                    .incident(incident)
                    .event(event)
                    .eventKey(eventKey)
                    .eventTimestamp(Instant.now())
                    .payloadJson(payloadJson)
                    .traceId(traceId)
                    .build();
            timelineRepository.save(timeline);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to persist incident timeline", e);
        }
    }

    private void ensureMetersInitialized() {
        if (incidentsCreatedCounter == null) {
            incidentsCreatedCounter = Counter.builder("incidents_created_total")
                    .description("Total number of created incidents")
                    .register(meterRegistry);
        }
        if (lifecycleHistogram == null) {
            lifecycleHistogram = DistributionSummary.builder("incident_lifecycle_seconds")
                    .description("Incident lifecycle duration from occurrence to resolution")
                    .baseUnit("seconds")
                    .publishPercentileHistogram(true)
                    .register(meterRegistry);
        }
    }
}

package com.smartflow.incident.messaging;

import com.smartflow.incident.repository.IncidentTimelineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IncidentEventListener {

    private final IncidentTimelineRepository timelineRepository;

    @KafkaListener(topics = IncidentTopics.INCIDENT_CREATED, containerFactory = "incidentCreatedKafkaListenerContainerFactory")
    public void onIncidentCreated(IncidentCreatedEvent event) {
        timelineRepository.findByEventKey(IncidentEventKeys.createdKey(event.getSource(), event.getOccurredAt().toEpochMilli()))
                .ifPresentOrElse(
                        timeline -> log.debug("Audit success for incident created event id={}", event.getId()),
                        () -> log.warn("Missing timeline entry for incident created event id={}", event.getId())
                );
    }

    @KafkaListener(topics = IncidentTopics.INCIDENT_RESOLVED, containerFactory = "incidentResolvedKafkaListenerContainerFactory")
    public void onIncidentResolved(IncidentResolvedEvent event) {
        timelineRepository.findByEventKey(IncidentEventKeys.resolvedKey(event.getId()))
                .ifPresentOrElse(
                        timeline -> log.debug("Audit success for incident resolved event id={}", event.getId()),
                        () -> log.warn("Missing timeline entry for incident resolved event id={}", event.getId())
                );
    }
}

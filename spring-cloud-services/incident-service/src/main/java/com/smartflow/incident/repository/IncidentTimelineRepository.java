package com.smartflow.incident.repository;

import com.smartflow.incident.domain.IncidentTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentTimelineRepository extends JpaRepository<IncidentTimeline, Long> {
    Optional<IncidentTimeline> findByEventKey(String eventKey);
}

package com.smartflow.incident.repository;

import com.smartflow.incident.domain.Incident;
import com.smartflow.incident.domain.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {
    List<Incident> findByStatus(IncidentStatus status);
}

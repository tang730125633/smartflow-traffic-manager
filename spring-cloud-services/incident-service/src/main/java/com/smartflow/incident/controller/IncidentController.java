package com.smartflow.incident.controller;

import com.smartflow.incident.service.IncidentApplicationService;
import com.smartflow.incident.service.IncidentNotFoundException;
import com.smartflow.incident.web.dto.CreateIncidentRequest;
import com.smartflow.incident.web.dto.IncidentResponse;
import com.smartflow.incident.web.dto.IncidentSummaryResponse;
import com.smartflow.incident.web.dto.ResolveIncidentRequest;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentApplicationService incidentApplicationService;
    private final Tracer tracer;

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(@Valid @RequestBody CreateIncidentRequest request) {
        String traceId = currentTraceId();
        IncidentResponse response = incidentApplicationService.createIncident(request, traceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<IncidentResponse> resolveIncident(@PathVariable Long id,
                                                            @Valid @RequestBody ResolveIncidentRequest request) {
        String traceId = currentTraceId();
        IncidentResponse response = incidentApplicationService.resolveIncident(id, request, traceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncident(@PathVariable Long id) {
        IncidentResponse response = incidentApplicationService.getIncident(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<IncidentSummaryResponse>> listIncidents(@RequestParam(name = "status", required = false) String status) {
        return ResponseEntity.ok(incidentApplicationService.listIncidents(status));
    }

    @ExceptionHandler(IncidentNotFoundException.class)
    public ResponseEntity<String> handleNotFound(IncidentNotFoundException ex) {
        log.warn("{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    private String currentTraceId() {
        return Optional.ofNullable(tracer.currentSpan())
                .map(Span::context)
                .map(TraceContext::traceId)
                .orElse("N/A");
    }
}

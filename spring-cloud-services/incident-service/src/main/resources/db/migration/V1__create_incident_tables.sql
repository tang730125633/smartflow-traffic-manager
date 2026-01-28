CREATE TABLE IF NOT EXISTS incident (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(64) NOT NULL,
    level VARCHAR(16) NOT NULL,
    location VARCHAR(128) NOT NULL,
    status VARCHAR(16) NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    source VARCHAR(64),
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS incident_timeline (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    incident_id BIGINT NOT NULL,
    event VARCHAR(16) NOT NULL,
    event_key VARCHAR(64) NOT NULL,
    event_timestamp TIMESTAMP NOT NULL,
    payload_json TEXT,
    trace_id VARCHAR(64),
    CONSTRAINT fk_incident FOREIGN KEY (incident_id) REFERENCES incident(id),
    CONSTRAINT uk_timeline_event_key UNIQUE (event_key)
);

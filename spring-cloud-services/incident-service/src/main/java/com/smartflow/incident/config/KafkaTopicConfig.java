package com.smartflow.incident.config;

import com.smartflow.incident.messaging.IncidentTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic incidentCreatedTopic() {
        return TopicBuilder.name(IncidentTopics.INCIDENT_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic incidentResolvedTopic() {
        return TopicBuilder.name(IncidentTopics.INCIDENT_RESOLVED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

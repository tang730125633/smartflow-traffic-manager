package com.smartflow.incident;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartflow.incident.domain.IncidentTimelineEvent;
import com.smartflow.incident.repository.IncidentRepository;
import com.smartflow.incident.repository.IncidentTimelineRepository;
import com.smartflow.incident.web.dto.CreateIncidentRequest;
import com.smartflow.incident.web.dto.ResolveIncidentRequest;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IncidentControllerTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Container
    static KafkaContainer kafka = new KafkaContainer("confluentinc/cp-kafka:7.4.0");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IncidentRepository incidentRepository;

    @Autowired
    IncidentTimelineRepository timelineRepository;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.consumerProps(UUID.randomUUID().toString(), "true", kafka.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(java.util.List.of("traffic.incident.created", "traffic.incident.resolved"));
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
        timelineRepository.deleteAll();
        incidentRepository.deleteAll();
    }

    @Test
    void shouldCreateAndResolveIncident() throws Exception {
        CreateIncidentRequest create = new CreateIncidentRequest();
        create.setType("ACCIDENT");
        create.setLevel(com.smartflow.incident.domain.IncidentLevel.HIGH);
        create.setLocation("NH48");
        create.setOccurredAt(Instant.now());
        create.setSource("flask-test");

        String createPayload = objectMapper.writeValueAsString(create);
        String response = mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        ConsumerRecords<String, String> createdRecords = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5));
        assertThat(createdRecords.count()).isGreaterThan(0);

        ResolveIncidentRequest resolve = new ResolveIncidentRequest();
        resolve.setDescription("Cleared manually");

        mockMvc.perform(post("/incidents/{id}/resolve", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resolve)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));

        mockMvc.perform(get("/incidents/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        assertThat(timelineRepository.findAll())
                .hasSize(2)
                .extracting("event")
                .containsExactlyInAnyOrder(IncidentTimelineEvent.CREATED, IncidentTimelineEvent.RESOLVED);
    }
}

package com.smartflow.ai.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 交通数据Kafka消费者
 */
@Component
@Slf4j
public class TrafficDataConsumer {
    
    /**
     * 消费交通数据消息
     */
    @KafkaListener(topics = "traffic-data-topic", groupId = "ai-detection-group")
    public void consumeTrafficData(@Payload String message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                 @Header(KafkaHeaders.OFFSET) long offset,
                                 Acknowledgment acknowledgment) {
        try {
            log.info("接收到交通数据消息 - Topic: {}, Partition: {}, Offset: {}, Message: {}", 
                    topic, partition, offset, message);
            
            // 这里可以添加具体的业务逻辑
            // 例如：数据验证、存储、分析等
            
            // 手动确认消息
            acknowledgment.acknowledge();
            
            log.info("交通数据消息处理完成");
            
        } catch (Exception e) {
            log.error("处理交通数据消息失败", e);
            // 根据业务需求决定是否重试或丢弃消息
        }
    }
    
    /**
     * 消费事故检测消息
     */
    @KafkaListener(topics = "accident-detection-topic", groupId = "ai-detection-group")
    public void consumeAccidentDetection(@Payload String message,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                       @Header(KafkaHeaders.OFFSET) long offset,
                                       Acknowledgment acknowledgment) {
        try {
            log.info("接收到事故检测消息 - Topic: {}, Partition: {}, Offset: {}, Message: {}", 
                    topic, partition, offset, message);
            
            // 这里可以添加具体的业务逻辑
            // 例如：事故分析、通知发送、数据存储等
            
            // 手动确认消息
            acknowledgment.acknowledge();
            
            log.info("事故检测消息处理完成");
            
        } catch (Exception e) {
            log.error("处理事故检测消息失败", e);
            // 根据业务需求决定是否重试或丢弃消息
        }
    }
}


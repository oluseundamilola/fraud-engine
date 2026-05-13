package com.app.fraudengine.extended.events;

import com.app.fraudengine.extended.DTOs.InspectionResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherService {

    private static final String TOPIC_NAME = "inspected-transactions";

    private final KafkaTemplate<String, InspectionResultDTO> kafkaTemplate;

    public void publishInspectionResult(
        InspectionResultDTO result
    ) {

        try {

            kafkaTemplate.send(
                TOPIC_NAME,
                result.getTransactionReference(),
                result
            );

            log.info(
                "[FRAUD-PUBLISHER] Inspection result published | Ref: {} | Blocked: {}",
                result.getTransactionReference(),
                result.isBlocked()
            );

        } catch (Exception e) {

            log.error(
                "[FRAUD-PUBLISHER] Failed to publish inspection result | Ref: {}",
                result.getTransactionReference(),
                e
            );
        }
    }
}

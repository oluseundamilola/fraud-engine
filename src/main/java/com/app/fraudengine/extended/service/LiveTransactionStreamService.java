package com.app.fraudengine.extended.service;

import com.app.fraudengine.service.dto.TransactionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class LiveTransactionStreamService {

    private final List<SseEmitter> emitters =
        new CopyOnWriteArrayList<>();

    /**
     * Client subscribes here
     */
    public SseEmitter subscribe() {

        SseEmitter emitter =
            new SseEmitter(Long.MAX_VALUE);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));

        emitter.onTimeout(() -> emitters.remove(emitter));

        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

    /**
     * Push transaction to dashboard
     */
    public void publishTransaction(
        TransactionDTO transaction
    ) {

        List<SseEmitter> deadEmitters =
            new CopyOnWriteArrayList<>();

        emitters.forEach(emitter -> {

            try {

                emitter.send(
                    SseEmitter.event()
                        .name("transaction")
                        .data(transaction)
                );

            } catch (IOException e) {

                deadEmitters.add(emitter);

                log.error(
                    "Failed to send SSE event",
                    e
                );
            }
        });

        emitters.removeAll(deadEmitters);
    }
}

package br.com.himax.saga.orchestration.orchestrator.core.saga;

import br.com.himax.saga.orchestration.orchestrator.api.model.Event;
import br.com.himax.saga.orchestration.orchestrator.core.enums.EEventSource;
import br.com.himax.saga.orchestration.orchestrator.core.enums.ETopics;
import br.com.himax.saga.orchestration.orchestrator.domain.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static br.com.himax.saga.orchestration.orchestrator.core.saga.SagaHandler.EVENT_SOURCE_INDEX;
import static br.com.himax.saga.orchestration.orchestrator.core.saga.SagaHandler.SAGA_HANDLER;
import static br.com.himax.saga.orchestration.orchestrator.core.saga.SagaHandler.SAGA_STATUS_INDEX;
import static br.com.himax.saga.orchestration.orchestrator.core.saga.SagaHandler.TOPIC_INDEX;
import static java.lang.String.format;
import static org.springframework.util.ObjectUtils.isEmpty;


@Slf4j
@AllArgsConstructor
@Component
public class SagaExecutionController {

    public static final String SAGA_LOG_ID = "ORDER ID: %s | TRANSACTION ID: %s | EVENT ID: %s";

    public ETopics getNextTopic(Event event){
        if(isEmpty(event.getSource()) || isEmpty(event.getStatus())){
            throw new ValidationException("Source and Status must be informed");
        }

        ETopics topic = findTopicBySourceAndStatus(event);
        logCurrentSaga(event, topic);

        return topic;
    }

    private ETopics findTopicBySourceAndStatus(Event event){
        return (ETopics) Arrays.stream(SAGA_HANDLER)
                .filter(row -> isEventSourceAndStatusValid(event, row))
                .map(row -> row[TOPIC_INDEX])
                .findFirst()
                .orElseThrow(()-> new ValidationException("Topic Not found"));
    }

    private boolean isEventSourceAndStatusValid(Event event, Object[] row){
        var source = row[EVENT_SOURCE_INDEX];
        var status = row[SAGA_STATUS_INDEX];

        return event.getSource().equals(source) && event.getStatus().equals(status);
    }

    private void logCurrentSaga(Event event, ETopics topic){
        String sagaId = createSagaId(event);
        EEventSource source = event.getSource();

        switch (event.getStatus()){
            case SUCCESS -> log.info("### CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
            case ROLLBACK_PENDING -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
            case FAIL -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
        }
    }

    private String createSagaId(Event event){
        return format(SAGA_LOG_ID,event.getPayload().getId(), event.getPayload().getTransactionId(), event.getId());
    }
}

package br.com.himax.saga.orchestration.payment.api.consumer;

import br.com.himax.saga.orchestration.payment.core.mapper.JsonHelper;
import br.com.himax.saga.orchestration.payment.api.model.Event;
import br.com.himax.saga.orchestration.payment.domain.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentConsumer {
    public static final String SUCCESS = "payment-success";
    public static final String FAIL = "payment-fail";
    private final JsonHelper jsonHelper;
    private PaymentService service;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = SUCCESS)
    public void consumeSuccessEvent(String payload){
        log.info("Receiving success event {} from {} topic",payload, SUCCESS);
        Event event =  jsonHelper.toEvent(payload);
        service.realizePayment(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = FAIL)
    public void consumeFailEvent(String payload){
        log.info("Receiving rollback event {} from {} topic",payload, FAIL);
        Event event =  jsonHelper.toEvent(payload);
        service.realizeRefund(event);
    }
}

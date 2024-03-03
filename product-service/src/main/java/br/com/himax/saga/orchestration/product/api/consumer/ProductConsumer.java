package br.com.himax.saga.orchestration.product.api.consumer;

import br.com.himax.saga.orchestration.product.core.mapper.JsonHelper;
import br.com.himax.saga.orchestration.product.api.model.Event;
import br.com.himax.saga.orchestration.product.domain.exception.ValidationException;
import br.com.himax.saga.orchestration.product.domain.service.ProductValidationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Component
public class ProductConsumer {
    private static final String SUCCESS = "product-success";
    private static final String FAIL = "product-fail";
    private final JsonHelper jsonHelper;
    private ProductValidationService productService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = SUCCESS)
    public void consumeSuccessEvent(String payload){
        log.info("Receiving success event {} from {} topic",payload, SUCCESS);
        Event event =  jsonHelper.toEvent(payload);
        productService.validadeExistingProducts(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = FAIL)
    public void consumeFailEvent(String payload){
        log.info("Receiving rollback event {} from {} topic",payload, FAIL);
        Event event =  jsonHelper.toEvent(payload);
        productService.rollbackEvent(event);
    }
}

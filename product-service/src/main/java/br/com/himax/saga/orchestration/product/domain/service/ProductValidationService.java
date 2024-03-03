package br.com.himax.saga.orchestration.product.domain.service;

import br.com.himax.saga.orchestration.product.api.model.Event;
import br.com.himax.saga.orchestration.product.api.model.History;
import br.com.himax.saga.orchestration.product.api.model.OrderItem;
import br.com.himax.saga.orchestration.product.api.producer.ProductProducer;
import br.com.himax.saga.orchestration.product.core.mapper.JsonHelper;
import br.com.himax.saga.orchestration.product.domain.exception.ProductNotFoundException;
import br.com.himax.saga.orchestration.product.domain.exception.ValidationException;
import br.com.himax.saga.orchestration.product.domain.model.ValidationProduct;
import br.com.himax.saga.orchestration.product.domain.repository.ProductRepository;
import br.com.himax.saga.orchestration.product.domain.repository.ValidationRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static br.com.himax.saga.orchestration.product.core.enums.ESagaStatus.*;

@Slf4j
@AllArgsConstructor
@Service
public class ProductValidationService {
    private static final String CURRENT_SOURCE = "PRODUCT_SERVICE";

    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;
    private final JsonHelper helper;
    private final ProductProducer producer;
    private final Validator validator;

    public void validadeExistingProducts(Event event) {
        try {
            validateProduct(event);
            verifyIfAllProductsExist(event);
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSucces(event);
        } catch (Exception ex) {
            log.error("Error trying to validade products: ",ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }finally {
            producer.sendEvent(helper.toJson(event));
        }
    }

    private void validateProduct(Event event) {
        if(event == null){
            throw new ValidationException("Invalid payload: ".concat(helper.toJson(event)));
        }

        Set<ConstraintViolation<Event>> errors = validator.validate(event);
        if(!errors.isEmpty()){
           Optional<ConstraintViolation<Event>> erro =  errors.stream().findFirst();
            ConstraintViolation<Event> e = erro.get();
            throw new ValidationException(String.format("Property %s %s", e.getPropertyPath(),e.getMessage()));
        }
    }

    public void rollbackEvent(Event event){
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Rollback executed on product validation");
        producer.sendEvent(helper.toJson(event));
        changeValidationToFail(event);
    }

    private void changeValidationToFail(Event event) {
        validationRepository
                .findByOrderIdAndTransactionId(event.getPayloadOrderId(), event.getPayloadTransactionId())
                .ifPresentOrElse(
                        this::updateValidation,
                        ()-> createValidation(event, false));
    }

    private void updateValidation(ValidationProduct validation) {
        validation.setSuccess(false);
        validationRepository.save(validation);
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to validade products: ".concat(message));
    }

    private void handleSucces(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Products were validated successfully!");
    }

    private void addHistory(Event event, String message){
        History history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addHistory(history);
    }

    private void checkCurrentValidation(Event event) {
        if (validationRepository.existsByOrderIdAndTransactionId(
                event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
    }

    private void createValidation(Event event, boolean success) {
        ValidationProduct validation = ValidationProduct.builder()
                .orderId(event.getPayloadOrderId())
                .transactionId(event.getPayloadTransactionId())
                .success(success)
                .build();

        validationRepository.save(validation);
    }

    private void verifyIfAllProductsExist(Event event){
        event.getProducts()
            .forEach(product->{
                if(!productExists(product)){
                    throw new ProductNotFoundException(String.format("Product with id %s not found",product.getProductCode()));
                }
            });
    }

    private boolean productExists(OrderItem product) {
        return productRepository.existsByCode(product.getProductCode());
    }
}

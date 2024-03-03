package br.com.himax.saga.orchestration.payment.domain.service;

import br.com.himax.saga.orchestration.payment.api.model.Event;
import br.com.himax.saga.orchestration.payment.api.model.History;
import br.com.himax.saga.orchestration.payment.api.model.OrderItem;
import br.com.himax.saga.orchestration.payment.api.producer.PaymentProducer;
import br.com.himax.saga.orchestration.payment.core.enums.EPaymentStatus;
import br.com.himax.saga.orchestration.payment.core.enums.ESagaStatus;
import br.com.himax.saga.orchestration.payment.core.mapper.JsonHelper;
import br.com.himax.saga.orchestration.payment.domain.exception.ValidationException;
import br.com.himax.saga.orchestration.payment.domain.model.Payment;
import br.com.himax.saga.orchestration.payment.domain.repository.PaymentRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static br.com.himax.saga.orchestration.payment.core.enums.ESagaStatus.FAIL;
import static br.com.himax.saga.orchestration.payment.core.enums.ESagaStatus.ROLLBACK_PENDING;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentService {
    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
    private static final Double REDUCE_SUM_VALUE = 0.0;
    private static final Double MIN_VALUE_AMOUNT = 0.1;

    private final PaymentRepository paymentRepository;
    private final JsonHelper helper;
    private final PaymentProducer producer;
    private final Validator validator;

    public void realizePayment(Event event) {
        try {
            validatePayment(event);
            checkCurrentValidation(event);
            createPaymentPending(event, true);
            Payment payment = findByOrderIdAndTransactionId(event);
            validateAmount(payment.getTotalAmount());
            integrateWithExternalPaymentApi();
            changePaymentToSuccess(payment);
            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error trying to make payment: ",ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }finally {
            producer.sendEvent(helper.toJson(event));
        }
    }

    private void integrateWithExternalPaymentApi() {
        log.info("Integrate with external API vendor like PAGAR-ME, MERCADO PAGO, MASTERCARD, VISA");
    }

    private void validatePayment(Event event) {
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

    private void checkCurrentValidation(Event event) {
        if (paymentRepository.existsByOrderIdAndTransactionId(
                event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
    }

    private void createPaymentPending(Event event, boolean success) {
        var totalAmount = calculateAmount(event);
        var totalItems = calculateTotalItems(event);

        Payment payment = Payment.builder()
                .orderId(event.getPayloadOrderId())
                .transactionId(event.getPayloadTransactionId())
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .build();

        save(payment);
        setEventAmountItems(event, payment);
    }

    private double calculateAmount(Event event) {
        return event
                .getPayload()
                .getProducts()
                .stream()
                .map(product -> product.getQuantity() * product.getProduct().getUnitValue())
                .reduce(REDUCE_SUM_VALUE, Double::sum);
    }

    private int calculateTotalItems(Event event) {
        return event
                .getPayload()
                .getProducts()
                .stream()
                .map(OrderItem::getQuantity)
                .reduce(REDUCE_SUM_VALUE.intValue(), Integer::sum);
    }

    private void setEventAmountItems(Event event, Payment payment) {
        event.getPayload().setTotalAmount(payment.getTotalAmount());
        event.getPayload().setTotalItems(payment.getTotalItems());
    }

    private void validateAmount(double amount) {
        if (amount < MIN_VALUE_AMOUNT) {
            throw new ValidationException("The minimal amount available is ".concat(String.valueOf(MIN_VALUE_AMOUNT)));
        }
    }

    private void changePaymentToSuccess(Payment payment) {
        payment.setStatus(EPaymentStatus.SUCCESS);
        save(payment);
    }

    private void handleSuccess(Event event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Payment realized successfully!");
    }

    private void addHistory(Event event, String message) {
        History history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addHistory(history);
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to realize payment: ".concat(message));
    }

    public void realizeRefund(Event event) {
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);

        try {
            changePaymentStatusToRefund(event);
            addHistory(event, "Rollback executed for payment!");
        } catch (Exception ex) {
            addHistory(event, "Rollback not executed for payment: ".concat(ex.getMessage()));
        }finally {
            producer.sendEvent(helper.toJson(event));
        }
    }

    private void changePaymentStatusToRefund(Event event) {
        var payment = findByOrderIdAndTransactionId(event);
        payment.setStatus(EPaymentStatus.REFUND);
        setEventAmountItems(event, payment);
        save(payment);
    }

    private Payment findByOrderIdAndTransactionId(Event event) {
        return paymentRepository
                .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .orElseThrow(() -> new ValidationException("Payment not found by orderID and transactionID"));
    }

    private void save(Payment payment) {
        paymentRepository.save(payment);
    }
}

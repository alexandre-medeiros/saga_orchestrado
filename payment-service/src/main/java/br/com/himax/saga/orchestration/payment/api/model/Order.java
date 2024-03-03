package br.com.himax.saga.orchestration.payment.api.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @NotEmpty
    private String id;
    @Valid
    @NotNull
    private List<OrderItem> products;
    @NotEmpty
    private String transactionId;
    private double totalAmount;
    private int totalItems;
    private LocalDateTime createdAt;
}

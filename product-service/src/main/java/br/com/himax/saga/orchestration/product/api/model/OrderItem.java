package br.com.himax.saga.orchestration.product.api.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Valid
    @NotNull
    private Product product;
    @Positive
    private int quantity;

    public String getProductCode(){
        return product.getCode();
    }
}

package br.com.himax.saga.orchestration.order.api.dto;

import br.com.himax.saga.orchestration.order.domain.document.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private List<OrderItem> products;
}

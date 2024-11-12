package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.PostOrderResponse;
import wonpick.travel.server.entity.Order;
import wonpick.travel.server.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public PostOrderResponse saveOrder(String orderId, int amount) {
        Order order = Order.createPendingOrder(orderId, amount);
        Order savedOrder = orderRepository.save(order);
        return new PostOrderResponse(savedOrder.getId());
    }
}

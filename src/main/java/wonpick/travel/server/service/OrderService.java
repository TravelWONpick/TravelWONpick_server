package wonpick.travel.server.service;

import com.auth0.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.PostOrderResponse;
import wonpick.travel.server.entity.Order;
import wonpick.travel.server.entity.User;
import wonpick.travel.server.repository.OrderRepository;
import wonpick.travel.server.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(OrderService.class);


    public PostOrderResponse saveOrder(String accessToken, String orderId, int amount) {
        logger.info("[OrderService.saveOrder]");

        // JWT Decode 후 User 조회
        String sub = JWT.decode(accessToken).getSubject();
        User user = userRepository.findBySub(sub).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        // 주문 생성
        Order order = Order.createPendingOrder(user, orderId, amount);

        Order savedOrder = orderRepository.save(order);
        return new PostOrderResponse(savedOrder.getId());
    }


    public Order findByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다."));
    }
}

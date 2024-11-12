package wonpick.travel.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wonpick.travel.server.entity.enums.OrderStatus;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_seq_id", nullable = false)
    private Long id; // DB에서 자동 생성되는 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 주문을 한 사용자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation; // 주문에 연결된 예약

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId; // 클라이언트에서 제공하는 주문번호 UUID

    @Column(name = "amount", nullable = false)
    private int amount; // 최종 결제 금액

    @Column(name = "status", nullable = false)
    private OrderStatus status; // 결제 상태

    // 결제 대기 상태로 Order 생성
    // TODO: 결제 승인 완료 시 PAID 상태로 변경
    public static Order createPendingOrder(String orderId, int amount) {
        return Order.builder()
                .orderId(orderId)
                .amount(amount)
                .status(OrderStatus.PENDING)
                .build();
    }
}

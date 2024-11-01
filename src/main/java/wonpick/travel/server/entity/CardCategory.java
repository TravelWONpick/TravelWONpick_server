package wonpick.travel.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wonpick.travel.server.entity.enums.CardCategoryType;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardCategory extends BaseEntity{
    @Id
    @Column(name = "cc_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name="name", nullable = false)
    private CardCategoryType categoryType;

    @Column(nullable = false)
    private String image;
}

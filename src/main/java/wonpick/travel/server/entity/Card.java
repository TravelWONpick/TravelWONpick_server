package wonpick.travel.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wonpick.travel.server.entity.enums.CardType;

import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Card extends BaseEntity {

    @Id
    @Column(name = "card_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "annual_fee", nullable = false)
    private Integer annualFee;

    @Column(nullable = false)
    private String image;

    @Column(name = "detail_link", nullable = false)
    private String detailLink;

    @Column(name = "apply_link", nullable = false)
    private String applyLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType type;

    @OneToMany(mappedBy = "cardBenefit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardBenefit> cardBenefits;

    @OneToMany(mappedBy = "cardCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardCategory> cardCategories;
}

package wonpick.travel.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wonpick.travel.server.entity.enums.CardCategoryType;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {

    @Id
    @Column(name = "category_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CardCategoryType type;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardCategory> cardCategories;
}

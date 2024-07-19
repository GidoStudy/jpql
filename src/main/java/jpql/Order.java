package jpql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="ORDERS")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    private Long id;
    private int orderAmount;
    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}

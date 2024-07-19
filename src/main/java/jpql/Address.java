package jpql;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@AllArgsConstructor @NoArgsConstructor
@ToString
public class Address {

    private String city;
    private String street;
    private String zipcode;


}

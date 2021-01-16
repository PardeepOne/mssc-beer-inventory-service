package guru.springframework.beer.common.events;

import guru.springframework.beer.inventory.service.web.model.BeerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BeerEvent implements Serializable {
    private static final long serialVersionUID = -8220039980451224159L;
    BeerDto beerDto;
}

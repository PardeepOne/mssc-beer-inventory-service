package guru.springframework.beer.common.events;

import guru.springframework.beer.inventory.service.web.model.BeerDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewInventoryEvent extends guru.springframework.beer.common.events.BeerEvent {
    private static final long serialVersionUID = -3897841818216307649L;

    public NewInventoryEvent(BeerDto beerDto){super(beerDto);}
}

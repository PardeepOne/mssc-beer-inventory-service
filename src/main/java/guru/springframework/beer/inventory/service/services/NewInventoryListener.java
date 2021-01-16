package guru.springframework.beer.inventory.service.services;

import guru.springframework.beer.common.events.NewInventoryEvent;
import guru.springframework.beer.inventory.service.config.JmsConfig;
import guru.springframework.beer.inventory.service.domain.BeerInventory;
import guru.springframework.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.springframework.beer.inventory.service.web.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewInventoryListener {
    private final JmsTemplate jmsTemplate;
    private final BeerInventoryRepository beerInventoryRepository;

    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event){
        log.debug("Received message: {}",event);
        log.debug("Received message's content: {}",event.getBeerDto());
        BeerDto receivedBeerDto = event.getBeerDto();

        BeerInventory beerInventory = BeerInventory.builder().beerId(receivedBeerDto.getId()).upc(receivedBeerDto.getUpc()).quantityOnHand(
                receivedBeerDto.getQuantityOnHand()).build();

        //inserting the new inventory into the repository
        beerInventoryRepository.save(beerInventory);
    }
}

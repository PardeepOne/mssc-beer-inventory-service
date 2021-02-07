package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderLineDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {

    private final BeerInventoryRepository beerInventoryRepository;

    @Override
    public Boolean allocateOrder(BeerOrderDto beerOrderDto) {
        log.debug("Allocation order with Id: '{}'", beerOrderDto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        beerOrderDto.getBeerOrderLines()
                .forEach(beerOrderLine -> {
                    int quantityAllocated = beerOrderLine.getQuantityAllocated() != null ? beerOrderLine.getQuantityAllocated() : 0;
                    int quantityOrdered = beerOrderLine.getOrderQuantity() != null ? beerOrderLine.getOrderQuantity() : 0;
                    if ((quantityOrdered
                            - quantityAllocated) > 0) {
                        allocateBeerOrderLine(beerOrderLine);
                    }
                    totalOrdered.set(totalOrdered.get() + beerOrderLine.getOrderQuantity());
                    totalAllocated.set(totalAllocated.get() + quantityAllocated);
                });

        log.debug("Total Ordered: "+totalOrdered.get() + " - Total allocated: "+totalAllocated.get());

        return totalOrdered.get() == totalAllocated.get();
    }

    private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLineDto){
        List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(beerOrderLineDto.getUpc());

        beerInventoryList.forEach(beerInventory -> {
            int quantityOnHand = beerInventory.getQuantityOnHand() == null ? 0 : beerInventory.getQuantityOnHand();
            final int orderQuantity = beerOrderLineDto.getOrderQuantity() == null ? 0 : beerOrderLineDto.getOrderQuantity();
            final int quantityAllocated = beerOrderLineDto.getQuantityAllocated() == null ? 0 : beerOrderLineDto.getQuantityAllocated();
            int qtyToAllocate = orderQuantity - quantityAllocated;

            if(quantityOnHand >= qtyToAllocate){
                //Full Allocation
                quantityOnHand = quantityOnHand - qtyToAllocate;

                //because we already have the ordered quantity in our inventory
                beerOrderLineDto.setQuantityAllocated(orderQuantity);
                beerInventory.setQuantityOnHand(quantityOnHand);

                //saving the inventory status
                beerInventoryRepository.save(beerInventory);
            }else if(quantityOnHand > 0){
                //Partial Allocation
                beerOrderLineDto.setQuantityAllocated(quantityAllocated + quantityOnHand);
                beerInventory.setQuantityOnHand(0);

                //deleting the beer inventory as it has been consumed
                beerInventoryRepository.delete(beerInventory);
            }
        });
    }
}

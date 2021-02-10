package guru.sfg.beer.inventory.service.services.listener;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.services.AllocationService;
import guru.sfg.brewery.model.events.AllocateOrderRequest;
import guru.sfg.brewery.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationListener {
    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_REQUEST_QUEUE)
    public void listen(AllocateOrderRequest allocateOrderRequest) {
        AllocateOrderResult.AllocateOrderResultBuilder allocationBuilder = AllocateOrderResult.builder();
        allocationBuilder.beerOrderDto(allocateOrderRequest.getBeerOrderDto());


        try {
            Boolean allocationResult = allocationService.allocateOrder(allocateOrderRequest.getBeerOrderDto());
            allocationBuilder.pendingInventory(!allocationResult);
        } catch (Exception e) {
            log.error("Allocation failed for Id: {}",allocateOrderRequest.getBeerOrderDto().getId());
            allocationBuilder.allocationError(true);
        }

        //sending allocation result over JMS
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE,
                allocationBuilder.build());
    }
}

package io.eventuate.examples.tram.ordersandcustomers.orders.service;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.CustomerCreditReservationFailedEvent;
import io.eventuate.examples.tram.ordersandcustomers.commondomain.CustomerCreditReservedEvent;
import io.eventuate.examples.tram.ordersandcustomers.commondomain.CustomerValidationFailedEvent;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.springframework.beans.factory.annotation.Autowired;


public class CustomerEventConsumer {

  @Autowired
  private OrderService orderService;

  public DomainEventHandlers domainEventHandlers() {
	  // domainEventHandler 메소드가 호출 될 때 CusomerEventConsummer에 있는 모든 메소드들을 Event로 등록해 해당 정보를 반환한다.
	  // e.g. this::handleCustomerCreditReservedEvent 등
    return DomainEventHandlersBuilder
            .forAggregateType("io.eventuate.examples.tram.ordersandcustomers.customers.domain.Customer")
            .onEvent(CustomerCreditReservedEvent.class, this::handleCustomerCreditReservedEvent)
            .onEvent(CustomerCreditReservationFailedEvent.class, this::handleCustomerCreditReservationFailedEvent)
            .onEvent(CustomerValidationFailedEvent.class, this::handleCustomerValidationFailedEvent)
            .build();
  }

  private void handleCustomerCreditReservedEvent(DomainEventEnvelope<CustomerCreditReservedEvent> domainEventEnvelope) {
    orderService.approveOrder(domainEventEnvelope.getEvent().getOrderId());
  }

  private void handleCustomerCreditReservationFailedEvent(DomainEventEnvelope<CustomerCreditReservationFailedEvent> domainEventEnvelope) {
    orderService.rejectOrder(domainEventEnvelope.getEvent().getOrderId());
  }

  private void handleCustomerValidationFailedEvent(DomainEventEnvelope<CustomerValidationFailedEvent> domainEventEnvelope) {
    orderService.rejectOrder(domainEventEnvelope.getEvent().getOrderId());
  }
}

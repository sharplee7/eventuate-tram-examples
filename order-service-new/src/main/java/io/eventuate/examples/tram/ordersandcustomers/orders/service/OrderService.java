package io.eventuate.examples.tram.ordersandcustomers.orders.service;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.OrderApprovedEvent;
import io.eventuate.examples.tram.ordersandcustomers.commondomain.OrderDetails;
import io.eventuate.examples.tram.ordersandcustomers.commondomain.OrderRejectedEvent;
import io.eventuate.examples.tram.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Collections.singletonList;

@Transactional
public class OrderService {

  @Autowired
  private DomainEventPublisher domainEventPublisher;

  @Autowired
  private OrderRepository orderRepository;

  /**
   * 주문 생성
   * @param orderDetails
   * @return
   */
  public Order createOrder(OrderDetails orderDetails) {
    ResultWithEvents<Order> orderWithEvents = Order.createOrder(orderDetails);
    Order order = orderWithEvents.result;
    orderRepository.save(order);
    domainEventPublisher.publish(Order.class, order.getId(), orderWithEvents.events);
    return order;
  }

  public void approveOrder(Long orderId) {
    Order order = orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("order with id %s not found", orderId)));
    order.noteCreditReserved();
    domainEventPublisher.publish(Order.class,
            orderId, singletonList(new OrderApprovedEvent(order.getOrderDetails())));
    
    // debug code added by euidoek.lee
    try {
    	throw new Exception();
    } catch (Exception e) {
    	System.out.println("@OrderService.approveOrder ************* ");
    	e.printStackTrace();
    }
    
  }

  public void rejectOrder(Long orderId) {
    Order order = orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("order with id %s not found", orderId)));
    order.noteCreditReservationFailed();
    domainEventPublisher.publish(Order.class,
            orderId, singletonList(new OrderRejectedEvent(order.getOrderDetails())));
    
    // debug code added by euidoek.lee
    try {
    	throw new Exception();
    } catch (Exception e) {
    	System.out.println("@OrderService.rejectOrder ************* ");
    	e.printStackTrace();
    }
  }
}

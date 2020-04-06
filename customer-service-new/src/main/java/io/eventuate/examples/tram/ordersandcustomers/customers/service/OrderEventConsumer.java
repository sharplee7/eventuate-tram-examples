package io.eventuate.examples.tram.ordersandcustomers.customers.service;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.CustomerCreditReservationFailedEvent;
import io.eventuate.examples.tram.ordersandcustomers.commondomain.CustomerCreditReservedEvent;
import io.eventuate.examples.tram.ordersandcustomers.commondomain.CustomerValidationFailedEvent;
import io.eventuate.examples.tram.ordersandcustomers.commondomain.OrderCreatedEvent;
import io.eventuate.examples.tram.ordersandcustomers.customers.domain.Customer;
import io.eventuate.examples.tram.ordersandcustomers.customers.domain.CustomerCreditLimitExceededException;
import io.eventuate.examples.tram.ordersandcustomers.customers.domain.CustomerRepository;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Optional;

public class OrderEventConsumer {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private DomainEventPublisher domainEventPublisher;

	/**
	 * domainEventHandler생성, 이 이벤트 핸들러가 카프카(Queue)로부터 오는 메시지를 컨슈밍함
	 * 생성시 OrderEventConsumer에서 사용하는 모든 메소드 정보를 등록
	 * @return
	 */
	public DomainEventHandlers domainEventHandlers() {
		return DomainEventHandlersBuilder
				.forAggregateType("io.eventuate.examples.tram.ordersandcustomers.orders.domain.Order")
				.onEvent(OrderCreatedEvent.class, this::orderCreatedEventHandler).build();
	}
	
	/**
	 * Message Queue로부터 들어오는 메시지를 컨슈밍
	 * Order서비스에서 주문이 들어오면 그 주문이 유효한지 체크한다.
	 * @param domainEventEnvelope
	 */
	public void orderCreatedEventHandler(DomainEventEnvelope<OrderCreatedEvent> domainEventEnvelope) {
		Long orderId = Long.parseLong(domainEventEnvelope.getAggregateId());
		OrderCreatedEvent orderCreatedEvent = domainEventEnvelope.getEvent();
		Long customerId = orderCreatedEvent.getOrderDetails().getCustomerId();
		//여기서는 서비스를 이용하지 않고 customerRepository를 이용해 데이터 직접 조회(트랜잭션이 없어서?)
		// Order서비스에서 주문 차감한 정보를 kafka로부터 받아 먼저 주문자의 id가 유효한 정보인지 체크한다.
		Optional<Customer> possibleCustomer = customerRepository.findById(customerId);
		// 만약 주문자 아이디가 없다면 CustomerValidationFailedEvent 전송하고 종료
		if (!possibleCustomer.isPresent()) {
			logger.info("Non-existent customer: {}", customerId);
			domainEventPublisher.publish(Customer.class, customerId,
					Collections.singletonList(new CustomerValidationFailedEvent(orderId)));

			return;
		}
		// 조회된 사용자 정보로 인스턴스 생성
		Customer customer = possibleCustomer.get();

		try {
			// 주문정보로 전달된 메시지로부터 사용자 id와 크래딧 차감을 저장
			customer.reserveCredit(orderId, orderCreatedEvent.getOrderDetails().getOrderTotal());
			CustomerCreditReservedEvent customerCreditReservedEvent = new CustomerCreditReservedEvent(orderId);
			// OUTERBOX에 정보 저장 ... 
			domainEventPublisher.publish(Customer.class, customer.getId(),
					Collections.singletonList(customerCreditReservedEvent));

		} catch (CustomerCreditLimitExceededException e) {
			CustomerCreditReservationFailedEvent customerCreditReservationFailedEvent = 
					new CustomerCreditReservationFailedEvent(orderId);
			
			domainEventPublisher.publish(Customer.class, customer.getId(),
					Collections.singletonList(customerCreditReservationFailedEvent));
		}
	}
}

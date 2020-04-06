package io.eventuate.examples.tram.ordersandcustomers.customers.service;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.ordersandcustomers.customers.domain.Customer;
import io.eventuate.examples.tram.ordersandcustomers.customers.domain.CustomerRepository;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerService {

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private DomainEventPublisher domainEventPublisher;

  /**
   * name과 creditLimit은 화면으로부터 넘어옴
   * @param name
   * @param creditLimit
   * @return
   */
  public Customer createCustomer(String name, Money creditLimit) {
	// Customer 객체 기반의 ResultWithEvent 객체를 생성한다. 도메인 이벤트 정보를 기록하고 있다.
    ResultWithEvents<Customer> customerWithEvents = Customer.create(name, creditLimit);
    // 생성된 도메인 이벤트 결과를 기반으로 저장한다. // DB저장
    Customer customer = customerRepository.save(customerWithEvents.result);
    // entity class, unique id 및 도메인 이벤트 정보 기록(Bin/Wal 혹은 Polling 방식에 따라 다름)
    domainEventPublisher.publish(Customer.class, customer.getId(), customerWithEvents.events);
    return customer;
  }
}

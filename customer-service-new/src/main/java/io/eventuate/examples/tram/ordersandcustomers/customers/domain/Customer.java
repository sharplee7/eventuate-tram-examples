package io.eventuate.examples.tram.ordersandcustomers.customers.domain;

import static java.util.Collections.singletonList;

import java.util.Collections;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.CustomerCreatedEvent;
import io.eventuate.examples.tram.ordersandcustomers.commondomain.Money;
import io.eventuate.tram.events.publisher.ResultWithEvents;

@Entity
@Table(name="Customer")
@Access(AccessType.FIELD)
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @Embedded
  private Money creditLimit;

  @ElementCollection
  private Map<Long, Money> creditReservations;

  Money availableCredit() {
    return creditLimit.subtract(creditReservations.values().stream().reduce(Money.ZERO, Money::add));
  }

  public Customer() {
  }

  public Customer(String name, Money creditLimit) {
    this.name = name;
    this.creditLimit = creditLimit;
    this.creditReservations = Collections.emptyMap();
  }

  public static ResultWithEvents<Customer> create(String name, Money creditLimit) {
	    Customer customer = new Customer(name, creditLimit);
	    ResultWithEvents<Customer> resultWithEvents = new ResultWithEvents<>(customer,
	            singletonList(new CustomerCreatedEvent(customer.getName(), customer.getCreditLimit())));
	    
	    return resultWithEvents;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Money getCreditLimit() {
    return creditLimit;
  }

  public void reserveCredit(Long orderId, Money orderTotal) {
	  // 사용 크래딧과 보유 크래딧 비교
    if (availableCredit().isGreaterThanOrEqual(orderTotal)) {
    	// 차감
      creditReservations.put(orderId, orderTotal);
    } else {
     // 만약 차감하려는 크래딧이 보유크래딧 보다 크다면 에러
    	throw new CustomerCreditLimitExceededException();
    }
  }
}

package org.payment.service;

import org.payment.model.Customer;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {

    private final List<Customer> customerList;

    public CustomerService(List<List<String>> records) {
        this.customerList = convertToCustomers(records);
    }

    public List<Customer> convertToCustomers(List<List<String>> records) {
        return records.stream().map(strings -> Customer.builder()
                        .webshopId(strings.get(0))
                        .customerId(strings.get(1))
                        .name(strings.get(2))
                        .address(strings.get(3))
                        .build())
                .collect(Collectors.toList());
    }

    public Customer findByWebshopIdAndCustomerId(String webshopId, String customerId) {
        return customerList.stream()
                .filter(
                        customer -> customer.webshopId().equals(webshopId) && customer.customerId().equals(customerId))
                .findFirst()
                .orElse(null);
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }
}

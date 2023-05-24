package org.payment.service;

import org.payment.model.Customer;
import org.payment.model.Payment;
import org.payment.model.PaymentType;

import java.util.*;
import java.util.stream.Collectors;

public class PaymentService {

    private final List<Payment> paymentList;
    private final CustomerService customerService;
    public static final int RESULT_LENGTH = 3;

    public PaymentService(CustomerService customerService, List<List<String>> records) {
        this.customerService = customerService;
        this.paymentList = convertToPayments(records);
    }

    public List<Payment> convertToPayments(List<List<String>> records) {

        return records.stream().map(strings -> Payment.builder()
                        .webshopId(strings.get(0))
                        .customer(customerService.findByWebshopIdAndCustomerId(strings.get(0), strings.get(1)))
                        .paymentType(PaymentType.valueOf(strings.get(2).toUpperCase()))
                        .amount(Integer.parseInt(strings.get(3)))
                        .bankAccountNumber(strings.get(4))
                        .cardNumber(strings.get(5))
                        .date(strings.get(6))
                        .build())
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByWebshopIdAndCustomerId(String webshopId, String customerId) {
        return paymentList.stream()
                .filter(payment -> payment.getWebshopId().equals(webshopId) &&
                        payment.getCustomer().customerId().equals(customerId)).collect(Collectors.toList());
    }

    public String[] provideNameAddressTotalAmount(Customer customer) {
        String[] result = new String[RESULT_LENGTH];
        List<Payment> payments = getPaymentsByWebshopIdAndCustomerId(customer.webshopId(), customer.customerId());
        String name = customerService.findByWebshopIdAndCustomerId(customer.webshopId(), customer.customerId()).name();
        String address = customerService.findByWebshopIdAndCustomerId(customer.webshopId(), customer.customerId()).address();
        int totalPaymentAmount = 0;
        for (Payment payment : payments) {
            totalPaymentAmount += payment.getAmount();
        }
        result[0] = name;
        result[1] = address;
        result[2] = String.valueOf(totalPaymentAmount);

        return result;
    }

    public List<String[]> provideAllPaymentsWithNameAndAddress() {
        List<Customer> customerList = customerService.getCustomerList();
        List<String[]> payments = new ArrayList<>();

        for (Customer customer : customerList) {
            payments.add(provideNameAddressTotalAmount(customer));
        }
        return payments;
    }

    public List<String[]> provideFirstTwoCustomersByPayment() {

        Map<Customer, Integer> customerPaymentsMap = new HashMap<>();

        for (Payment payment : paymentList) {
            Customer customer = payment.getCustomer();
            int amount = payment.getAmount();

            if (customerPaymentsMap.containsKey(customer)) {
                int totalAmount = customerPaymentsMap.get(customer) + amount;
                customerPaymentsMap.put(customer, totalAmount);
            } else {
                customerPaymentsMap.put(customer, amount);
            }
        }

        List<Map.Entry<Customer, Integer>> sortedCustomerPayments = new ArrayList<>(customerPaymentsMap.entrySet());
        sortedCustomerPayments.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<Customer, Integer> topTwoCustomerByPayment = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<Customer, Integer> entry : sortedCustomerPayments) {
            topTwoCustomerByPayment.put(entry.getKey(), entry.getValue());
            count++;

            if (count == 2) {
                break;
            }
        }

        List<String[]> result = new ArrayList<>();

        for (Map.Entry<Customer, Integer> entry : topTwoCustomerByPayment.entrySet()) {
            Customer customer = entry.getKey();
            int amount = entry.getValue();
            String[] topCustomer = new String[RESULT_LENGTH];
            topCustomer[0] = customer.name();
            topCustomer[1] = customer.address();
            topCustomer[2] = String.valueOf(amount);
            result.add(topCustomer);
        }

        return result;
    }

    public List<String[]> provideWebshopEarnings() {
        Map<String, Integer> webshopEarningsMap = new HashMap<>();

        for (Payment payment : paymentList) {
            String webshopId = payment.getWebshopId();
            int amount = payment.getAmount();

            if (webshopEarningsMap.containsKey(webshopId)) {
                int earnings = webshopEarningsMap.get(webshopId) + amount;
                webshopEarningsMap.put(webshopId, earnings);
            } else {
                webshopEarningsMap.put(webshopId, amount);
            }
        }

        List<String[]> result = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : webshopEarningsMap.entrySet()) {
            String webshopId = entry.getKey();
            int cardAmount = getPaymentAmount(paymentList, webshopId, PaymentType.CARD);
            int transferAmount = getPaymentAmount(paymentList, webshopId, PaymentType.TRANSFER);

            String[] webshop = new String[RESULT_LENGTH];

            webshop[0] = webshopId;
            webshop[1] = String.valueOf(cardAmount);
            webshop[2] = String.valueOf(transferAmount);

            result.add(webshop);
        }

        return result;
    }

    private int getPaymentAmount(List<Payment> paymentList, String webshopId, PaymentType paymentType) {
        int amount = 0;

        for (Payment payment : paymentList) {
            if (payment.getWebshopId().equals(webshopId) && payment.getPaymentType() == paymentType) {
                amount += payment.getAmount();
            }
        }

        return amount;
    }
}

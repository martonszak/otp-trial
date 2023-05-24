package org.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Payment {

    private String webshopId;

    private Customer customer;

    private PaymentType paymentType;

    private int amount;

    private String bankAccountNumber;

    private String cardNumber;

    private String date;

}

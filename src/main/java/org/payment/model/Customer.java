package org.payment.model;

import lombok.Builder;

@Builder
public record Customer(String webshopId, String customerId, String name, String address) {
}

package org.payment;

import org.payment.service.*;

import java.io.IOException;

public class Main {

    public static final String CUSTOMERS_CSV = "src/main/resources/customer.csv";
    public static final String PAYMENTS_CSV = "src/main/resources/payments.csv";

    public static void main(String[] args) throws IOException {
        CsvReader paymentReader = new CsvReader();
        CsvReader customerReader = new CsvReader();
        CsvWriter csvWriter = new CsvWriter();
        CsvPaymentValidator csvPaymentValidator = new CsvPaymentValidator();

        customerReader.read(CUSTOMERS_CSV);
        paymentReader.read(PAYMENTS_CSV);

        CustomerService customerService = new CustomerService(customerReader.getRecords());

        csvPaymentValidator.validate(paymentReader.getRecords(), customerService);

        PaymentService paymentService = new PaymentService(customerService, csvPaymentValidator.getValidRecords());

        csvWriter.writeToFile(paymentService.provideAllPaymentsWithNameAndAddress(), "report01");
        csvWriter.writeToFile(paymentService.provideFirstTwoCustomersByPayment(), "top");
        csvWriter.writeToFile(paymentService.provideWebshopEarnings(), "report02");
    }
}
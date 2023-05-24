package org.payment.service;

import org.payment.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvPaymentValidator {

    List<String> validators = new ArrayList<>();
    List<List<String>> validRecords = new ArrayList<>();

    public CsvPaymentValidator() {
    }

    public void validate(List<List<String>> records, CustomerService customerService) {

        List<Customer> customerList = customerService.getCustomerList();

        ErrorLogger errorLogger = new ErrorLogger();
        validators.add("^WS\\d\\d$");
        // Validator for the first column to start with WS followed by two numbers
        validators.add("^A\\d\\d$");
        // Validator for the second column to start with A followed by two numbers
        validators.add("^(card|transfer)$");
        // Validator for the third column to be "card" or "transfer"
        validators.add("^[0-9]{1,6}$");
        // Validator for the fourth column to allow only numbers and max 6 long
        validators.add("^(?:\\d{16})?$");
        // Validator for the fifth column to allow only numbers and 0 or 16 long
        validators.add("^(?:\\d{16})?$");
        // Validator for the sixth column to allow only numbers and 0 or 16 long
        validators.add("^(\\d{4})\\.(0[1-9]|1[0-2])\\.(0[1-9]|[12]\\d|3[01])$");
        // Validator for the seventh column to allow only YYYY.MM.DD format

        // Validate all columns width the specified regex validators
        for (int i = 0; i < records.size(); i++) {
            boolean isRecordValid = true;
            for (int j = 0; j < records.get(i).size(); j++) {
                Pattern pattern = Pattern.compile(validators.get(j));
                Matcher matcher = pattern.matcher(records.get(i).get(j));
                if (!matcher.matches()) {
                    errorLogger.writeLog("Input does not match the pattern in line " + (i + 1) + " at column " + (j + 1));
                    isRecordValid = false;
                }
            }

            // Card number / Bank account number validation
            if (records.get(i).get(2).equals("card") && !records.get(i).get(4).equals("")) {
                errorLogger.writeLog("Error in line " + (i + 1) + ", bank account number provided for card.");
                isRecordValid = false;
            }

            // Customer validation
            for (int k = 0; k < customerList.size(); k++) {
                if (records.get(i).get(0).equals(customerList.get(k).webshopId()) && records.get(i).get(1).equals(customerList.get(k).customerId())) {
                    isRecordValid = true;
                    break;
                } else {
                    isRecordValid = false;
                }
            }

            if (isRecordValid) {
                validRecords.add(records.get(i));
            }

        }
        System.out.println("Validation finished.");
    }

    public List<List<String>> getValidRecords() {
        return validRecords;
    }
}

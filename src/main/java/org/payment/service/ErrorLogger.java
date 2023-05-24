package org.payment.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ErrorLogger {

    private static final String PATH = "src/main/resources/application.log";

    public void writeLog(String error) {
        try (FileWriter fileWriter = new FileWriter(PATH, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(error + System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

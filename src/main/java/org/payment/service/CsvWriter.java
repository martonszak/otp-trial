package org.payment.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvWriter {

    public static final String FILETYPE_CSV = ".csv";
    public static final String FILE_PATH = "src/main/resources/";

    private String convertToCsv(String[] strings) {
        return Stream.of(strings)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public void writeToFile(List<String[]> strings, String fileName) throws IOException {
        File outPutFile = new File(FILE_PATH + fileName + FILETYPE_CSV);
        try (PrintWriter printWriter = new PrintWriter(outPutFile)) {
            strings.stream()
                    .map(this::convertToCsv)
                    .forEach(printWriter::println);
        }

    }
}

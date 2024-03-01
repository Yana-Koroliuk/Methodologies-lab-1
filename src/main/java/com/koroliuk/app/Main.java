package com.koroliuk.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Path: ");
        String inputData = scanner.nextLine();
        try {
            if (inputData.contains("--out")) {
                String inputFile = List.of(inputData.split(" ")).get(0);
                String outputFile = List.of(inputData.split(" ")).get(2);
                String content = Files.readString(Paths.get(inputFile));
                Converter converter = new Converter(content);
                String outputHtml = converter.markdownToHtml();
                Files.writeString(Paths.get(outputFile), outputHtml);
            } else {
                String content = Files.readString(Paths.get(inputData));
                Converter converter = new Converter(content);
                System.out.println(converter.markdownToHtml());
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
    }
}
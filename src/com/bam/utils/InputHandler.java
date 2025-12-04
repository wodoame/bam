package com.bam.utils;
import java.util.Scanner;

public class InputHandler {
    private final Scanner scanner = new Scanner(System.in);

    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public double getDoubleInput(String prompt, String errorMessage) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print(errorMessage + '\n');
            System.out.print(prompt);
            scanner.next(); // consume bad input
        }
        double input = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return input;
    }


    public int getIntInput(String prompt, String errorMessage) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print(errorMessage + '\n');
            System.out.print(prompt);
            scanner.next(); // consume bad input
        }
        int input = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return input;
    }

    public void closeScanner() {
        scanner.close();
    }

}

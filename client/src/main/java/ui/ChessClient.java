package ui;

import java.util.Scanner;

public class ChessClient {
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public void run() {
        System.out.println("♕ Welcome to Chess. Sign in to start. ♕");

        while (running) {
            printPreloginMenu();
            System.out.print("Enter command: ");

            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "1", "l", "login":
                    System.out.println("Login selected");
                    break;
                case "2", "r", "register":
                    System.out.println("Register selected");
                    break;
                case "3", "q", "quit":
                    running = false;
                    break;
                case "4", "h", "help":
                    printPreloginMenu();
                    break;
                default:
                    System.out.println("Invalid command.");
                    break;
            }
        }

        System.out.println("Goodbye!");
    }

    private void printPreloginMenu() {
        System.out.println();
        System.out.println("Options:");
        System.out.println("1. Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("2. Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("3. Exit the program: \"q\", \"quit\"");
        System.out.println("4. Print this message: \"h\", \"help\"");
    }
}

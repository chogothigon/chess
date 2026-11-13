package ui;

import java.util.Scanner;
import client.ServerFacade;

public class ChessClient {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade = new ServerFacade(8080);
    private boolean running = true;
    private String authToken;

    public void run() {
        System.out.println("♕ Welcome to Chess. Sign in to start. ♕");
        printPreloginMenu();

        while (running) {
            System.out.print("Enter command: ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            switch (command) {
                case "1", "l", "login":
                    login(parts);
                    break;
                case "2", "r", "register":
                    register(parts);
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


    private void login(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Usage: login <USERNAME> <PASSWORD>");
            return;
        }

        String username = parts[1];
        String password = parts[2];

        try {
            var result = facade.login(username, password);
            authToken = result.authToken();

            System.out.println("Successfully logged in as " + result.username());

        } catch (RuntimeException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void register(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }

        String username = parts[1];
        String password = parts[2];
        String email = parts[3];

        try {
            var result = facade.register(username, password, email);
            authToken = result.authToken();

            System.out.println("Successfully registered as " + result.username());

        } catch (RuntimeException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}

package ui;

import client.ServerFacade;
import java.util.Scanner;

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
                System.out.println("Invalid command.");
                continue;
            }

            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            if (authToken != null) {
                handlePostlogin(command, parts);
                continue;
            }

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

    private void printPostloginMenu() {
        System.out.println();
        System.out.println("Options:");
        System.out.println("List current games: \"l\", \"list\"");
        System.out.println("Create a new game: \"c\", \"create\" <GAME NAME>");
        System.out.println("Join a game: \"j\", \"join\" <GAME ID> <COLOR>");
        System.out.println("Watch a game: \"w\", \"watch\" <GAME ID>");
        System.out.println("Logout: \"logout\"");
    }

    private void handlePostlogin(String command, String[] parts) {
        switch (command) {
            case "l", "list":
                System.out.println("List games selected");
                break;
            case "c", "create":
                createGame(parts);
                break;
            case "j", "join":
                System.out.println("Join game selected");
                break;
            case "w", "watch":
                System.out.println("Watch game selected");
                break;
            case "logout":
                try {
                    facade.logout(authToken);
                    authToken = null;

                    System.out.println("Successfully logged out.");
                    printPreloginMenu();

                } catch (RuntimeException e) {
                    System.out.println("Logout failed: " + e.getMessage());
                }
                break;
            case "h", "help":
                printPostloginMenu();
                break;
            case "q", "quit":
                running = false;
                break;
            default:
                System.out.println("Invalid command.");
                break;
        }
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
            printPostloginMenu();

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
            printPostloginMenu();

        } catch (RuntimeException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void createGame(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Usage: create <GAME_NAME> (no spaces)");
            return;
        }

        String gameName = parts[1];

        try {
            facade.createGame(authToken, gameName);
            System.out.println("Successfully created game: " + gameName);
        } catch (RuntimeException e) {
            System.out.println("Could not create game: " + e.getMessage());
        }
    }
}

package ui;

import client.ServerFacade;
import model.GameData;

import java.util.Scanner;

public class ChessClient {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade = new ServerFacade(8080);
    private boolean running = true;
    private String authToken;
    private GameData[] listedGames = new GameData[0];

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
                case "l", "login":
                    login(parts);
                    break;
                case "r", "register":
                    register(parts);
                    break;
                case "q", "quit":
                    running = false;
                    break;
                case "h", "help":
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
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program: \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");
    }

    private void printPostloginMenu() {
        System.out.println();
        System.out.println("Options:");
        System.out.println("List current games: \"l\", \"list\"");
        System.out.println("Create a new game: \"c\", \"create\" <GAME NAME>");
        System.out.println("Join a game: \"j\", \"join\" <GAME NUMBER> <COLOR>");
        System.out.println("Watch a game: \"w\", \"watch\" <GAME NUMBER>");
        System.out.println("Logout: \"logout\"");
        System.out.println("Exit the program: \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");
    }

    private void handlePostlogin(String command, String[] parts) {
        switch (command) {
            case "l", "list":
                listGames();
                break;
            case "c", "create":
                createGame(parts);
                break;
            case "j", "join":
                joinGame(parts);
                break;
            case "w", "watch":
                watchGame(parts);
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

    private void listGames() {
        try {
            listedGames = facade.listGames(authToken);

            if (listedGames.length == 0) {
                System.out.println("There are no games available.");
                return;
            }

            System.out.println("Current Games:");

            for (int i = 0; i < listedGames.length; i++) {
                GameData game = listedGames[i];

                String white = game.whiteUsername();
                String black = game.blackUsername();

                if (white == null) {
                    white = "Available";
                }

                if (black == null) {
                    black = "Available";
                }

                System.out.println((i + 1) + ". " + game.gameName() + " | White: " + white + " | Black: " + black);
            }

        } catch (RuntimeException e) {
            listedGames = new GameData[0];
            System.out.println("Could not list games: " + e.getMessage());
        }
    }

    private void joinGame(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Usage: join <GAME NUMBER> <COLOR>");
            return;
        }

        int gameNumber;

        try {
            gameNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            System.out.println("Game number must be a number.");
            return;
        }

        if (gameNumber < 1 || gameNumber > listedGames.length) {
            System.out.println("Invalid game number. Use list to see available games.");
            return;
        }

        String color = parts[2].toUpperCase();

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Color must be WHITE or BLACK.");
            return;
        }

        int gameID = listedGames[gameNumber - 1].gameID();

        try {
            facade.joinGame(authToken, gameID, color);
            System.out.println("Joined " + listedGames[gameNumber - 1].gameName() + " as " + color);

        } catch (RuntimeException e) {
            System.out.println("Could not join game: " + e.getMessage());
        }
    }

    private void watchGame(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Usage: watch <GAME NUMBER>");
            return;
        }

        int gameNumber;

        try {
            gameNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            System.out.println("Game number must be a number.");
            return;
        }

        if (gameNumber < 1 || gameNumber > listedGames.length) {
            System.out.println("Invalid game number. Use list to see available games.");
            return;
        }

        var game = listedGames[gameNumber - 1];

        System.out.println("Watching game: " + game.gameName());
        BoardRenderer renderer = new BoardRenderer();
        renderer.drawBoard();
    }
}

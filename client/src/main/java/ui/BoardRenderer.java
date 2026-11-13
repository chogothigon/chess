package ui;

import chess.*;

public class BoardRenderer {

    private static final String EM = "\u2003";

    public void drawBoard(boolean blackPerspective) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        System.out.println();
        printColumnLabels(blackPerspective);

        for (int i = 0; i < 8; i++) {
            int row = blackPerspective ? i + 1 : 8 - i;

            printBorderLabel(String.valueOf(row));

            for (int j = 0; j < 8; j++) {
                int col = blackPerspective ? 8 - j : j + 1;

                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_BROWN);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_BROWN);
                }

                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                if (piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    } else {
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
                    }

                    System.out.print(getPieceSymbol(piece));
                } else {
                    System.out.print(EscapeSequences.EMPTY);
                }

                System.out.print(EscapeSequences.RESET_TEXT_COLOR);
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            printBorderLabel(String.valueOf(row));
            System.out.println();
        }

        printColumnLabels(blackPerspective);
        System.out.println();
    }

    private String label(String text) {
        return " " + text + EM;
    }

    private void printBorderLabel(String text) {
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(label(text));
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }

    private void printColumnLabels(boolean blackPerspective) {
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_BLUE);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print(EscapeSequences.EMPTY);

        for (int i = 0; i < 8; i++) {
            char col = blackPerspective ? (char) ('h' - i) : (char) ('a' + i);
            System.out.print(label(String.valueOf(col)));
        }

        System.out.print(EscapeSequences.EMPTY);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.println();
    }

    private String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> EscapeSequences.BLACK_KING;
            case QUEEN -> EscapeSequences.BLACK_QUEEN;
            case ROOK -> EscapeSequences.BLACK_ROOK;
            case BISHOP -> EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
            case PAWN -> EscapeSequences.BLACK_PAWN;
        };
    }
}

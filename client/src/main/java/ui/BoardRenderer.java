
package ui;

import chess.*;

public class BoardRenderer {

    private static final String EM = "\u2003";

    public void drawBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        System.out.println();
        printColumnLabels();

        for (int row = 8; row >= 1; row--) {
            System.out.print(label(String.valueOf(row)));

            for (int col = 1; col <= 8; col++) {
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                }

                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                if (piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                    } else {
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
                    }

                    System.out.print(getPieceSymbol(piece));
                } else {
                    System.out.print(EscapeSequences.EMPTY);
                }

                System.out.print(EscapeSequences.RESET_TEXT_COLOR);
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(label(String.valueOf(row)));
        }

        printColumnLabels();
        System.out.println();
    }

    private String label(String text) {
        return " " + text + EM;
    }

    private void printColumnLabels() {
        System.out.print(EscapeSequences.EMPTY);

        for (char col = 'a'; col <= 'h'; col++) {
            System.out.print(label(String.valueOf(col)));
        }

        System.out.print(EscapeSequences.EMPTY);
        System.out.println();
    }

    private String getPieceSymbol(ChessPiece piece) {
        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case KING -> white ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> white ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> white ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP -> white ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> white ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> white ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}

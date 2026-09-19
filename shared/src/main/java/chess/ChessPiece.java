package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public static boolean targetMovement (ChessBoard board, ChessPosition myPosition, List<ChessMove> validMoves, int newTargetRow, int newTargetCol) {
        ChessPosition newTargetPos = new ChessPosition(newTargetRow, newTargetCol);
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece targetPiece = board.getPiece(newTargetPos);

        if (targetPiece == null) {
            validMoves.add(new ChessMove(myPosition, newTargetPos, null));
            return false;
        }
        else if (targetPiece.getTeamColor() != piece.getTeamColor()) {
            validMoves.add(new ChessMove(myPosition, newTargetPos, null));
            return true;
        }
        else {
            return true;
        }
    }

    public static void bishopMovement(ChessBoard board, ChessPosition myPosition, List<ChessMove> validMoves, int row, int col, int rowDir, int colDir) {
        for (int i = 1; i < 8; i++) {
            int newTargetRow = row + (i * rowDir);
            int newTargetCol = col + (i * colDir);

            if (newTargetRow < 1 || newTargetRow > 8 || newTargetCol < 1 || newTargetCol > 8) {
                break;
            }

            if (targetMovement(board, myPosition, validMoves, newTargetRow, newTargetCol)) {
                break;
            }
        }
    }

    public static void kingMovement(ChessBoard board, ChessPosition myPosition, List<ChessMove> validMoves, int row, int col, int rowDir, int colDir) {
        int newTargetRow = row + (rowDir);
        int newTargetCol = col + (colDir);

        if (newTargetRow < 1 || newTargetRow > 8 || newTargetCol < 1 || newTargetCol > 8) {
            return;
        }

        targetMovement(board, myPosition, validMoves, newTargetRow, newTargetCol);
    }

    public static void knightMovement(ChessBoard board, ChessPosition myPosition, List<ChessMove> validMoves, int row, int col, int rowDir, int colDir) {
        for (int i = 1; i < 8; i++) {
            int newTargetRow = row + (i * rowDir);
            int newTargetCol = col + (i * colDir);

            if (newTargetRow < 1 || newTargetRow > 8 || newTargetCol < 1 || newTargetCol > 8) {
                break;
            }

            if (targetMovement(board, myPosition, validMoves, newTargetRow, newTargetCol)) {
                break;
            }
        }
    }

    public static void pawnMovement(ChessBoard board, ChessPosition myPosition, List<ChessMove> validMoves, int row, int col, int rowDir, int colDir) {
        for (int i = 1; i < 8; i++) {
            int newTargetRow = row + (i * rowDir);
            int newTargetCol = col + (i * colDir);

            if (newTargetRow < 1 || newTargetRow > 8 || newTargetCol < 1 || newTargetCol > 8) {
                break;
            }

            if (targetMovement(board, myPosition, validMoves, newTargetRow, newTargetCol)) {
                break;
            }
        }
    }

    public static void queenMovement(ChessBoard board, ChessPosition myPosition, List<ChessMove> validMoves, int row, int col, int rowDir, int colDir) {
        for (int i = 1; i < 8; i++) {
            int newTargetRow = row + (i * rowDir);
            int newTargetCol = col + (i * colDir);

            if (newTargetRow < 1 || newTargetRow > 8 || newTargetCol < 1 || newTargetCol > 8) {
                break;
            }

            if (targetMovement(board, myPosition, validMoves, newTargetRow, newTargetCol)) {
                break;
            }
        }
    }

    public static void rookMovement(ChessBoard board, ChessPosition myPosition, List<ChessMove> validMoves, int row, int col, int rowDir, int colDir) {
        for (int i = 1; i < 8; i++) {
            int newTargetRow = row + (i * rowDir);
            int newTargetCol = col + (i * colDir);

            if (newTargetRow < 1 || newTargetRow > 8 || newTargetCol < 1 || newTargetCol > 8) {
                break;
            }

            if (targetMovement(board, myPosition, validMoves, newTargetRow, newTargetCol)) {
                break;
            }
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        List<ChessMove> validMoves = new ArrayList<>();

        int[][] diagonalDirectionsArray = {
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };
        int[][] straightDirectionsArray = {
                {0, 1},
                {1, 0},
                {-1, 0},
                {0, -1}
        };

        if (piece.getPieceType() == null) {
            return validMoves;
        }

        if (piece.getPieceType() == PieceType.BISHOP) {
            for (int[] dir : diagonalDirectionsArray) {
                int rowDir = dir[0];
                int colDir = dir[1];
                bishopMovement(board, myPosition, validMoves, row, col, rowDir, colDir);
            }
        }
        else if (piece.getPieceType() == PieceType.KING) {
            for (int[] dir : diagonalDirectionsArray) {
                int rowDir = dir[0];
                int colDir = dir[1];
                kingMovement(board, myPosition, validMoves, row, col, rowDir, colDir);
            }
            for (int[] dir : straightDirectionsArray) {
                int rowDir = dir[0];
                int colDir = dir[1];
                kingMovement(board, myPosition, validMoves, row, col, rowDir, colDir);
            }
        }
        else if (piece.getPieceType() == PieceType.KNIGHT) {
            for (int[] dir : diagonalDirectionsArray) {
                int rowDir = dir[0];
                int colDir = dir[1];
                knightMovement(board, myPosition, validMoves, row, col, rowDir, colDir);
            }
        }
        else if (piece.getPieceType() == PieceType.PAWN) {
            for (int[] dir : diagonalDirectionsArray) {
                int rowDir = dir[0];
                int colDir = dir[1];
                pawnMovement(board, myPosition, validMoves, row, col, rowDir, colDir);
            }
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            for (int[] dir : diagonalDirectionsArray) {
                int rowDir = dir[0];
                int colDir = dir[1];
                queenMovement(board, myPosition, validMoves, row, col, rowDir, colDir);
            }
            for (int[] dir : straightDirectionsArray) {
                int rowDir = dir[0];
                int colDir = dir[1];
                queenMovement(board, myPosition, validMoves, row, col, rowDir, colDir);
            }
        }
        else if (piece.getPieceType() == PieceType.ROOK) {
            for (int[] dir : straightDirectionsArray) {
                int rowDir = dir[0];
                int colDir = dir[1];
                rookMovement(board, myPosition, validMoves, row, col, rowDir, colDir);
            }
        }


        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}

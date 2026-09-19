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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        int rowVal = myPosition.getRow();
        int colVal = myPosition.getColumn();
        List<ChessMove> validMoves = new ArrayList<>();

        if (piece.getPieceType() == PieceType.BISHOP) {

            for (int i = 1; i < 8; i++) {
                if (rowVal + i <= 8 && colVal + i <= 8) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(rowVal + i, colVal + i), null));
                }
                if (rowVal + i <= 8 && colVal - i >= 1) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(rowVal + i, colVal - i), null));
                }
                if (rowVal - i >= 1 && colVal + i <= 8) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(rowVal - i, colVal + i), null));
                }
                if (rowVal - i >= 1 && colVal - i >= 1) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(rowVal - i, colVal - i), null));
                }
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

package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeamTurn;
    private ChessBoard board;
    private ChessMove lastMove;
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteLeftRookMoved = false;
    private boolean whiteRightRookMoved = false;
    private boolean blackLeftRookMoved = false;
    private boolean blackRightRookMoved = false;

    public ChessGame() {
        this.currentTeamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentTeamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private void getEnPassantMove(ChessPosition startPosition, Collection<ChessMove> possibleMoves) {
        ChessPiece pawn = board.getPiece(startPosition);

        if (lastMove == null) {
            return;
        }

        ChessPosition lastStart = lastMove.getStartPosition();
        ChessPosition lastEnd = lastMove.getEndPosition();
        ChessPiece lastPiece = board.getPiece(lastEnd);

        if (lastPiece.getPieceType() != ChessPiece.PieceType.PAWN || lastPiece.getTeamColor() == pawn.getTeamColor()) {
            return;
        }

        if (Math.abs(lastStart.getRow() - lastEnd.getRow()) != 2) {
            return;
        }

        if (lastEnd.getRow() != startPosition.getRow() || Math.abs(lastEnd.getColumn() - startPosition.getColumn()) != 1) {
            return;
        }

        int direction = pawn.getTeamColor() == TeamColor.WHITE ? 1 : -1;
        ChessPosition endPosition = new ChessPosition(startPosition.getRow() + direction, lastEnd.getColumn());
        ChessMove enPassantMove = new ChessMove(startPosition, endPosition, null);
        possibleMoves.add(enPassantMove);
    }

    private boolean isSquareAttacked(ChessPosition position, TeamColor attackingTeam) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition piecePosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(piecePosition);

                if (piece == null || piece.getTeamColor() != attackingTeam) {
                    continue;
                }

                if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    int direction = attackingTeam == TeamColor.WHITE ? 1 : -1;

                    if (position.getRow() == row + direction && Math.abs(position.getColumn() - col) == 1) {
                        return true;
                    }

                    continue;
                }

                Collection<ChessMove> moves = piece.pieceMoves(board, piecePosition);

                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(position)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void getCastlingMoves(ChessPosition startPosition, Collection<ChessMove> possibleMoves) {
        ChessPiece king = board.getPiece(startPosition);
        TeamColor team = king.getTeamColor();
        TeamColor enemyTeam = team == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        int row = team == TeamColor.WHITE ? 1 : 8;

        if ((team == TeamColor.WHITE && whiteKingMoved) || (team == TeamColor.BLACK && blackKingMoved)) {
            return;
        }

        if (isSquareAttacked(startPosition, enemyTeam)) {
            return;
        }

        boolean rightRookMoved = team == TeamColor.WHITE ? whiteRightRookMoved : blackRightRookMoved;

        if (!rightRookMoved && board.getPiece(new ChessPosition(row, 6)) == null && board.getPiece(new ChessPosition(row, 7)) == null && !isSquareAttacked(new ChessPosition(row, 6), enemyTeam) && !isSquareAttacked(new ChessPosition(row, 7), enemyTeam)) {
            possibleMoves.add(new ChessMove(startPosition, new ChessPosition(row, 7), null));
        }

        boolean leftRookMoved = team == TeamColor.WHITE ? whiteLeftRookMoved : blackLeftRookMoved;

        if (!leftRookMoved && board.getPiece(new ChessPosition(row, 2)) == null && board.getPiece(new ChessPosition(row, 3)) == null && board.getPiece(new ChessPosition(row, 4)) == null && !isSquareAttacked(new ChessPosition(row, 4), enemyTeam) && !isSquareAttacked(new ChessPosition(row, 3), enemyTeam)) {
            possibleMoves.add(new ChessMove(startPosition, new ChessPosition(row, 3), null));
        }
    }

    private void updateCastlingCheck(ChessPiece piece, ChessPosition startPosition, ChessPosition endPosition) {
        if (endPosition.equals(new ChessPosition(1, 5)) || startPosition.equals(new ChessPosition(1, 5))) {
            whiteKingMoved = true;
        }
        else if (endPosition.equals(new ChessPosition(1, 1)) || startPosition.equals(new ChessPosition(1, 1))) {
            whiteLeftRookMoved = true;
        }
        else if (endPosition.equals(new ChessPosition(1, 8)) || startPosition.equals(new ChessPosition(1, 8))) {
            whiteRightRookMoved = true;
        }
        else if (endPosition.equals(new ChessPosition(8, 5)) || startPosition.equals(new ChessPosition(8, 5))) {
            blackKingMoved = true;
        }
        else if (endPosition.equals(new ChessPosition(8, 1)) || startPosition.equals(new ChessPosition(8, 1))) {
            blackLeftRookMoved = true;
        }
        else if (endPosition.equals(new ChessPosition(8, 8)) || startPosition.equals(new ChessPosition(8, 8))) {
            blackRightRookMoved = true;
        }
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = new ArrayList<>(piece.pieceMoves(board, startPosition));

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            getEnPassantMove(startPosition, possibleMoves);
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            getCastlingMoves(startPosition, possibleMoves);
        }

        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece capturedPiece = board.getPiece(endPosition);
            ChessPosition enPassantPosition = null;
            ChessPiece enPassantPiece = null;

            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && startPosition.getColumn() != endPosition.getColumn() && capturedPiece == null) {
                enPassantPosition = new ChessPosition(startPosition.getRow(), endPosition.getColumn());
                enPassantPiece = board.getPiece(enPassantPosition);
                board.addPiece(enPassantPosition, null);
            }

            board.addPiece(startPosition, null);
            board.addPiece(endPosition, piece);

            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }

            board.addPiece(startPosition, piece);
            board.addPiece(endPosition, capturedPiece);

            if (enPassantPosition != null) {
                board.addPiece(enPassantPosition, enPassantPiece);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null || piece.getTeamColor() != currentTeamTurn) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> moves = validMoves(startPosition);

        if (moves == null  || !moves.contains(move)) {
            throw new InvalidMoveException();
        }

        ChessPiece movedPiece = piece;

        if (move.getPromotionPiece() != null) {
            if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
                throw new InvalidMoveException();
            }

            movedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && startPosition.getColumn() != endPosition.getColumn() && board.getPiece(endPosition) == null) {
            ChessPosition capturedPawnPosition = new ChessPosition(startPosition.getRow(), endPosition.getColumn());
            board.addPiece(capturedPawnPosition, null);
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(startPosition.getColumn() - endPosition.getColumn()) == 2) {
            int row = startPosition.getRow();

            if (endPosition.getColumn() == 7) {
                ChessPosition rookStart = new ChessPosition(row, 8);
                ChessPosition rookEnd = new ChessPosition(row, 6);
                ChessPiece rook = board.getPiece(rookStart);
                board.addPiece(rookStart, null);
                board.addPiece(rookEnd, rook);
            }
            else {
                ChessPosition rookStart = new ChessPosition(row, 1);
                ChessPosition rookEnd = new ChessPosition(row, 4);
                ChessPiece rook = board.getPiece(rookStart);
                board.addPiece(rookStart, null);
                board.addPiece(rookEnd, rook);
            }
        }

        updateCastlingCheck(piece, startPosition, endPosition);
        board.addPiece(endPosition, movedPiece);
        board.addPiece(startPosition, null);
        lastMove = move;
        currentTeamTurn = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = position;
                    break;
                }
            }
            if (kingPosition != null) {
                break;
            }
        }

        TeamColor enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK: TeamColor.WHITE;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == enemyColor) {
                    Collection<ChessMove> enemyMoves = piece.pieceMoves(board, position);

                    for (ChessMove move : enemyMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);

                    if (moves != null  && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);

                    if (moves != null  && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeamTurn == chessGame.currentTeamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeamTurn, board);
    }
}

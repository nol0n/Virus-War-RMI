package rmi.game;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final int BOARD_SIZE = 10;

    private final CellState[][] board;
    private final boolean isXTurn;
    private final int remainingMoves;
    private final GameStatus status;
    private final String winner;

    public GameState(CellState[][] board, boolean isXTurn, int remainingMoves,
                     GameStatus status, String winner) {
        this.board = copyBoard(board);
        this.isXTurn = isXTurn;
        this.remainingMoves = remainingMoves;
        this.status = status;
        this.winner = winner;
    }

    // Безопасное копирование доски
    private CellState[][] copyBoard(CellState[][] original) {
        CellState[][] copy = new CellState[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, BOARD_SIZE);
        }
        return copy;
    }

    // Геттеры
    public CellState[][] getBoard() {
        return copyBoard(board);
    }

    public boolean isXTurn() {
        return isXTurn;
    }

    public int getRemainingMoves() {
        return remainingMoves;
    }

    public GameStatus getStatus() {
        return status;
    }

    public String getWinner() {
        return winner;
    }
}

package soap.game;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class GameState implements Serializable {
    private static final int SIZE = 10;

    private CellState[][] board;
    private boolean isXTurn;
    private int remainingMoves;
    private GameStatus status;
    private String winner;

    public GameState() {
        this.board = new CellState[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                this.board[i][j] = CellState.EMPTY;
            }
        }
        this.isXTurn = true;
        this.remainingMoves = 3;
        this.status = GameStatus.WAITING_PLAYERS;
        this.winner = null;
    }

    public GameState(CellState[][] board, boolean isXTurn, int remainingMoves,
                     GameStatus status, String winner) {
        this.board = copyBoard(board);
        this.isXTurn = isXTurn;
        this.remainingMoves = remainingMoves;
        this.status = status;
        this.winner = winner;
    }

    private CellState[][] copyBoard(CellState[][] original) {
        CellState[][] copy = new CellState[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    public void setBoard(CellState[][] board) {
        this.board = copyBoard(board);
    }

    public void setXTurn(boolean xTurn) {
        isXTurn = xTurn;
    }

    public void setRemainingMoves(int remainingMoves) {
        this.remainingMoves = remainingMoves;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

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

package rmi.game;

import rmi.net.RemoteVirusGame;
import java.rmi.RemoteException;

public class ServerGameModel implements RemoteVirusGame {
    private static final int SIZE = 10;
    private final CellState[][] board = new CellState[SIZE][SIZE];
    private boolean isXTurn = true;
    private GameStatus status = GameStatus.WAITING_PLAYERS;
    private int remainingMoves = 3;
    private String winner = null;

    private int xCount = 0;
    private int oCount = 0;
    private boolean xFirstMove = true;
    private boolean oFirstMove = true;

    private int connectedPlayers = 0;
    private boolean[] playerConnected = new boolean[2];
    private boolean[] playerReady = new boolean[2];

    public ServerGameModel() {
        reset();
    }

    private void reset() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = CellState.EMPTY;
            }
        }
        isXTurn = true;
        remainingMoves = 3;
        status = GameStatus.WAITING_PLAYERS;
        oCount = 0;
        xCount = 0;
        xFirstMove = true;
        oFirstMove = true;
        winner = null;
    }

    @Override
    public synchronized boolean makeMove(int playerId, int row, int col) throws RemoteException {
        if (status != GameStatus.PLAYING || !isPlayerTurn(playerId)) {
            return false;
        }

        if (!isValidMove(row, col)) {
            return false;
        }

        if (board[row][col] == CellState.EMPTY) {
            board[row][col] = isXTurn ? CellState.X_ALIVE : CellState.O_ALIVE;
            if (isXTurn) {
                xCount++;
                xFirstMove = false;
            } else {
                oCount++;
                oFirstMove = false;
            }
        } else if (isKillable(row, col)) {
            board[row][col] = isXTurn ? CellState.O_DEAD : CellState.X_DEAD;
            if (isXTurn) {
                oCount--;
            } else {
                xCount--;
            }
        } else {
            return false;
        }

        remainingMoves--;

        endConditions();

        if (remainingMoves == 0) {
            isXTurn = !isXTurn;
            remainingMoves = 3;
        }

        return true;
    }

    private boolean isValidMove(int row, int col) {
        if (isFirstMove()) {
            if (isXTurn) {
                return row == 0 && col == 0;
            } else {
                return row == SIZE-1 && col == SIZE-1;
            }
        }
        return isReachable(row, col);
    }

    private boolean isFirstMove() {
        if (isXTurn && xFirstMove && xCount == 0) {
            return true;
        } else if (!isXTurn && oFirstMove && oCount == 0) {
            return true;
        }
        return false;
    }

    private boolean isReachable(int row, int col) {
        if (hasAdjacentAlly(row, col)) {
            return true;
        }
        boolean[][] visited = new boolean[SIZE][SIZE];
        return isReachableThroughDead(row, col, visited);
    }

    private boolean hasAdjacentAlly(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE) {
                    if (board[newRow][newCol] == (isXTurn ? CellState.X_ALIVE : CellState.O_ALIVE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isReachableThroughDead(int row, int col, boolean[][] visited) {
        if (hasAdjacentAlly(row, col)) {
            return true;
        }

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE
                        && !visited[newRow][newCol]) {
                    CellState deadEnemy = isXTurn ? CellState.O_DEAD : CellState.X_DEAD;
                    if (board[newRow][newCol] == deadEnemy) {
                        visited[newRow][newCol] = true;
                        if (isReachableThroughDead(newRow, newCol, visited)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isKillable(int row, int col) {
        CellState target = board[row][col];
        return (isXTurn && target == CellState.O_ALIVE) ||
                (!isXTurn && target == CellState.X_ALIVE);
    }

    private boolean hasAvailableMoves() {
        if (isFirstMove()) {
            return true;
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((board[row][col] == CellState.EMPTY ||
                        board[row][col] == (isXTurn ? CellState.O_ALIVE : CellState.X_ALIVE))
                        && isReachable(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean enemyIsAlive() {
        if (isXTurn && (oFirstMove || oCount != 0)) {
            return true;
        } else if (!isXTurn && (xFirstMove || xCount != 0)) {
            return true;
        }
        return false;
    }

    private void endConditions() {
        if (!hasAvailableMoves()) {
            status = GameStatus.FINISHED;
            winner = !isXTurn ? "X" : "O";
            return;
        }

        isXTurn = !isXTurn;
        if (!hasAvailableMoves()) {
            status = GameStatus.FINISHED;
            isXTurn = !isXTurn;
            winner = isXTurn ? "X" : "O";
            return;
        }
        isXTurn = !isXTurn;

        if (!enemyIsAlive()) {
            status = GameStatus.FINISHED;
            winner = isXTurn ? "X" : "O";
        }
    }

    private boolean isPlayerTurn(int playerId) {
        return (playerId == 1 && isXTurn) || (playerId == 2 && !isXTurn);
    }

    @Override
    public synchronized void confirmReady(int playerId) throws RemoteException {
        if (status == GameStatus.READY || status == GameStatus.FINISHED) {
            playerReady[playerId - 1] = true;

            if (playerReady[0] && playerReady[1]) {
                reset();
                status = GameStatus.PLAYING;
                playerReady[0] = false;
                playerReady[1] = false;
            }
        }
    }

    @Override
    public synchronized int registerPlayer() throws RemoteException {
        for (int i = 0; i < 2; i++) {
            if (!playerConnected[i]) {
                playerConnected[i] = true;
                connectedPlayers++;
                if (connectedPlayers == 2) {
                    status = GameStatus.READY;
                }
                return i + 1;
            }
        }

        return -1;
    }

    @Override
    public synchronized void surrender(int playerId) throws RemoteException {
        if (status == GameStatus.PLAYING) {
            status = GameStatus.FINISHED;
            winner = playerId == 1 ? "O" : "X";
        }
    }

    @Override
    public synchronized void disconnectPlayer(int playerId) throws RemoteException {
        if (playerConnected[playerId - 1]) {
            playerConnected[playerId - 1] = false;
            connectedPlayers--;
            status = GameStatus.WAITING_PLAYERS;
            reset();
        }
    }

    @Override
    public GameState getGameState() throws RemoteException {
        return new GameState(board, isXTurn, remainingMoves, status, winner);
    }
}

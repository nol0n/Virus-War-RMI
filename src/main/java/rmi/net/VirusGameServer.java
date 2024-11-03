package rmi.net;

import rmi.game.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class VirusGameServer implements RemoteVirusGame {
    public static final String name = "VirusServer";
    public static final int port = 8080;

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

    @Override
    public synchronized void confirmReady(int playerId) throws RemoteException {
        if (status == GameStatus.READY && isValidPlayer(playerId)) {
            playerReady[playerId - 1] = true;

            if (playerReady[0] && playerReady[1]) {
                reset();
                status = GameStatus.PLAYING;
                playerReady[0] = false;
                playerReady[1] = false;
            }
        }
    }

    public VirusGameServer() {
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
        // Проверки валидности хода
        if (status != GameStatus.PLAYING ||
                !isValidPlayer(playerId) ||
                !isPlayerTurn(playerId)) {
            return false;
        }

        if (!isValidMove(row, col)) {
            return false;
        }

        // Выполнение хода
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
        // проверка доступных ходов у себя
        if (!hasAvailableMoves()) {
            status = GameStatus.FINISHED;
            winner = !isXTurn ? "X" : "O";
            return;
        }

        // проверка доступных ходов у оппонента
        isXTurn = !isXTurn;
        if (!hasAvailableMoves()) {
            status = GameStatus.FINISHED;
            isXTurn = !isXTurn;
            winner = isXTurn ? "X" : "O";
            return;
        }
        isXTurn = !isXTurn;

        // проверка кол-ва занятых оппонентом клеток
        if (!enemyIsAlive()) {
            status = GameStatus.FINISHED;
            winner = isXTurn ? "X" : "O";
        }
    }

    private boolean isValidPlayer(int playerId) {
        return playerId == 1 || playerId == 2;
    }

    private boolean isPlayerTurn(int playerId) {
        return (playerId == 1 && isXTurn) || (playerId == 2 && !isXTurn);
    }

    @Override
    public synchronized int registerPlayer() throws RemoteException {
        // Проверяем, есть ли отключившиеся игроки
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

        // Если нет отключившихся, проверяем есть ли свободные места
        if (connectedPlayers >= 2) {
            return -1;
        }

        int playerId = connectedPlayers + 1;
        playerConnected[connectedPlayers] = true;
        connectedPlayers++;

        if (connectedPlayers == 2) {
            status = GameStatus.READY;
        }

        return playerId;
    }

    @Override
    public synchronized void startGame() throws RemoteException {
        if (status == GameStatus.READY) {
            status = GameStatus.PLAYING;
        }
    }

    @Override
    public boolean isGameReady() throws RemoteException {
        return status == GameStatus.PLAYING;
    }

    @Override
    public synchronized void surrender(int playerId) throws RemoteException {
        if (status == GameStatus.PLAYING && isValidPlayer(playerId)) {
            status = GameStatus.FINISHED;
            winner = playerId == 1 ? "O" : "X";
        }
    }

    @Override
    public GameState getGameState() throws RemoteException {
        return new GameState(board, isXTurn, remainingMoves, status, winner);
    }

    @Override
    public synchronized void resetGame(int playerId) throws RemoteException {
        playerReady[0] = false;
        playerReady[1] = false;
        if (connectedPlayers == 2) {
            status = GameStatus.READY;
            playerReady[playerId - 1] = true;
        }
    }

    @Override
    public synchronized void disconnectPlayer(int playerId) throws RemoteException {
        if (isValidPlayer(playerId) && playerConnected[playerId - 1]) {
            playerConnected[playerId - 1] = false;
            connectedPlayers--;
            status = GameStatus.WAITING_PLAYERS;
            reset();
        }
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            VirusGameServer server = new VirusGameServer();
            RemoteVirusGame proxy = (RemoteVirusGame)
                    UnicastRemoteObject.exportObject(server, port);
            registry.rebind(name, proxy);
            System.out.println("Virus Game Server is running...");
        } catch (RemoteException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

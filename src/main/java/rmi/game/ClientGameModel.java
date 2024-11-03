package rmi.game;

import rmi.net.RemoteVirusGame;
import rmi.game.GameState;
import java.rmi.RemoteException;

public class ClientGameModel {
    private final RemoteVirusGame server;
    private final int playerId;
    private GameState currentState;
    private boolean victoryMessageShown = false;
    private GameStatus lastStatus = null;

    public ClientGameModel(RemoteVirusGame server, int playerId) {
        this.server = server;
        this.playerId = playerId;
        updateState();
    }

    public void startGame() {
        try {
            server.startGame();
            updateState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void confirmReady() {
        try {
            server.confirmReady(playerId);
            updateState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void resetGame() {
        try {
            server.resetGame(playerId);
            // НЕ сбрасываем тут victoryMessageShown
            updateState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean makeMove(int row, int col) {
        try {
            boolean success = server.makeMove(playerId, row, col);
            if (success) {
                updateState();
            }
            return success;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void surrender() {
        try {
            server.surrender(playerId);
            updateState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            server.disconnectPlayer(playerId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateState() {
        try {
            GameState newState = server.getGameState();
            GameStatus newStatus = newState.getStatus();

            // Сброс флага только при изменении статуса на READY
            if (newStatus == GameStatus.READY && lastStatus != GameStatus.READY) {
                victoryMessageShown = false;
            }

            lastStatus = newStatus;
            currentState = newState;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // Геттеры для UI
    public CellState[][] getBoard() {
        return currentState.getBoard();
    }

    public boolean isMyTurn() {
        return (playerId == 1 && currentState.isXTurn()) ||
                (playerId == 2 && !currentState.isXTurn());
    }

    public boolean isGameReady() {
        try {
            return server.isGameReady();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public GameStatus getGameStatus() {
        return currentState.getStatus();
    }

    public int getRemainingMoves() {
        return currentState.getRemainingMoves();
    }

    public String getWinner() {
        return currentState.getWinner();
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isVictoryMessageShown() {
        return victoryMessageShown;
    }

    public void setVictoryMessageShown(boolean shown) {
        victoryMessageShown = shown;
    }
}

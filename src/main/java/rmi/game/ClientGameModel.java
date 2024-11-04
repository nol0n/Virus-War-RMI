package rmi.game;

import rmi.net.RemoteVirusGame;
import java.rmi.RemoteException;

public class ClientGameModel {
    private final RemoteVirusGame server;
    private final int playerId;
    private GameState currentState;
    private boolean victoryMessageShown = false;

    public ClientGameModel(RemoteVirusGame server, int playerId) {
        this.server = server;
        this.playerId = playerId;
        updateState();
    }

    public void confirmReady() {
        try {
            server.confirmReady(playerId);
            updateState();
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void surrender() {
        try {
            server.surrender(playerId);
            updateState();
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }

    public void disconnect() {
        try {
            server.disconnectPlayer(playerId);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateState() {
        try {
            currentState = server.getGameState();;
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }

    public CellState[][] getBoard() {
        return currentState.getBoard();
    }

    public boolean isMyTurn() {
        return (playerId == 1 && currentState.isXTurn()) ||
                (playerId == 2 && !currentState.isXTurn());
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

package soap.game;

import soap.net.VirusGameService;

public class ClientGameModel {
    private final VirusGameService server;
    private final int playerId;
    private GameState currentState;
    private boolean victoryMessageShown = false;

    public ClientGameModel(VirusGameService server, int playerId) {
        this.server = server;
        this.playerId = playerId;
        updateState();
    }

    public void confirmReady() {
        server.confirmReady(playerId);
        updateState();
    }

    public boolean makeMove(int row, int col) {
        boolean success = server.makeMove(playerId, row, col);
        if (success) {
            updateState();
        }
        return success;
    }

    public void surrender() {
        server.surrender(playerId);
        updateState();
    }

    public void disconnect() {
        server.disconnectPlayer(playerId);
    }

    public void updateState() {
        currentState = server.getGameState();;
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

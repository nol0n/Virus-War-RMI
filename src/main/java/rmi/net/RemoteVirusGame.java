package rmi.net;

import rmi.game.GameState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteVirusGame extends Remote {
    boolean makeMove(int playerId, int row, int col) throws RemoteException;

    int registerPlayer() throws RemoteException;

    void confirmReady(int playerId) throws RemoteException;

    void surrender(int playerId) throws RemoteException;

    void disconnectPlayer(int playerId) throws RemoteException;

    GameState getGameState() throws RemoteException;
}

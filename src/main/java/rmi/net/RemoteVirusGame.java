package rmi.net;

import rmi.game.GameState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteVirusGame extends Remote {
    // Регистрация игрока (возвращает ID игрока: 1 - X, 2 - O)
    int registerPlayer() throws RemoteException;
    void startGame() throws RemoteException;
    void confirmReady(int playerId) throws RemoteException;

    // Проверка готовности игры (два игрока подключились)
    boolean isGameReady() throws RemoteException;

    // Основные игровые методы
    boolean makeMove(int playerId, int row, int col) throws RemoteException;
    void surrender(int playerId) throws RemoteException;

    // Получение состояния игры
    GameState getGameState() throws RemoteException;

    // Сброс игры (например, для новой партии)
    void resetGame(int playerId) throws RemoteException;

    // Отключение игрока
    void disconnectPlayer(int playerId) throws RemoteException;
}

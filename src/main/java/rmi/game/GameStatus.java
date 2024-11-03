package rmi.game;

public enum GameStatus {
    WAITING_PLAYERS,    // Ожидание игроков
    READY,             // Игроки подключены, ожидание старта
    PLAYING,           // Игра идет
    FINISHED           // Игра окончена
}

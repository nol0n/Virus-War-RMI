package rmi.net;

import rmi.game.ClientGameModel;
import rmi.game.GameFrame;
import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class VirusGameClient {
    public static final int port = 8080;
    static final String host = "localhost";
    static final String name = "VirusServer";

    private ClientGameModel gameModel;
    private GameFrame gameFrame;

    public void start() {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            RemoteVirusGame server = (RemoteVirusGame) registry.lookup(name);

            int playerId = server.registerPlayer();
            if (playerId == -1) {
                JOptionPane.showMessageDialog(null, "Игра уже заполнена");
                return;
            }

            gameModel = new ClientGameModel(server, playerId);
            SwingUtilities.invokeLater(() -> gameFrame = new GameFrame(gameModel));

            startUpdateThread();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка подключения к серверу: " + e.getMessage());
        }
    }

    private void startUpdateThread() {
        Thread updateThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                SwingUtilities.invokeLater(() -> gameFrame.refresh());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    public static void main(String[] args) {
        new VirusGameClient().start();
    }
}

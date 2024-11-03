package rmi.net;

import rmi.game.ClientGameModel;
import rmi.game.GamePanel;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class VirusGameClient {
    public static final int port = 8080;
    static final String host = "localhost";
    static final String name = "VirusServer";

    private ClientGameModel gameModel;
    private GamePanel gamePanel;
    private JFrame frame;

    public void start() {
        try {
            // Подключение к серверу
            Registry registry = LocateRegistry.getRegistry(host, port);
            RemoteVirusGame server = (RemoteVirusGame) registry.lookup(name);

            // Регистрация игрока
            int playerId = server.registerPlayer();
            if (playerId == -1) {
                JOptionPane.showMessageDialog(null, "Игра уже заполнена");
                return;
            }

            // Инициализация модели и UI
            gameModel = new ClientGameModel(server, playerId);
            SwingUtilities.invokeLater(this::createAndShowGUI);

            // Запуск потока обновления состояния
            startUpdateThread();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка подключения к серверу: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Война вирусов - Игрок " +
                (gameModel.getPlayerId() == 1 ? "X" : "O"));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameModel.disconnect();
                frame.dispose();
            }
        });

        gamePanel = new GamePanel(gameModel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startUpdateThread() {
        Thread updateThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                if (gameModel != null) {
                    gameModel.updateState();
                    if (gamePanel != null) {
                        SwingUtilities.invokeLater(() -> gamePanel.refresh());
                    }
                }
                try {
                    Thread.sleep(100);  // Обновление каждые 100мс
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

package rmi.game;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    private final GamePanel gamePanel;
    private final ClientGameModel gameModel;

    public GameFrame(ClientGameModel model) {
        gameModel = model;
        gamePanel = new GamePanel(gameModel);

        setTitle("Война вирусов - Игрок " +
                (gameModel.getPlayerId() == 1 ? "X" : "O"));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameModel.disconnect();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void refresh() {
        if (gamePanel != null) {
            gamePanel.refresh();
        }
    }
}

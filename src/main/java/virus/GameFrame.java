package virus;

import javax.swing.*;

public class GameFrame extends JFrame {
    private final GamePanel gamePanel;
    private final GameModel gameModel;

    public GameFrame() {
        gameModel = new GameModel();
        gamePanel = new GamePanel(gameModel);

        setTitle("Virus War Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

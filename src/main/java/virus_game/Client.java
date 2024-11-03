package virus_game;

import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame());
    }
}

package net;

import virus.GameFrame;
import javax.swing.*;
import java.awt.*;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame());
    }
}

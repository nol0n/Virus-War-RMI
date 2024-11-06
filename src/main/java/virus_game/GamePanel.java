package virus_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
    private static final int CELL_SIZE = 60; // Уменьшил размер для поля 10x10
    private final GameModel model;
    private final JButton controlButton;
    private final JButton surrenderButton;
    private final JPanel gameArea;
    private final JPanel buttonArea;

    public GamePanel(GameModel model) {
        this.model = model;
        setLayout(new BorderLayout());

        gameArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
                drawCells(g);
            }
        };
        gameArea.setPreferredSize(new Dimension(
                GameModel.getSize() * CELL_SIZE,
                GameModel.getSize() * CELL_SIZE
        ));
        gameArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        buttonArea = new JPanel();
        controlButton = new JButton("Начать игру");
        controlButton.addActionListener(e -> handleControlButton());
        buttonArea.add(controlButton);

        surrenderButton = new JButton("Сдаться");
        surrenderButton.setEnabled(false);
        surrenderButton.addActionListener(e -> handleSurrenderButton());
        buttonArea.add(surrenderButton);

        add(gameArea, BorderLayout.CENTER);
        add(buttonArea, BorderLayout.SOUTH);
    }

    private void handleClick(int x, int y) {
        int row = y / CELL_SIZE;
        int col = x / CELL_SIZE;

        if (row < GameModel.getSize() && col < GameModel.getSize()) {
            if (model.makeMove(row, col)) {
                updateStatus();
                gameArea.repaint();
            }
        }
    }

    private void handleControlButton() {
        if (model.isGameIdle()) {
            model.startGame();
            updateStatus();
        } else if (model.isGameEnded()) {
            model.reset();
            controlButton.setText("Начать игру");
        }
        gameArea.repaint();
    }

    private void handleSurrenderButton() {
        model.surrender();
        updateStatus();
        surrenderButton.setEnabled(false);
        gameArea.repaint();
    }

    private void updateStatus() {
        if (model.isGameEnded()) {
            controlButton.setText("Сбросить");
            surrenderButton.setEnabled(false);
            String message = "Победили " + model.getWinner() + "!";
            JOptionPane.showMessageDialog(this, message, "Конец игры",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (model.isGameStarted()) {
            surrenderButton.setEnabled(true);
            String turn = model.isXTurn() ? "X" : "O";
            String moves = String.valueOf(model.getRemainingMoves());
            controlButton.setText("Ход " + turn + " (осталось ходов: " + moves + ")");
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i <= GameModel.getSize(); i++) {
            g.drawLine(i * CELL_SIZE, 0,
                    i * CELL_SIZE, GameModel.getSize() * CELL_SIZE);
            g.drawLine(0, i * CELL_SIZE,
                    GameModel.getSize() * CELL_SIZE, i * CELL_SIZE);
        }
    }

    private void drawCells(Graphics g) {
        for (int row = 0; row < GameModel.getSize(); row++) {
            for (int col = 0; col < GameModel.getSize(); col++) {
                drawCell(g, row, col, model.getCellState(row, col));
            }
        }
    }

    private void drawCell(Graphics g, int row, int col, GameModel.CellState state) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;
        int padding = 10;

        switch (state) {
            case X_ALIVE:
                drawX(g, x, y, Color.BLUE, false);
                break;
            case O_ALIVE:
                drawO(g, x, y, Color.RED, false);
                break;
            case X_DEAD:
                drawX(g, x, y, Color.GRAY, true);
                break;
            case O_DEAD:
                drawO(g, x, y, Color.GRAY, true);
                break;
        }
    }

    private void drawX(Graphics g, int x, int y, Color color, boolean isDead) {
        int padding = 10;
        g.setColor(color);

        g.drawLine(x + padding, y + padding,
                x + CELL_SIZE - padding, y + CELL_SIZE - padding);
        g.drawLine(x + CELL_SIZE - padding, y + padding,
                x + padding, y + CELL_SIZE - padding);

        if (isDead) {
            g.drawOval(x + padding/2, y + padding/2,
                    CELL_SIZE - padding, CELL_SIZE - padding);
        }
    }

    private void drawO(Graphics g, int x, int y, Color color, boolean isDead) {
        int padding = 10;
        g.setColor(color);

        if (isDead) {
            g.fillOval(x + padding, y + padding,
                    CELL_SIZE - 2*padding, CELL_SIZE - 2*padding);
        } else {
            g.drawOval(x + padding, y + padding,
                    CELL_SIZE - 2*padding, CELL_SIZE - 2*padding);
        }
    }
}

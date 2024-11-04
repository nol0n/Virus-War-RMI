package rmi.game;

import virus_game.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
    private static final int CELL_SIZE = 60;
    private final ClientGameModel model;
    private final JButton controlButton;
    private final JButton surrenderButton;
    private final JPanel gameArea;
    private final JPanel buttonArea;

    public GamePanel(ClientGameModel model) {
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
        gameArea.setPreferredSize(new Dimension(10 * CELL_SIZE, 10 * CELL_SIZE));
        gameArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        buttonArea = new JPanel();
        controlButton = new JButton("Ожидание игроков...");
        controlButton.addActionListener(e -> handleControlButton());
        controlButton.setEnabled(false);
        buttonArea.add(controlButton);

        surrenderButton = new JButton("Сдаться");
        surrenderButton.setEnabled(false);
        surrenderButton.addActionListener(e -> handleSurrenderButton());
        buttonArea.add(surrenderButton);

        add(gameArea, BorderLayout.CENTER);
        add(buttonArea, BorderLayout.SOUTH);
    }

    private void handleClick(int x, int y) {
        if (!model.isMyTurn()) {
            return;
        }

        int row = y / CELL_SIZE;
        int col = x / CELL_SIZE;

        if (row < 10 && col < 10) {
            if (model.makeMove(row, col)) {
                refresh();
            }
        }
    }

    private void handleControlButton() {
        GameStatus status = model.getGameStatus();
        if (status == GameStatus.READY || status == GameStatus.FINISHED) {
            model.confirmReady();
            controlButton.setEnabled(false);
            controlButton.setText("Ожидание второго игрока...");
            refresh();
        }
    }

    private void handleSurrenderButton() {
        model.surrender();
        refresh();
    }

    public void refresh() {
        model.updateState();
        switch (model.getGameStatus()) {
            case WAITING_PLAYERS:
                controlButton.setText("Ожидание игроков...");
                controlButton.setEnabled(false);
                surrenderButton.setEnabled(false);
                break;

            case READY:
                if (!controlButton.isEnabled()) {
                    controlButton.setText("Начать игру");
                    controlButton.setEnabled(true);
                }
                surrenderButton.setEnabled(false);
                break;

            case PLAYING:
                if (model.isMyTurn()) {
                    controlButton.setText("Ваш ход (осталось: " + model.getRemainingMoves() + ")");
                } else {
                    controlButton.setText("Ход противника");
                }
                model.setVictoryMessageShown(false);
                controlButton.setEnabled(false);
                surrenderButton.setEnabled(true);
                break;

            case FINISHED:
                if (!model.isVictoryMessageShown()) {
                    model.setVictoryMessageShown(true);
                    String winner = model.getWinner();
                    String message = winner.equals(model.getPlayerId() == 1 ? "X" : "O") ?
                            "Вы победили!" : "Вы проиграли";
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, message, "Конец игры",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                }
                controlButton.setText("Начать новую игру");
                controlButton.setEnabled(true);
                surrenderButton.setEnabled(false);
                break;
        }
        gameArea.repaint();
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 1; i <= GameModel.getSize() - 1; i++) {
            g.drawLine(i * CELL_SIZE, 0,
                    i * CELL_SIZE, GameModel.getSize() * CELL_SIZE);
            g.drawLine(0, i * CELL_SIZE,
                    GameModel.getSize() * CELL_SIZE, i * CELL_SIZE);
        }
    }

    private void drawCells(Graphics g) {
        CellState[][] board = model.getBoard();
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                drawCell(g, row, col, board[row][col]);
            }
        }
    }

    private void drawCell(Graphics g, int row, int col, CellState state) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;

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

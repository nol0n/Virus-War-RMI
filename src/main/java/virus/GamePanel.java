package virus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
    private static final int CELL_SIZE = 100;
    private static final int GRID_SIZE = 3;

    private final GameModel model;
    private final JButton controlButton;
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
        gameArea.setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        gameArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        buttonArea = new JPanel();
        controlButton = new JButton("Start Game");
        controlButton.addActionListener(e -> handleControlButton());
        buttonArea.add(controlButton);

        add(gameArea, BorderLayout.CENTER);
        add(buttonArea, BorderLayout.SOUTH);
    }

    private void handleClick(int x, int y) {
        int row = y / CELL_SIZE;
        int col = x / CELL_SIZE;

        if (row < GRID_SIZE && col < GRID_SIZE) {
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
            controlButton.setText("Start Game");
        }
        gameArea.repaint();
    }

    private void updateStatus() {
        if (model.isGameEnded()) {
            controlButton.setText("Reset Game");
            String message = model.isDraw() ? "Ничья!" :
                    (model.isXTurn() ? "Победили O!" : "Победили X!");
            JOptionPane.showMessageDialog(this, message, "Конец игры",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (model.isGameStarted()) {
            controlButton.setText("Ход " + (model.isXTurn() ? "X" : "O"));
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 1; i < GRID_SIZE; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE);
            g.drawLine(0, i * CELL_SIZE, GRID_SIZE * CELL_SIZE, i * CELL_SIZE);
        }
    }

    private void drawCells(Graphics g) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int state = model.getCellState(row, col);
                if (state != 0) {
                    drawCell(g, row, col, state);
                }
            }
        }
    }

    private void drawCell(Graphics g, int row, int col, int state) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;
        int padding = 20;

        g.setColor(state == 1 ? Color.BLUE : Color.RED);
        if (state == 1) { // X
            g.drawLine(x + padding, y + padding,
                    x + CELL_SIZE - padding, y + CELL_SIZE - padding);
            g.drawLine(x + CELL_SIZE - padding, y + padding,
                    x + padding, y + CELL_SIZE - padding);
        } else { // O
            g.drawOval(x + padding, y + padding,
                    CELL_SIZE - 2 * padding, CELL_SIZE - 2 * padding);
        }
    }
}

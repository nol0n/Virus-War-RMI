package virus;

public class GameModel {
    private final int[][] board = new int[3][3];
    private boolean isXTurn = true;
    private boolean gameStarted = false;
    private boolean gameEnded = false;

    public boolean makeMove(int row, int col) {
        if (!gameStarted || gameEnded || board[row][col] != 0) {
            return false;
        }

        board[row][col] = isXTurn ? 1 : 2;
        isXTurn = !isXTurn;

        if (checkWin() || checkDraw()) {
            gameEnded = true;
        }

        return true;
    }

    public void startGame() {
        gameStarted = true;
        gameEnded = false;
    }

    public void reset() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }
        isXTurn = true;
        gameStarted = false;
        gameEnded = false;
    }

    private boolean checkWin() {
        // Проверка строк и столбцов
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][0] == board[i][2]) return true;
            if (board[0][i] != 0 && board[0][i] == board[1][i] && board[0][i] == board[2][i]) return true;
        }
        // Проверка диагоналей
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[0][0] == board[2][2]) return true;
        return board[0][2] != 0 && board[0][2] == board[1][1] && board[0][2] == board[2][0];
    }

    private boolean checkDraw() {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) return false;
            }
        }
        return true;
    }

    public int getCellState(int row, int col) { return board[row][col]; }
    public boolean isXTurn() { return isXTurn; }
    public boolean isGameStarted() { return gameStarted; }
    public boolean isGameEnded() { return gameEnded; }
    public boolean isDraw() { return gameEnded && !checkWin(); }
}

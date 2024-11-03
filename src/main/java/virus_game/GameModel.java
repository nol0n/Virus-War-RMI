package virus_game;

public class GameModel {
    public enum GameState {
        INITIAL,
        PLAYING,
        FINISHED
    }

    public enum CellState {
        EMPTY,
        X_ALIVE,
        O_ALIVE,
        X_DEAD,
        O_DEAD
    }

    private static final int SIZE = 10;
    private final CellState[][] board = new CellState[SIZE][SIZE];
    private boolean isXTurn = true;
    private GameState gameState = GameState.INITIAL;
    private int remainingMoves = 3;
    private int xCount = 0;
    private int oCount = 0;
    private boolean xFirstMove = true;
    private boolean oFirstMove = true;
    private boolean winner = true;

    public GameModel() {
        reset();
    }

    public void reset() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = CellState.EMPTY;
            }
        }
        isXTurn = true;
        remainingMoves = 3;
        gameState = GameState.INITIAL;
        oCount = 0;
        xCount = 0;
        xFirstMove = true;
        oFirstMove = true;
    }

    public boolean makeMove(int row, int col) {
        if (!isGameStarted()) {
            return false;
        }

        if (!isValidMove(row, col)) {
            return false;
        }

        if (board[row][col] == CellState.EMPTY) {
            board[row][col] = isXTurn ? CellState.X_ALIVE : CellState.O_ALIVE;
            if (isXTurn) {
                xCount++;
                xFirstMove = false;
            } else {
                oCount++;
                oFirstMove = false;
            }
        } else if (isKillable(row, col)) {
            board[row][col] = isXTurn ? CellState.O_DEAD : CellState.X_DEAD;
            if (isXTurn) {
                oCount--;
            } else {
                xCount--;
            }
        } else {
            return false;
        }
        
        remainingMoves--;

        endConditions();

        if (remainingMoves == 0) {
            isXTurn = !isXTurn;
            remainingMoves = 3;
        }

        return true;
    }

    private boolean isValidMove(int row, int col) {
        if (isFirstMove()) {
            if (isXTurn) {
                return row == 0 && col == 0;
            } else {
                return row == SIZE-1 && col == SIZE-1;
            }
        }

        return isReachable(row, col);
    }

    private boolean isFirstMove() {
        if (isXTurn && xFirstMove && xCount == 0) {
            return true;
        } else if (!isXTurn && oFirstMove && oCount == 0) {
            return true;
        }

        return false;
    }

    private boolean isReachable(int row, int col) {
        if (hasAdjacentAlly(row, col)) {
            return true;
        }

        boolean[][] visited = new boolean[SIZE][SIZE];
        return isReachableThroughDead(row, col, visited);
    }

    private boolean hasAdjacentAlly(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE) {
                    if (board[newRow][newCol] == (isXTurn ? CellState.X_ALIVE : CellState.O_ALIVE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isReachableThroughDead(int row, int col, boolean[][] visited) {
        if (hasAdjacentAlly(row, col)) {
            return true;
        }

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE
                        && !visited[newRow][newCol]) {

                    CellState deadEnemy = isXTurn ? CellState.O_DEAD : CellState.X_DEAD;
                    if (board[newRow][newCol] == deadEnemy) {
                        visited[newRow][newCol] = true;
                        if (isReachableThroughDead(newRow, newCol, visited)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isKillable(int row, int col) {
        CellState target = board[row][col];
        return (isXTurn && target == CellState.O_ALIVE) || (!isXTurn && target == CellState.X_ALIVE);
    }

    public void startGame() {
        gameState = GameState.PLAYING;
    }

    private boolean hasAvailableMoves() {
        if (isFirstMove()) {
            return true;
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((board[row][col] == CellState.EMPTY ||
                        (board[row][col] == (isXTurn ? CellState.O_ALIVE : CellState.X_ALIVE)))
                        && isReachable(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean enemyIsAlive() {
        if (isXTurn && (oFirstMove || oCount != 0)) {
            return true;
        } else if (!isXTurn && (xFirstMove || xCount != 0)) {
            return true;
        }
        return false;
    }

    private void endConditions() {
        // проверка доступных ходов у себя
        if (!hasAvailableMoves()) {
            gameState = GameState.FINISHED;
            winner = !isXTurn;
            return;
        }

        // проверка доступных ходов у оппонента
        isXTurn = !isXTurn;
        if (!hasAvailableMoves()) {
            gameState = GameState.FINISHED;
            isXTurn = !isXTurn;
            winner = isXTurn;
            return;
        }
        isXTurn = !isXTurn;

        // проверка кол-ва занятых оппонентом клеток
        if (!enemyIsAlive()) {
            gameState = GameState.FINISHED;
            winner = isXTurn;
        }
    }

    public void surrender() {
        gameState = GameState.FINISHED;
        winner = !isXTurn;
    }

    public String getWinner() {
        return winner ? "X" : "O";
    }

    public CellState getCellState(int row, int col) { return board[row][col]; }
    public boolean isXTurn() { return isXTurn; }
    public int getRemainingMoves() { return remainingMoves; }
    public static int getSize() { return SIZE; }

    public boolean isGameIdle() { return gameState == GameState.INITIAL; }
    public boolean isGameStarted() { return gameState == GameState.PLAYING; }
    public boolean isGameEnded() { return gameState == GameState.FINISHED; }
}

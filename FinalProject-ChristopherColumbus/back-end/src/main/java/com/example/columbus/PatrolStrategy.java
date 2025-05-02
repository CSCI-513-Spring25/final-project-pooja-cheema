package com.example.columbus;

/**
 * Patrol strategy for patrol pirate ship.
 * Move up/down a column, then move to next column
 */
public class PatrolStrategy implements MovementStrategy {

    /*
     * Check if grid cell has an island
     */
    private boolean isIsland(Game game, int row, int col) {
        for (int[] island : game.getState().getIslands()) {
            if (island[0] == row && island[1] == col)
                return true;
        }
        return false;
    }

    // Movement logic
    @Override
    public void move(PirateShip ship) {
        if (!(ship instanceof PatrolPirateShip)) 
            return;
        PatrolPirateShip patrol = (PatrolPirateShip) ship;
        Game game = Game.getInstance();

        int[] current = patrol.getPosition();
        int row = current[0];
        int col = current[1];
        int colDirection = patrol.getColDirection(); // +1 (right) or -1 (left)

        boolean goingDown = (col % 2 == 0); // Even columns: go down, odd columns: go up
        int nextRow = row, nextCol = col;
        // int lastCol = (colDirection == 1) ? 19 : 0;
        int[] treasure = game.getState().getTreasurePosition();

        // Move within the current column
        if (goingDown) {
            if (row < 19) {

                // Move down 1 cell
                nextRow = row + 1;
                if (!isIsland(game, nextRow, col) || (treasure[0] == nextRow && treasure[1] == col)) {
                    patrol.setPosition(new int[] { nextRow, col });
                    return;
                } else {

                    // Skip over stacked islands
                    boolean moved = false;
                    for (int scanRow = nextRow + 1; scanRow <= 19; scanRow++) {
                        if (!isIsland(game, scanRow, col) || (treasure[0] == scanRow && treasure[1] == col)) {
                            patrol.setPosition(new int[] { scanRow, col });
                            moved = true;
                            break;
                        }
                    }
                    if (moved)
                        return; // Stop if found a cell in column
                    // Else go to next column below
                }
            }
        } 
        
        // Go up odd columns
        else {
            if (row > 0) {
                // Try moving up
                nextRow = row - 1;
                if (!isIsland(game, nextRow, col) || (treasure[0] == nextRow && treasure[1] == col)) {
                    patrol.setPosition(new int[] { nextRow, col });
                    return;
                } else {
                    // Try to skip further up if blocked by stacked islands
                    boolean moved = false;
                    for (int scanRow = nextRow - 1; scanRow >= 0; scanRow--) {
                        if (!isIsland(game, scanRow, col) || (treasure[0] == scanRow && treasure[1] == col)) {
                            patrol.setPosition(new int[] { scanRow, col });
                            moved = true;
                            break;
                        }
                    }
                    if (moved)
                        return; // Stop if found a cell in column
                    // Else fall through to next column logic below
                }
            }
        }

        // At end of the column - move to next column
        nextCol = col + colDirection;

        // If next col is out of grid, reverse direction and move in the other direction
        if (nextCol < 0 || nextCol > 19) {
            patrol.setColDirection(-colDirection);
            nextCol = col + (-colDirection);
            if (nextCol < 0)
                nextCol = 0;
            if (nextCol > 19)
                nextCol = 19;
        }

        // Scan the whole next column for the first available cell
        boolean moved = false;
        if (goingDown) {

            // Scan Bottom to top, if earlier going down
            for (int scanRow = 19; scanRow >= 0; scanRow--) {
                if (!isIsland(game, scanRow, nextCol) || 
                    (treasure[0] == scanRow && treasure[1] == nextCol)) 
                    {
                    patrol.setPosition(new int[] { scanRow, nextCol });
                    moved = true;
                    break;
                }
            }
        } 
        else {
            // Scan Top to bottom, if earlier going up
            for (int scanRow = 0; scanRow <= 19; scanRow++) {
                if (!isIsland(game, scanRow, nextCol) || (treasure[0] == scanRow && treasure[1] == nextCol)) {
                    patrol.setPosition(new int[] { scanRow, nextCol });
                    moved = true;
                    break;
                }
            }
        }
        if (!moved)
            patrol.setPosition(current); // All cells blocked, stay put

        // Handle collision with CC
        int[] cc = game.getState().getCcPosition();
        int[] patrolPos = patrol.getPosition();
        if (patrolPos[0] == cc[0] && patrolPos[1] == cc[1]) {
            // game.hijackByPirate();
            game.getGameStateManager().hijackByPirate();
            return;
        }
    }
}

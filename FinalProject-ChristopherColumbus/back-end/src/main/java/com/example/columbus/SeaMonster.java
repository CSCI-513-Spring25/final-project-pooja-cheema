package com.example.columbus;

import java.util.Random;
import com.example.columbus.GameState.PirateShipState;

/**
 * This class represents a sea monster in the game.
 */
public class SeaMonster implements Entity {

    private int[] position; // Current sea monster position (x,y)
    private int[] initialPosition; // Original spawn position
    private int ignoreTurns = 0;

    /*
     * Construct a sea monster at random position on grid
     */
    public SeaMonster() {
        this.position = new int[] { 0, 0 };
        this.initialPosition = position.clone(); // Store the initial position
    }

    @Override
    public void move() {
        moveMonster();
    }

    /*
     * Move monster to random adjacent cell, that is,
     * at most 1 cell away from initial cell in each direction and diagonally.
     * So, it is moving within a 3*3 grid around initial position
     */
    private void moveMonster() {
        Random random = new Random();
        int[] ccPosition = Game.getInstance().getState().getCcPosition();
        int[] newPosition = position.clone();

        // Generate random movement within 1 cell range from initial position, possible
        // moves
        int[][] possibleMoves = {
                { 0, 0 }, { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 }
        };

        int maxAttempts = 20; // Limit the number of attempts to find a valid position (to prevent infinite
                              // trying)
        for (int i = 0; i < maxAttempts; i++) {
            int[] move = possibleMoves[random.nextInt(possibleMoves.length)];
            newPosition[0] = initialPosition[0] + move[0];
            newPosition[1] = initialPosition[1] + move[1];

            // Ensure the new position is within bounds
            if (newPosition[0] >= 0 && newPosition[0] < 20 && newPosition[1] >= 0 && newPosition[1] < 20) {

                // Check if the new position is valid, not occupied by island
                if (!Game.getInstance().getGameStateManager().isOccupied(newPosition)) {

                // if (!Game.getInstance().isOccupied(newPosition)) {
                
                    boolean isOccupiedByPirate = false; // Do not move onto pirate
                    for (PirateShipState pirate : Game.getInstance().getState().getPirates()) {
                        if (pirate.getPosition()[0] == newPosition[0] && pirate.getPosition()[1] == newPosition[1]) {
                            isOccupiedByPirate = true;
                            break;
                        }
                    }
                    // Ensure the new position is not occupied by another sea monster
                    boolean isOccupiedBySeaMonster = false;
                    for (SeaMonster seaMonster : Game.getInstance().getState().getSeaMonsters()) {
                        if (seaMonster.getPosition()[0] == newPosition[0]
                                && seaMonster.getPosition()[1] == newPosition[1]) {
                            isOccupiedBySeaMonster = true;
                            break;
                        }
                    }
                    // Ensure the new position is not occupied by the CC
                    if (!isOccupiedByPirate && !isOccupiedBySeaMonster
                            && !(newPosition[0] == ccPosition[0] && newPosition[1] == ccPosition[1])) {

                        // Update GameState's occupied matrix
                        Game.getInstance().getState().updateSeaMonsterPosition(position, newPosition);

                        // Move monster
                        position[0] = newPosition[0];
                        position[1] = newPosition[1];
                        break;
                    }
                }
            }
        }

    }

    /*
     * Get current position of monster
     */
    @Override
    public int[] getPosition() {
        return position;
    }

    /*
     * Set current position of sea monster
     */
    @Override
    public void setPosition(int[] position) {
        this.position = position;
        this.initialPosition = position.clone(); // So monster moves within 3x3 from actual spawn
    }

    @Override
    public void activateIgnoreMode(int turns) {
        this.ignoreTurns = turns;
    }

    @Override
    public boolean isIgnoringColumbus() {
        return ignoreTurns > 0;
    }

    @Override
    public void decrementIgnoreTurns() {
        if (ignoreTurns > 0)
            ignoreTurns--;
    }

}
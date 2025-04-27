package com.example.columbus;

import java.util.Random;
import com.example.columbus.GameState.PirateShipState;

/**
 * This class represents a sea monster in the game
 */
public class SeaMonster implements Entity, Observer {

    private int[] position; // Current sea monster position (x,y)

    /*
     * Construct a sea monster at random position on grid
     */
    public SeaMonster() {
        Random random = new Random();
        this.position = new int[] { random.nextInt(10), random.nextInt(10) };
    }

    @Override
    public void move() {
    }



    private void moveMonster() {
        System.out.println("Sea monster is inside private move method ");
        Random random = new Random();
        int[] ccPosition = Game.getInstance().getState().getCcPosition();
        int[] newPosition = position.clone();

        // Generate random movement within 3 cells range
        int maxAttempts = 10; // Limit the number of attempts to find a valid position
        for (int i = 0; i < maxAttempts; i++) {
            int deltaX = random.nextInt(7) - 3; // Range from -3 to 3
            int deltaY = random.nextInt(7) - 3; // Range from -3 to 3
            newPosition[0] = Math.max(0, Math.min(9, position[0] + deltaX));
            newPosition[1] = Math.max(0, Math.min(9, position[1] + deltaY));

            // Check if the new position is valid
            if (!Game.getInstance().isOccupied(newPosition)) {
                boolean isOccupiedByPirate = false;
                for (PirateShipState pirate : Game.getInstance().getState().getPirates()) {
                    if (pirate.getPosition()[0] == newPosition[0] && pirate.getPosition()[1] == newPosition[1]) {
                        isOccupiedByPirate = true;
                        break;
                    }
                }
                // Ensure the new position is not occupied by the CC or another sea monster
                boolean isOccupiedBySeaMonster = false;
                for (SeaMonster seaMonster : Game.getInstance().getState().getSeaMonsters()) {
                    if (seaMonster.getPosition()[0] == newPosition[0] && seaMonster.getPosition()[1] == newPosition[1]) {
                        isOccupiedBySeaMonster = true;
                        break;
                    }
                }
                // Ensure the new position is not occupied by the CC
                if (!isOccupiedByPirate && !isOccupiedBySeaMonster && !(newPosition[0] == ccPosition[0] && newPosition[1] == ccPosition[1])) {
                    Game.getInstance().getState().updateSeaMonsterPosition(position, newPosition); // Update the position in GameState
                    position[0] = newPosition[0];
                    position[1] = newPosition[1];
                    break;
                }
            }
        }

        System.out.println("Sea monster moving to " + position[0] + "," + position[1]);
        System.out.println("---------------- ");
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
    }

    @Override
    public void update(int[] ccPosition) {
        // respond to CC's position changes
        moveMonster();
        System.out.println("SeaMonster notified of CC's position: " + ccPosition[0] + "," + ccPosition[1]);
    }
    
}
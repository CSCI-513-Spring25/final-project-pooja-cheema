package com.example.columbus;

import java.util.Arrays;
import java.util.List;

/**
 * This class holds chase strategy that makes pirate ship chase columbus ship
 * 
 */
public class ChaseStrategy implements MovementStrategy {

    /*
     * Move pirate ship one step closer to columbus ship, avoiding occupied cells
     */
    @Override
    public void move(PirateShip ship) {

        // Get CC current position from game state
        int[] ccPosition = Game.getInstance().getState().getCcPosition();

        // Get pirate current position
        int[] position = ship.getPosition();
        int[] newPosition = position.clone();

        // Prioritize vertical movement first
        if (position[0] < ccPosition[0]) {
            newPosition[0] = Math.min(position[0] + 1, 19); // Move down
        } 
        else if (position[0] > ccPosition[0]) {
            newPosition[0] = Math.max(position[0] - 1, 0); // Move up
        } 

        // If on same row, move horizontally towards CC
        else if (position[1] < ccPosition[1]) {
            newPosition[1] = Math.min(position[1] + 1, 19); // Move right
        } else if (position[1] > ccPosition[1]) {
            newPosition[1] = Math.max(position[1] - 1, 0); // Move left
        }

        // Check if the new position is occupied or out of bounds
        if (Game.getInstance().isOccupied(newPosition) || 
          (newPosition[0] == 0 && newPosition[1] == 0) || 
          (newPosition[0] == ccPosition[0] && newPosition[1] == ccPosition[1])) 
          {
            // Try alternative paths
            List<int[]> alternatives = Arrays.asList(
                new int[]{position[0], Math.min(position[1] + 1, 19)}, // Move right
                new int[]{position[0], Math.max(position[1] - 1, 0)}, // Move left
                new int[]{Math.min(position[0] + 1, 19), position[1]}, // Move down
                new int[]{Math.max(position[0] - 1, 0), position[1]}  // Move up
            );

            for (int[] alternative : alternatives) {
                // Pick first unoccupied, valid cell
                if (!Game.getInstance().isOccupied(alternative) && !(alternative[0] == 0 && alternative[1] == 0) && !(alternative[0] == ccPosition[0] && alternative[1] == ccPosition[1])) {
                    newPosition = alternative;
                    break;
                }
            }
        }

        // Update pirate position
        position[0] = newPosition[0];
        position[1] = newPosition[1];
    }
}
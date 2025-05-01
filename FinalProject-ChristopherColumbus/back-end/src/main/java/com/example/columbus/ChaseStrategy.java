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

        Game game = Game.getInstance();
        GameStateManager gameState = game.getGameStateManager();
        // EntityManager entityManager = game.getEntityManager();

        int[] ccPosition = gameState.getCcPosition(); // Get CC current position

        // Get pirate current position
        int[] position = ship.getPosition();
        int[] newPosition = position.clone();

        // Prioritize vertical movement first
        if (position[0] < ccPosition[0]) {
            newPosition[0] = Math.min(position[0] + 1, 19); // Move down
        } else if (position[0] > ccPosition[0]) {
            newPosition[0] = Math.max(position[0] - 1, 0); // Move up
        }

        // If on same row, move horizontally towards CC
        else if (position[1] < ccPosition[1]) {
            newPosition[1] = Math.min(position[1] + 1, 19); // Move right
        } else if (position[1] > ccPosition[1]) {
            newPosition[1] = Math.max(position[1] - 1, 0); // Move left
        }

        // if (entityManager.isOccupied(newPosition) ||
        if (gameState.isOccupied(newPosition) ||
                Arrays.equals(newPosition, new int[] { 0, 0 }) ||
                Arrays.equals(newPosition, ccPosition)) {

            List<int[]> alternatives = Arrays.asList(
                    new int[] { position[0], Math.min(position[1] + 1, 19) },
                    new int[] { position[0], Math.max(position[1] - 1, 0) },
                    new int[] { Math.min(position[0] + 1, 19), position[1] },
                    new int[] { Math.max(position[0] - 1, 0), position[1] });

            for (int[] alt : alternatives) {
                // if (!entityManager.isOccupied(alt) &&
                if (!gameState.isOccupied(alt) &&
                        !(alt[0] == 0 && alt[1] == 0) &&
                        !(alt[0] == ccPosition[0] && alt[1] == ccPosition[1])) {
                    newPosition = alt;
                    break;
                }
            }
        }
        ship.setPosition(newPosition);
    }
}
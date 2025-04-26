package com.example.columbus;

import java.util.Arrays;
import java.util.List;

public class ChaseStrategy implements MovementStrategy {
    @Override
    public void move(PirateShip ship) {

        int[] ccPosition = Game.getInstance().getState().getCcPosition();
        int[] position = ship.getPosition();
        int[] newPosition = position.clone();

        System.out.println("ChaseStrategy: Current position: " + position[0] + "," + position[1]);
        System.out.println("ChaseStrategy: CC position: " + ccPosition[0] + "," + ccPosition[1]);

        // Prioritize horizontal movement first
        if (position[0] < ccPosition[0]) {
            newPosition[0] = Math.min(position[0] + 1, 9);
        } else if (position[0] > ccPosition[0]) {
            newPosition[0] = Math.max(position[0] - 1, 0);
        } else if (position[1] < ccPosition[1]) {
            newPosition[1] = Math.min(position[1] + 1, 9);
        } else if (position[1] > ccPosition[1]) {
            newPosition[1] = Math.max(position[1] - 1, 0);
        }

        // Check if the new position is occupied or out of bounds
        if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0) || (newPosition[0] == ccPosition[0] && newPosition[1] == ccPosition[1])) {
            // Try alternative paths
            List<int[]> alternatives = Arrays.asList(
                new int[]{position[0], Math.min(position[1] + 1, 9)}, // Move right
                new int[]{position[0], Math.max(position[1] - 1, 0)}, // Move left
                new int[]{Math.min(position[0] + 1, 9), position[1]}, // Move down
                new int[]{Math.max(position[0] - 1, 0), position[1]}  // Move up
            );

            for (int[] alternative : alternatives) {
                if (!Game.getInstance().isOccupied(alternative) && !(alternative[0] == 0 && alternative[1] == 0) && !(alternative[0] == ccPosition[0] && alternative[1] == ccPosition[1])) {
                    newPosition = alternative;
                    break;
                }
            }
        }

        position[0] = newPosition[0];
        position[1] = newPosition[1];

        System.out.println("ChaseStrategy: Pirate ship moving to " + position[0] + "," + position[1]);
    }
}
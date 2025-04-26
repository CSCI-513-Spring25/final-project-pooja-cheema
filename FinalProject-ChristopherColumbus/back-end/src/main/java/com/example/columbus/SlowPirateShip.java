package com.example.columbus;

/**
 * This class represents a slow pirate ship that moves one cell at a time towards CC ship
 */
public class SlowPirateShip extends PirateShip {

    // Move pirate ship 1 cell closer to CC, avoid islands
    @Override
    public void move() {
        int[] ccPosition = Game.getInstance().getState().getCcPosition();
        int[] newPosition = position.clone();

        // Determine direction to move towards CC based on CC's position
        if (ccPosition[0] < position[0]) { // Move up if CC is above
            newPosition[0] = Math.max(position[0] - 1, 0);

            // If blocked, alternatively move left/right
            if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                if (ccPosition[1] < position[1]) { // CC is to the left
                    newPosition = tryMove(position, "left", "right");
                } else { // CC is to the right
                    newPosition = tryMove(position, "right", "left");
                }
            }
            updatePosition(newPosition);
            return;
        } 
        
        // Move down if CC is below
        else if (ccPosition[0] > position[0]) {
            newPosition[0] = Math.min(position[0] + 1, 9);

            // If blocked, alternatively move left/right
            if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                if (ccPosition[1] < position[1]) { // CC is to the left
                    newPosition = tryMove(position, "left", "right");
                } else { // CC is to the right
                    newPosition = tryMove(position, "right", "left");
                }
            }
            updatePosition(newPosition);
            return;
        } 
        
        // Move left if CC is to the left
        else if (ccPosition[1] < position[1]) {
            newPosition[1] = Math.max(position[1] - 1, 0);

            // If blocked, alternatively move up/down
            if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                if (ccPosition[0] < position[0]) { // CC is above
                    newPosition = tryMove(position, "up", "down");
                } else { // CC is below
                    newPosition = tryMove(position, "down", "up");
                }
            }
            updatePosition(newPosition);
            return;
        } 
        
        // Move right if CC is to the right
        else if (ccPosition[1] > position[1]) {
            newPosition[1] = Math.min(position[1] + 1, 9);

            // If blocked, alternatively move up/down
            if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                if (ccPosition[0] < position[0]) { // CC is above
                    newPosition = tryMove(position, "up", "down");
                } else { // CC is below
                    newPosition = tryMove(position, "down", "up");
                }
            }
            updatePosition(newPosition);
            return;
        }
    }

    /*
     * Update CC's position
     */
    private void updatePosition(int[] newPosition) {
        position[0] = newPosition[0];
        position[1] = newPosition[1];

        // Log movement
        System.out.println("Slow pirate ship moving to " + position[0] + "," + position[1]);
        System.out.println("---------------- ");
    }

    /*
     * Try moving in specified (primary) direction.
     * If blocked, alternatively move in secondary direction
     */
    private int[] tryMove(int[] position, String primary, String secondary) {
        int[] newPosition = position.clone();

        // Move in primary direction
        switch (primary) {
            case "up":
                newPosition[0] = Math.max(position[0] - 1, 0);
                break;
            case "down":
                newPosition[0] = Math.min(position[0] + 1, 9);
                break;
            case "left":
                newPosition[1] = Math.max(position[1] - 1, 0);
                break;
            case "right":
                newPosition[1] = Math.min(position[1] + 1, 9);
                break;
        }

        // If primary direction is not blocked, move ahead
        if (!Game.getInstance().isOccupied(newPosition)) {
            return newPosition;
        }

        // Else, try secondary direction
        newPosition = position.clone();
        switch (secondary) {
            case "up":
                newPosition[0] = Math.max(position[0] - 1, 0);
                break;
            case "down":
                newPosition[0] = Math.min(position[0] + 1, 9);
                break;
            case "left":
                newPosition[1] = Math.max(position[1] - 1, 0);
                break;
            case "right":
                newPosition[1] = Math.min(position[1] + 1, 9);
                break;
        }
        return newPosition;
    }

    /*
     * Get type/name of pirate ship
     */
    @Override
    public String getType() {
        return "slow";
    }
}
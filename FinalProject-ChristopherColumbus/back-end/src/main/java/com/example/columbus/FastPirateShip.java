package com.example.columbus;

/**
 * This class represents a Fast Pirate Ship that moves two cells at each step
 */
public class FastPirateShip extends PirateShip {

    /*
     * Move the fast pirate ship towards christopher.
     * Ship moves 2 cells at a time in the direction of CC,
     * avoiding islands
     * 
     */
    @Override
    public void move() {

        int[] ccPosition = Game.getInstance().getState().getCcPosition(); // CC's current position
        int[] newPosition = position.clone(); // Copy current position for manipulation

        // Determine movement direction based on CC's position
        if (ccPosition[0] < position[0]) { // CC is above
            newPosition[0] = Math.max(position[0] - 2, 0); // Move up by 2 cells, not above grid

            // Check if blocked by obstacle or trying to move to (0,0)
            if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                
                // If can't move up, alternatively move left/rigt towards CC
                if (ccPosition[1] < position[1]) { // CC is to the left
                    newPosition = tryMove(position, "left", "right");
                } else { // CC is to the right
                    newPosition = tryMove(position, "right", "left");
                }
            }
            updatePosition(newPosition);
            return;
        } 
        
        else if (ccPosition[0] > position[0]) { // CC is below
            newPosition[0] = Math.min(position[0] + 2, 9); // Move down by 2 cells, not below grid
            if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                
                // If can't move down, alternatively move left/rigt towards CC
                if (ccPosition[1] < position[1]) { // CC is to the left
                    newPosition = tryMove(position, "left", "right");
                } else { // CC is to the right
                    newPosition = tryMove(position, "right", "left");
                }
            }
            updatePosition(newPosition);
            return;
        } 
        
        else if (ccPosition[1] < position[1]) { // CC is to the left (same row)
            newPosition[1] = Math.max(position[1] - 2, 0);
            if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                
                // If can't move left, alternatively move up/down towards CC
                if (ccPosition[0] < position[0]) { // CC is above
                    newPosition = tryMove(position, "up", "down");
                } else { // CC is below
                    newPosition = tryMove(position, "down", "up");
                }
            }
            updatePosition(newPosition);
            return;
        } 
        
        else if (ccPosition[1] > position[1]) { // CC is to the right (same row)
            newPosition[1] = Math.min(position[1] + 2, 9);
            if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                
                // If can't move right, alternatively move up/down towards CC
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
     * This method updates pirate's position and prints it's new position
     */
    private void updatePosition(int[] newPosition) {
        position[0] = newPosition[0];
        position[1] = newPosition[1];
        
        System.out.println("Fast pirate ship moving to " + position[0] + "," + position[1]);
        System.out.println("---------------- ");
    }

    /*
     * This method tries to move the pirate 2 cells in given direction.
     * if that direction is blocked, falls back to secondary direction
     */
    private int[] tryMove(int[] position, String primary, String secondary) {
        int[] newPosition = position.clone();
        switch (primary) {
            case "up":
                newPosition[0] = Math.max(position[0] - 2, 0);
                break;
            case "down":
                newPosition[0] = Math.min(position[0] + 2, 9);
                break;
            case "left":
                newPosition[1] = Math.max(position[1] - 2, 0);
                break;
            case "right":
                newPosition[1] = Math.min(position[1] + 2, 9);
                break;
        }
        
        // Move ahead if way is not blocked
        if (!Game.getInstance().isOccupied(newPosition)) {
            return newPosition; // Success
        }

        // Try secondary direction
        newPosition = position.clone();
        switch (secondary) {
            case "up":
                newPosition[0] = Math.max(position[0] - 2, 0);
                break;
            case "down":
                newPosition[0] = Math.min(position[0] + 2, 9);
                break;
            case "left":
                newPosition[1] = Math.max(position[1] - 2, 0);
                break;
            case "right":
                newPosition[1] = Math.min(position[1] + 2, 9);
                break;
        }
        return newPosition; // Could still be blocked
    }

    /*
     * This method returns string type of this pirate for identification
     */
    @Override
    public String getType() {
        return "fast";
    }
}
package com.example.columbus;

/**
 * This class represents chase strategy for a  a Fast Pirate Ship 
 * that moves two cells at a time towards CC
 */
    public class FastChaseStrategy implements MovementStrategy {

    /*
     * Move the fast pirate ship towards christopher.
     * Ship moves 2 cells at a time in the direction of CC,
     * avoiding islands
     */
    @Override
    public void move(PirateShip pirate) {

        if (Game.getInstance().getColumbus().isInvisible()) {
            return; // Don't chase if Columbus is invisible
        }

        Game game = Game.getInstance();
        GameStateManager gsm = game.getGameStateManager();

        int[] ccPosition = Game.getInstance().getColumbus().getPosition(); // CC's current position
        int[] position = pirate.getPosition(); // Get position from pirate
        int[] newPosition = position.clone(); // Copy current position for manipulation

        // Determine movement direction based on CC's position
        if (ccPosition[0] < position[0]) { // CC is above
            newPosition[0] = Math.max(position[0] - 2, 0); // Move up by 2 cells, not above grid

            // Check if blocked by obstacle or trying to move to (0,0)
            if (gsm.isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                
                // If can't move up, alternatively move left/rigt towards CC
                if (ccPosition[1] < position[1]) { // CC is to the left
                    newPosition = tryMove(position, "left", "right");
                } else { // CC is to the right
                    newPosition = tryMove(position, "right", "left");
                }
            }
            pirate.setPosition(newPosition);

            return;
        } 
        
        else if (ccPosition[0] > position[0]) { // CC is below
            newPosition[0] = Math.min(position[0] + 2, 19); // Move down by 2 cells, not below grid
            // if (Game.getInstance().isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
            if (gsm.isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                // If can't move down, alternatively move left/rigt towards CC
                if (ccPosition[1] < position[1]) { // CC is to the left
                    newPosition = tryMove(position, "left", "right");
                } else { // CC is to the right
                    newPosition = tryMove(position, "right", "left");
                }
            }
            // updatePosition(newPosition);
            pirate.setPosition(newPosition);

            return;
        } 
        
        else if (ccPosition[1] < position[1]) { // CC is to the left (same row)
            newPosition[1] = Math.max(position[1] - 2, 0);
                if (gsm.isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                
                // If can't move left, alternatively move up/down towards CC
                if (ccPosition[0] < position[0]) { // CC is above
                    newPosition = tryMove(position, "up", "down");
                } else { // CC is below
                    newPosition = tryMove(position, "down", "up");
                }
            }
            pirate.setPosition(newPosition);
            return;
        } 
        
        else if (ccPosition[1] > position[1]) { // CC is to the right (same row)
            newPosition[1] = Math.min(position[1] + 2, 19);
                if (gsm.isOccupied(newPosition) || (newPosition[0] == 0 && newPosition[1] == 0)) {
                
                // If can't move right, alternatively move up/down towards CC
                if (ccPosition[0] < position[0]) { // CC is above
                    newPosition = tryMove(position, "up", "down");
                } else { // CC is below
                    newPosition = tryMove(position, "down", "up");
                }
            }
            pirate.setPosition(newPosition);
            return;
        }
        pirate.setPosition(newPosition);
    }

    /*
     * This method tries to move the pirate 2 cells in given direction.
     * if that direction is blocked, falls back to secondary direction
     */
    private int[] tryMove(int[] position, String primary, String secondary) {

        GameStateManager gsm = Game.getInstance().getGameStateManager();

        int[] newPosition = position.clone();
        switch (primary) {
            case "up":
                newPosition[0] = Math.max(position[0] - 2, 0);
                break;
            case "down":
                newPosition[0] = Math.min(position[0] + 2, 19);
                break;
            case "left":
                newPosition[1] = Math.max(position[1] - 2, 0);
                break;
            case "right":
                newPosition[1] = Math.min(position[1] + 2, 19);
                break;
        }
        
        // Move ahead if way is not blocked
        if (gsm.isOccupied(newPosition)) {
            return newPosition; // Success
        }

        // Try secondary direction
        newPosition = position.clone();
        switch (secondary) {
            case "up":
                newPosition[0] = Math.max(position[0] - 2, 0);
                break;
            case "down":
                newPosition[0] = Math.min(position[0] + 2, 19);
                break;
            case "left":
                newPosition[1] = Math.max(position[1] - 2, 0);
                break;
            case "right":
                newPosition[1] = Math.min(position[1] + 2, 19);
                break;
        }
        return newPosition; // Could still be blocked
    }
}
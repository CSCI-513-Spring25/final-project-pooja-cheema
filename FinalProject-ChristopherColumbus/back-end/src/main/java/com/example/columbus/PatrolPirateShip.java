package com.example.columbus;

/**
 * Represents patrol pirate ship that follows a patrol route.
 * It moves vertically around the entire grid.
 */
public class PatrolPirateShip extends PirateShip {

    // Fields to track patrol state
    private int mode = 0; // 0 = column, 1 = row 

    // Current column and row of patrol ship
    private int col = 0; 
    private int row = 0;

    // Direction of column patrol (used to reverse patrol at edges)
    private int colDirection = 1; // +1 (right), -1 (left)

    // Get column direction
    public int getColDirection() {
        return colDirection;
    }

    // Set column direction
    public void setColDirection(int dir) {
        this.colDirection = dir;
    }

    /*
     * Default constructor, sets default patrol direction
     */
    public PatrolPirateShip() {
        super(); // Initialize parent pirate ship
        colDirection = 1;
    }

    // Getters and setters for patrol state (mode 0 or 1)
    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    // Get/Set current column for patrol logic
    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    // Get/Set current row for patrol logic
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    /*
     * Returns string type of this pirate ship for identification
     */
    @Override
    public String getType() {
        return "patrol";
    }
}
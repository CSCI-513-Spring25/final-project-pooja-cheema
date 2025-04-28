package com.example.columbus;

// import java.util.Timer;
// import java.util.TimerTask;

public class PatrolPirateShip extends PirateShip {

    
    private int mode = 0;
    private int col = 0;
    private int row = 0;

    private int colDirection = 1;

    public int getColDirection() {
        return colDirection;
    }

    public void setColDirection(int dir) {
        this.colDirection = dir;
    }

    public PatrolPirateShip() {
        super();

        colDirection = 1;
    }

    // Getters and setters for patrol state
    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    /*
     * This method returns string type of this pirate ship for identification
     */
    @Override
    public String getType() {
        return "patrol";
    }
}
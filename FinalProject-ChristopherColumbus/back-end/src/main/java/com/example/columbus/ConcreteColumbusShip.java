package com.example.columbus;

/**
 * Concrete implementation of ColumbusShip interface
 */
public class ConcreteColumbusShip implements ColumbusShip {
    private int[] position = new int[] {0, 0}; // Start position of the CC ship on grid

    // Get current position of CC ship.
    @Override
    public int[] getPosition() {
        return position;
    }

    // Set new position of CC ship.
    @Override
    public void setPosition(int[] position) {
        this.position = position;
    }

     // Indicates whether the ship is invisible.
    @Override
    public boolean isInvisible() {
        return false; // Default: ship is visible
    }

    // Decrements incisible cloak counter.
    @Override
    public void decrementCloak() {
        // Do nothing here
    }
}

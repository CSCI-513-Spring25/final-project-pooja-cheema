package com.example.columbus;


/**
 * Interface representing CC ship in the game.
 */
public interface ColumbusShip {
    int[] getPosition(); // current position of CC ship

    void setPosition(int[] position); // Set CC ship position

    boolean isInvisible(); // Check if CC ship is currently invisible

    void decrementCloak(); // Decrease turns left for CC's invisibility cloak power
}

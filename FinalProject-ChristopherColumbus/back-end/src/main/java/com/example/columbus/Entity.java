package com.example.columbus;

/** 
 * Interface representing movable entities (Christopher ship, pirate ships, monsters)
 */
public interface Entity {
    public void move(); // Method for movement logic

    public int[] getPosition(); // Method to get current position of entity

    public void setPosition(int[] position); // Method to set new position of entity

    public void activateIgnoreMode(int turns); // Method to activate ignore mode by pirates for CC

    default boolean isIgnoringColumbus() { // If entity is in ignoring-CC mode
        return false;
    }

    public void decrementIgnoreTurns(); // Decrement CC's invisible cloak turns

}
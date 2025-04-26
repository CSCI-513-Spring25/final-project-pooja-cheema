package com.example.columbus;

// Represents movable entities (Christopher ship, pirate ships, monsters)
public interface Entity {
    public void move(); // Method for movement logic

    public int[] getPosition(); // Method to get current position of entity

    public void setPosition(int[] position); // Method to set new position of entity
}
package com.example.columbus;

import java.util.Random;

/**
 * Abstract class representing a pirate ship.
 * It implements Observer and Entity interfaces to allow it to be 
 * notified of CC's position
 */
public abstract class PirateShip implements Observer, Entity {

    protected int[] position; // Current pirate ship position
    protected MovementStrategy strategy; // Current movement strategy for pirate ship

    /*
     * Constructor to initialize pirate ship at a random position within the grid
     */
    public PirateShip() {
        Random random = new Random();
        this.position = new int[] { random.nextInt(20), random.nextInt(20) };
    }

    /*
     * Get current position of pirate ship
     */
    @Override
    public int[] getPosition() {
        return position;
    }

    /*
     * Set current position of pirate ship
     */
    @Override
    public void setPosition(int[] position) {
        this.position = position;
    }

    /*
     * Set movement strategy for pirate ship
     */
    public void setStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    /*
     * Get movement strategy for pirate ship
     */
    public MovementStrategy getStrategy() {
        return strategy;
    } 

    /*
     * Move pirate ship according to current movement strategy
     */
    public void move() {
        strategy.move(this);
    }    

    /*
     * This method is called when CC position is updated.
     */
    @Override
    public void update(int[] ccPosition) {
        move();
    }

    /*
     * Get type/name of pirate ship.
     * To be implemented by concrete sub classes
     */
    public abstract String getType();
}
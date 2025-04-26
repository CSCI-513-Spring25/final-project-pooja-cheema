package com.example.columbus;

import java.util.Random;

/**
 * This class represents a sea monster in the game
 */
public class SeaMonster implements Entity {

    private int[] position; // Current sea monster position (x,y)

    /*
     * Construct a sea monster at random position on grid
     */
    public SeaMonster() {
        Random random = new Random();
        this.position = new int[] { random.nextInt(10), random.nextInt(10) };
    }

    @Override
    public void move() {
    }

    /*
     * Get current position of monster
     */
    @Override
    public int[] getPosition() {
        return position;
    }

    /*
     * Set current position of sea monster
     */
    @Override
    public void setPosition(int[] position) {
        this.position = position;
    }
}
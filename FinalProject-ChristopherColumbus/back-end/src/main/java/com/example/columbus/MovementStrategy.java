package com.example.columbus;

/**
 * Implementing different movement behaviours for pirate ships.
 * This is part of Strategy Design Pattern, allowing PirateShip objects 
 * to change their movement algorithm at runtime
 */
public interface MovementStrategy {
    public void move(PirateShip ship);
}
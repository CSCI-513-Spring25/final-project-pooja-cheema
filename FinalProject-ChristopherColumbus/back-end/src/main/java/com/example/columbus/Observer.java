package com.example.columbus;

/**
 * To notify observers of CC's position change
 */
public interface Observer {
    
    public void update(int[] ccPosition);
}
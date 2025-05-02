package com.example.columbus;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite class to represent a group of entities (for composite pattern).
 * Allows collective movement and management of multiple entities as one.
 */
public class EntityGroup implements Entity {

    private List<Entity> entities = new ArrayList<>(); // List to hold all entities in a group
    private int ignoreTurns = 0; // Number of turns to ignore Columbus


    // This method adds an entity to the group
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    // This method returns the entities to the group
    public List<Entity> getEntities() {
        return entities;
    }

    public boolean isIgnoring() {
        return ignoreTurns > 0;
    }

    @Override
    public void move() {
        if (ignoreTurns > 0) {
            ignoreTurns--; // Decrement the ignore turn count
            return; // Skip all entity movement this turn (or only skip collision logic, your choice)
        }

        for (Entity entity : entities) {
            entity.move();
        }
    }

    public void activateIgnoreMode(int turns) {
        for (Entity entity : entities) {
            entity.activateIgnoreMode(turns);
        }
    }
    
    // Decrement number of CC's turns ignored by slow and fast pirates
    public void decrementIgnoreTurns() {
        for (Entity entity : entities) {
            entity.decrementIgnoreTurns();
        }
    }

    @Override
    public int[] getPosition() {
        // Composite EntityGroup does not have a single position (Each entity has a single position)
        // So, throw an exception
        throw new UnsupportedOperationException("getPosition is not supported for EntityGroup");
    }

    @Override
    public void setPosition(int[] position) {
        // Set position for all entities in the group
        for (Entity entity : entities) {
            entity.setPosition(position);
        }
    }
}
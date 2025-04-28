package com.example.columbus;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite class to represent a group of entities (for composite pattern).
 * Allows collective movement and management of multiple entities as one.
 */
public class EntityGroup implements Entity {

    private List<Entity> entities = new ArrayList<>(); // List to hold all entities in a group

    // This method adds an entity to the group
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    // This method returns the entities to the group
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public void move() {
        for (Entity entity : entities) { //Loop through all entities
            entity.move(); 
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
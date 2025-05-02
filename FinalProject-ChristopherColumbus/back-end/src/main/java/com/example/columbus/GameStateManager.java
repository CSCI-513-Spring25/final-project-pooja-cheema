package com.example.columbus;

import java.util.*;

/**
 * This class manages the overall game state, including CC ship,
 * treasure location, collision detection, and cell occupancy
 */
public class GameStateManager {
    private ColumbusShip columbus = new ConcreteColumbusShip(); // CC ship
    private int[] treasurePosition = { 19, 19 }; // Default treasure location
    private String collisionStatus = null; // Collision type (island/pirate etc.)
    private Set<String> occupied = new HashSet<>(); // Track occupied grid cells 
    private static final int GRID_SIZE = 20; // Grid size
    private EntityManager entityManager;

    // Initializes default state (CC at 0,0)
    public GameStateManager() {
        occupied.add("0,0");
    }

    /**
     * Resets game state:
     * - Resets Columbus to (0,0)
     * - Generates a new treasure location
     * - Clears all occupied cells
     */
    public void reset() {

        columbus = new ConcreteColumbusShip();
        columbus.setPosition(new int[] { 0, 0 });

        Random r = new Random();
        do {
            treasurePosition = new int[] { r.nextInt(20), r.nextInt(20) };
        } while (Arrays.equals(treasurePosition, columbus.getPosition()) || isOccupied(treasurePosition));
        addOccupied(treasurePosition);

        collisionStatus = null;
        occupied.clear(); // Clear tracker for occupied cells
        addOccupied(columbus.getPosition());

    }

    // Handles player movement and updates game state
    public GameState handleMove(String direction, EntityManager em, ObserverManager om) {
        int[] newPosition = columbus.getPosition().clone();

        // Compute new position based on direction
        switch (direction) {
            case "up":
                newPosition[0] = Math.max(0, newPosition[0] - 1);
                break;
            case "down":
                newPosition[0] = Math.min(GRID_SIZE - 1, newPosition[0] + 1);
                break;
            case "left":
                newPosition[1] = Math.max(0, newPosition[1] - 1);
                break;
            case "right":
                newPosition[1] = Math.min(GRID_SIZE - 1, newPosition[1] + 1);
                break;
        }

         // Island collision blocks movement
        if (em.isIsland(newPosition))
            return new GameState(columbus.getPosition(), treasurePosition, em.getPirates(), em.getMonsters(),
                    em.getIslands(),
                    "island", columbus);

        // Monster collision: allow movement but mark it            
        if (em.isMonster(newPosition)) {
            columbus.setPosition(newPosition); // Update cc position
            om.notifyObservers(columbus.getPosition());

            columbus.decrementCloak(); // Decrement invisibility turns
            UnwrapColumbus(); // Replace decorator CC ship with base CC ship if cloak expired

            return new GameState(columbus.getPosition(), treasurePosition, em.getPirates(), em.getMonsters(),
                    em.getIslands(),
                    "monster", columbus);
        }

        // Pirate collision: reset game state
        if (em.isPirate(newPosition)) {
            reset();
            em.initializeEntities(om);
            om.notifyObservers(columbus.getPosition());
            return new GameState(columbus.getPosition(), treasurePosition, em.getPirates(), em.getMonsters(),
                    em.getIslands(),
                    "pirate", columbus);
        }

        // Treasure collected
        if (Arrays.equals(newPosition, treasurePosition))
            return new GameState(columbus.getPosition(), treasurePosition, em.getPirates(), em.getMonsters(),
                    em.getIslands(),
                    "treasure", columbus);

        columbus.setPosition(newPosition); // Update cc position
        om.notifyObservers(columbus.getPosition());
        entityManager.getPirateGroup().decrementIgnoreTurns(); // Decrement invisibility turns
        columbus.decrementCloak(); // decrement invisibility cloak
        UnwrapColumbus(); // unwrap after cloak expires

        return new GameState(columbus.getPosition(), treasurePosition, em.getPirates(), em.getMonsters(),
                em.getIslands(), null, columbus);
    }

    // If pirate hijacks CC
    public void hijackByPirate() {
        this.collisionStatus = "pirate";
    }

    // Clears all occupied cells except (0,0)
    public void clearOccupied() {
        occupied.clear();
        occupied.add("0,0");
    }

    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    // Returns current game state
    public GameState getState() {
        return new GameState(
                columbus.getPosition(),
                treasurePosition,
                entityManager.getPirates(),
                entityManager.getMonsters(),
                entityManager.getIslands(),
                collisionStatus,
                columbus);
    }

    public void setCollisionStatus(String status) {
        this.collisionStatus = status;
    }

    public int[] getCcPosition() {
        return columbus.getPosition();
    }

    public boolean isOccupied(int[] pos) {
        return occupied.contains(pos[0] + "," + pos[1]);
    }

    public boolean isColumbusInvisible() {
        return columbus.isInvisible();
    }

    // Removes invisibility decorator if cloak duration is over
    public void UnwrapColumbus() {
        if (columbus instanceof InvisibleColumbusDecorator) {
            ColumbusShip unwrapped = ((InvisibleColumbusDecorator) columbus).unwrapIfExpired();
            if (unwrapped != columbus) {
                columbus = unwrapped;
            }
        }
    }

    public void addOccupied(int[] pos) {
        occupied.add(pos[0] + "," + pos[1]);
    }

    public void removeOccupied(int[] pos) {
        occupied.remove(pos[0] + "," + pos[1]);
    }

    public String getCollisionStatus() {
        return collisionStatus;
    }

    public ColumbusShip getColumbus() {
        return columbus;
    }

    public void setColumbus(ColumbusShip columbus) {
        this.columbus = columbus;
    }
}
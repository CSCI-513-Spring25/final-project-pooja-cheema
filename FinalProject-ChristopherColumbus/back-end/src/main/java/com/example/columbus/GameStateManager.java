package com.example.columbus;

import java.util.*;

public class GameStateManager {
    private int[] ccPosition = { 0, 0 };
    private int[] treasurePosition = { 19, 19 };
    private String collisionStatus = null;
    private Set<String> occupied = new HashSet<>();
    private static final int GRID_SIZE = 20;
    private EntityManager entityManager;

    public GameStateManager() {
        occupied.add("0,0");
    }

    public void reset() {
        ccPosition = new int[] { 0, 0 };
        // treasurePosition = new int[]{19, 19};


        Random r = new Random();
        do {
            treasurePosition = new int[] { r.nextInt(20), r.nextInt(20) };
        } while (Arrays.equals(treasurePosition, ccPosition) || isOccupied(treasurePosition));
        addOccupied(treasurePosition);

        
        collisionStatus = null;
        occupied.clear();
        occupied.add("0,0");
    }

    public GameState handleMove(String direction, EntityManager em, ObserverManager om) {
        int[] newPosition = ccPosition.clone();

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

        if (em.isIsland(newPosition))
            return new GameState(ccPosition, treasurePosition, em.getPirates(), em.getMonsters(), em.getIslands(),
                    "island");
        if (em.isMonster(newPosition)) {
            ccPosition = new int[] { 0, 0 };
            om.notifyObservers(ccPosition);
            return new GameState(ccPosition, treasurePosition, em.getPirates(), em.getMonsters(), em.getIslands(),
                    "monster");
        }
        if (em.isPirate(newPosition)) {
            reset();
            em.initializeEntities(om);
            om.notifyObservers(ccPosition);
            return new GameState(ccPosition, treasurePosition, em.getPirates(), em.getMonsters(), em.getIslands(),
                    "pirate");
        }
        if (Arrays.equals(newPosition, treasurePosition))
            return new GameState(ccPosition, treasurePosition, em.getPirates(), em.getMonsters(), em.getIslands(),
                    "treasure");
        ccPosition = newPosition;
        om.notifyObservers(ccPosition);
        return new GameState(ccPosition, treasurePosition, em.getPirates(), em.getMonsters(), em.getIslands(), null);
    }

    public void hijackByPirate() {
        this.collisionStatus = "pirate";
    }

    public void clearOccupied() {
        occupied.clear();
        occupied.add("0,0");
    }

    
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    public GameState getState() {
        return new GameState(
                ccPosition,
                treasurePosition,
                entityManager.getPirates(),
                entityManager.getMonsters(),
                entityManager.getIslands(),
                collisionStatus);
    }

    // public void setEntityManager(EntityManager em) {
    // this.entityManager = em;
    // }

    public void setCollisionStatus(String status) {
        this.collisionStatus = status;
    }

    // public GameState getState() {
    // return new GameState(ccPosition, treasurePosition, null, null, null,
    // collisionStatus);
    // }

    public int[] getCcPosition() {
        return ccPosition;
    }

    public boolean isOccupied(int[] pos) {
        return occupied.contains(pos[0] + "," + pos[1]);
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
    
}
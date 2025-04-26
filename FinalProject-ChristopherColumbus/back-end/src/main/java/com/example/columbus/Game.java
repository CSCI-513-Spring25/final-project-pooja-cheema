package com.example.columbus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class acts as main model for the CC game.
 * 
 * It manages state of all entities (CC, pirate, monsters, island, treasure) on the grid,
 * handles movement logic, and applies Observer Pattern to notify pirate ships of CC ship movement
 * 
 * This is implemented as Singleton so that only one instance of game exists.
 */
public class Game {
    
    private static Game instance; // Singleton instance
    private static final int GRID_SIZE = 10; // Size of grid
    private int[] ccPosition = { 0, 0 }; // CC initial ship position
    private int[] treasurePosition = { 9, 9 }; // Treasure position
    private List<PirateShip> pirates = new ArrayList<>(); // List of Pirate Ships
    private List<Entity> monsters = new ArrayList<>(); // List of sea monsters
    private List<int[]> islands = new ArrayList<>(); // List of island positions
    private List<Observer> observers = new ArrayList<>(); // List of Observers
    private Set<String> occupiedPositions = new HashSet<>(); // A set to keep track of occupied grid cells

    // Private constructor for singleton
    private Game() {
        createPirates();
        createMonsters();
        createIslands();
        occupiedPositions.add("0,0"); // Mark CC's starting position cell as occupied cell
    }

    /*
     * This method returns singleton instance of Game class
     */
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /*
     * This method starts or resets the game to initial state
     */
    public void start() {
        ccPosition = new int[] { 0, 0 }; // Ensure CC starts at [0, 0]
        occupiedPositions.clear();
        occupiedPositions.add("0,0");
        resetEntities(); // Randomly place entities 
        notifyObservers(); // Notify pirates about CC's ship position
    }

    /*
     * This method moves CC ship in specified direction.
     * It handles restricting movement to islands, collisions with pirate/monster/treasure,
     * and notifies observers (pirates) after CC's move
     */
    public GameState move(String direction) {
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

        // Check if the new position is an island (can not move onto island)
        for (int[] island : islands) {
            if (island[0] == newPosition[0] && island[1] == newPosition[1]) {
                return new GameState(ccPosition, treasurePosition, pirates, monsters, islands, "island");
            }
        }

        // Check if the new position is a sea monster, then CC goes back to initial position(0,0)
        for (Entity monster : monsters) {
            if (monster.getPosition()[0] == newPosition[0] && monster.getPosition()[1] == newPosition[1]) {
                ccPosition = new int[] { 0, 0 }; // Reset CC position
                notifyObservers();
                return new GameState(ccPosition, treasurePosition, pirates, monsters, islands, "monster");
            }
        }

        // Check if the new position is a pirate ship, (means CC ship hijacked) then reset the game
        for (PirateShip pirate : pirates) {
            if (pirate.getPosition()[0] == newPosition[0] && pirate.getPosition()[1] == newPosition[1]) {
                start(); // Reset the game
                notifyObservers();
                return new GameState(ccPosition, treasurePosition, pirates, monsters, islands, "pirate");
            }
        }

        // Check if the new position is the treasure, then CC wins
        if (newPosition[0] == treasurePosition[0] && newPosition[1] == treasurePosition[1]) {
            return new GameState(ccPosition, treasurePosition, pirates, monsters, islands, "treasure");
        }

        // Move CC to new position
        ccPosition = newPosition;
        notifyObservers();

        // Pirates move in response to Observer notofication
        return getState();
    }

    /*
     * This method returns the current state of the game
     */
    public GameState getState() {
        return new GameState(ccPosition, treasurePosition, pirates, monsters, islands, null);
    }

    /*
     * This method creates and places pirate ships, and registers them as observers
     */
    private void createPirates() {

        // Create one fast pirate ship
        PirateShip fastPirate = PirateShipFactory.createPirateShip("fast");
        fastPirate.setStrategy(new ChaseStrategy());
        placeEntity(fastPirate);
        pirates.add(fastPirate);
        addObserver(fastPirate);

        // Create one slow pirate ship
        // for (int i = 0; i < 2; i++) {
            PirateShip slowPirate = PirateShipFactory.createPirateShip("slow");
            slowPirate.setStrategy(new ChaseStrategy());
            placeEntity(slowPirate);
            pirates.add(slowPirate);
            addObserver(slowPirate);
        // }
    }

    /*
     * This method creates and places sea monsters as an entity group
     */
    private void createMonsters() {
        EntityGroup monsterGroup = new EntityGroup();
        for (int i = 0; i < 3; i++) {
            SeaMonster monster = new SeaMonster();
            placeEntity(monster);
            monsterGroup.addEntity(monster);
        }
        monsters.addAll(monsterGroup.getEntities());
    }

    /*
     * This method randomly creates and places islands on the grid
     */
    private void createIslands() {
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int[] position;
            do {
                position = new int[] { random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE) };
            } while (occupiedPositions.contains(position[0] + "," + position[1]));
            islands.add(position);
            occupiedPositions.add(position[0] + "," + position[1]);
        }
    }

    /*
     * This method resets and randomly repositions all entities (treasure, pirates, monsters, islands)
     */
    private void resetEntities() {

        Random random = new Random();

        // Reset treasure position
        do {
            treasurePosition = new int[] { random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE) };
        } while (occupiedPositions.contains(treasurePosition[0] + "," + treasurePosition[1]));
        occupiedPositions.add(treasurePosition[0] + "," + treasurePosition[1]);

        // Reset pirate positions
        for (PirateShip pirate : pirates) {
            placeEntity(pirate);
        }

        // Reset monster positions
        for (Entity monster : monsters) {
            placeEntity(monster);
        }

        // Reset island positions
        islands.clear();
        for (int i = 0; i < 5; i++) {
            int[] position;
            do {
                position = new int[] { random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE) };
            } while (occupiedPositions.contains(position[0] + "," + position[1]) || isAdjacentToCC(position));
            islands.add(position);
            occupiedPositions.add(position[0] + "," + position[1]);
        }
    }
    
    /*
     * This method is responsible to randomly place an entity on the grid,
     * but not on an occupied cell, or adjacent to CC's cell
     */
    private void placeEntity(Entity entity) {
        Random random = new Random();
        int[] position;
        do {
            position = new int[] { random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE) };
        } while (occupiedPositions.contains(position[0] + "," + position[1]) || (position[0] == 0 && position[1] == 0)
                || isAdjacentToCC(position));
        entity.setPosition(position);
        occupiedPositions.add(position[0] + "," + position[1]);
    }

    /*
     * This method checks if a given position is directly adjacent to CC's position,
     * to prevent very early collision at the start of the game
     */
    private boolean isAdjacentToCC(int[] position) {
        return (position[0] == 0 && position[1] == 1) || (position[0] == 1 && position[1] == 0)
                || (position[0] == 1 && position[1] == 1);
    }

    /*
     * This method checks if a grid position is occupied by any entity
     * (CC, islands, pirates, monsters, treasure)
     */
    public boolean isOccupied(int[] position) {
        String posKey = position[0] + "," + position[1];
        return occupiedPositions.contains(posKey);
    }

    /*
     * This method adds an Observer (implementing Observer Pattern)
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /*
     * This method notifies all observers (pirates) of CC's current position 
     * (implementing Observer Pattern)
     */
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(ccPosition);
        }
    }
}
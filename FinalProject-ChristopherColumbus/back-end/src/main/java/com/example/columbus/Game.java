package com.example.columbus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * This class acts as main model for the CC game.
 * 
 * It manages state of all entities (CC, pirate, monsters, island, treasure) on
 * the grid,
 * handles movement logic, and applies Observer Pattern to notify pirate ships
 * of CC ship movement
 * 
 * This is implemented as Singleton so that only one instance of game exists.
 */
public class Game {

    private static Game instance; // Singleton instance
    private static final int GRID_SIZE = 10; // Size of grid
    private int[] ccPosition = { 0, 0 }; // CC initial ship position
    // private int[] treasurePosition = { 9, 9 }; // Treasure position

    private int[] treasurePosition; // Treasure position

    private List<PirateShip> pirates = new ArrayList<>(); // List of Pirate Ships
    private List<Entity> monsters = new ArrayList<>(); // List of sea monsters
    private List<int[]> islands = new ArrayList<>(); // List of island positions
    private List<Observer> observers = new ArrayList<>(); // List of Observers
    private Set<String> occupiedPositions = new HashSet<>(); // A set to keep track of occupied grid cells

    private ScheduledExecutorService monsterMover; // Executor for scheduling monster movements
    // private Timer monsterTimer;

    private ScheduledExecutorService patrolScheduler; // Scheduler for patrolling pirate

    private PatrolPirateShip patrolPirate; // Keep track of patrol ship

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
    // public static Game getInstance() {
    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public synchronized void start() {
        ccPosition = new int[] { 0, 0 };
        occupiedPositions.clear();
        occupiedPositions.add("0,0");
        pirates.clear();
        monsters.clear();
        islands.clear();
        observers.clear();

        Random random = new Random();
    do {
        treasurePosition = new int[] { random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE) };
    } while (occupiedPositions.contains(treasurePosition[0] + "," + treasurePosition[1]));
    occupiedPositions.add(treasurePosition[0] + "," + treasurePosition[1]);

        createPirates();
        createMonsters();
        createIslands();
        notifyObservers();

        startMonsterMovement();
        startPatrollingPirate();
    }

    /*
     * This method moves CC ship in specified direction.
     * It handles restricting movement to islands, collisions with
     * pirate/monster/treasure,
     * and notifies observers (pirates) after CC's move
     */
    public synchronized GameState move(String direction) {

        System.out.println("CC moving " + direction);

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

                stopMonsterMovement();
                stopPatrollingPirate();

                start(); // Reset the game
                notifyObservers();
                return new GameState(ccPosition, treasurePosition, pirates, monsters, islands, "pirate");
            }
        }
 

        // Check if the new position is the treasure, then CC wins
        if (newPosition[0] == treasurePosition[0] && newPosition[1] == treasurePosition[1]) {

            stopMonsterMovement();
            stopPatrollingPirate();

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
    public synchronized GameState getState() {
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

        // Create one patrol pirate ship (scheduler based)
        patrolPirate = (PatrolPirateShip) PirateShipFactory.createPirateShip("patrol");
        patrolPirate.setStrategy(new PatrolStrategy());
        placeEntity(patrolPirate);
        pirates.add(patrolPirate);
        // addObserver(fastPirate);

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

            // addObserver(monster);

        }
        monsters.addAll(monsterGroup.getEntities());
    }

    /*
     * This method randomly creates and places islands on the grid
     */
    private void createIslands() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int[] position;
            do {
                position = new int[] { random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE) };
            } while (occupiedPositions.contains(position[0] + "," + position[1]));
            islands.add(position);
            occupiedPositions.add(position[0] + "," + position[1]);
        }
    }

    /*
     * This method resets and randomly repositions all entities (treasure, pirates,
     * monsters, islands)
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


        // Reset patrol pirate's position and patrol state
        if (patrolPirate != null) {
            // patrolPirate.setPosition(new int[] { 0, 0 }); // Or use placeEntity(patrolPirate) for random
            placeEntity(patrolPirate);
            patrolPirate.setMode(0);
            patrolPirate.setCol(0);
            patrolPirate.setRow(0);
            // occupiedPositions.add("0,0");
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
        return (position[0] == 0 && position[1] == 1) ||
                (position[0] == 1 && position[1] == 0) ||
                (position[0] == 1 && position[1] == 1);
    }

    /*
     * This method checks if a grid position is occupied by any entity
     * (CC, islands, pirates, monsters, treasure)
     */
    public synchronized boolean isOccupied(int[] position) {
        String posKey = position[0] + "," + position[1];
        return occupiedPositions.contains(posKey);
    }

    /*
     * This method adds an Observer (implementing Observer Pattern)
     */
    public synchronized void addObserver(Observer observer) {
        observers.add(observer);
    }

    /*
     * This method notifies all observers (pirates) of CC's current position
     * (implementing Observer Pattern)
     */
    public synchronized void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(ccPosition);
        }
    }

    /*
     * This method starts time-based movement of monsters.
     * It initializes the scheduler if not already running,
     * and schedules moveAllMonsters() method to run every specified number of
     * seconds.
     */
    public void startMonsterMovement() {
        if (monsterMover == null || monsterMover.isShutdown()) {

            // Create single-threaded scheduled executor
            monsterMover = Executors.newSingleThreadScheduledExecutor();

            // Schedule moveAllMonsters() to run every 3 seconds (after initial 3 seconds)
            monsterMover.scheduleAtFixedRate(() -> {
                moveAllMonsters();
            }, 3, 3, TimeUnit.SECONDS); // start after 3 sec, repeat every 3 sec
        }
    }

    /*
     * This method stops periodic monster movement,
     * shuts down the scheduled executor if running
     */
    public void stopMonsterMovement() {
        if (monsterMover != null) {
            monsterMover.shutdownNow();
        }
    }

    /*
     * This method moves all moneters by calling their move() method
     * Synchronized for thread safety since it may be called
     * by separate scheduler thread.
     */
    private void moveAllMonsters() {
        synchronized (this) { // Synchronized to ensure thread safety if accessed from multiple threads
            for (Entity monster : monsters) {
                monster.move();
            }
        }
    }

    public void startPatrollingPirate() {
        stopPatrollingPirate();

        // Scheduler for auto=move every 2 seconds
        patrolScheduler = Executors.newSingleThreadScheduledExecutor();
        patrolScheduler.scheduleAtFixedRate(() -> movePatrolPirate(), 2, 2, TimeUnit.SECONDS);

    }

    public void stopPatrollingPirate() {
        if (patrolScheduler != null) {
            patrolScheduler.shutdownNow();
            patrolScheduler = null;
        }
    }

    private void movePatrolPirate() {
        synchronized (this) {
            if (patrolPirate != null) {
                patrolPirate.move();

                // Check for hijack with CC after move:
                int[] cc = ccPosition;
                int[] next = patrolPirate.getPosition();
                if (Arrays.equals(next, cc)) {
                    // End game: hijack
                    stopMonsterMovement();
                    stopPatrollingPirate();
                    // Optionally: set collision to "pirate" in GameState, or restart
                }
            }
        }
    }

}
package com.example.columbus;

import java.util.ArrayList;
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
    private static final int GRID_SIZE = 20; // Size of grid
    private int[] ccPosition = { 0, 0 }; // CC initial ship position
    private int[] treasurePosition = { 19, 19 }; // Treasure position
    private List<PirateShip> pirates = new ArrayList<>(); // List of Pirate Ships
    private List<Entity> monsters = new ArrayList<>(); // List of sea monsters
    private List<int[]> islands = new ArrayList<>(); // List of island positions
    private List<Observer> observers = new ArrayList<>(); // List of Observers
    private Set<String> occupiedPositions = new HashSet<>(); // A set to keep track of occupied grid cells
    private String collisionStatus = null;
    private ScheduledExecutorService monsterMover; // Executor for scheduling monster movements
    private ScheduledExecutorService patrolScheduler; // Scheduler for patrolling pirate
    private PatrolPirateShip patrolPirate; // Keep track of patrol ship
    private String currentStrategy = "slow"; // Track current applied strategy type


    // Private constructor for singleton
    private Game() {
        createPirates();
        createMonsters();
        createIslands();
        occupiedPositions.add("0,0"); // Mark CC's starting position cell as occupied cell
    }

    /*
     * This method returns singleton instance of Game class.
     * It ensures only one instance of Game exists throughout application lifecycle
     */
    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /*
     * It initializes or restarts the game
     */
    public synchronized void start() {

        collisionStatus = null; // Reset collision status

        // Reset columbus starting position and clear occupied positions
        ccPosition = new int[] { 0, 0 };
        occupiedPositions.clear();
        occupiedPositions.add("0,0");

        // Clear all entities and observers
        pirates.clear();
        monsters.clear();
        islands.clear();
        observers.clear();

        currentStrategy = "slow"; // Reset to default strategy (for toggle purpose)

        // Recreate pirates, monsters, islands for fresh game
        createPirates();
        createMonsters();
        createIslands();
        createTreasure();
        notifyObservers();

        // Start scheduled movement of monster and pirate patrolling
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

        // Check if the new position is a pirate ship, (means CC ship hijacked)
        // then reset the game
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
        GameState state = new GameState(ccPosition, treasurePosition, pirates, monsters, islands, collisionStatus);
        return state;
    }

    /*
     * This method places treasure on the grid at random position each time
     */
    public void createTreasure() {
        // Randomly generate and place treasure, on unoccupied cell
        Random random = new Random();
        do {
            treasurePosition = new int[] { random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE) };
        } while (occupiedPositions.contains(treasurePosition[0] + "," + treasurePosition[1]));
        occupiedPositions.add(treasurePosition[0] + "," + treasurePosition[1]);
    }

    /*
     * This method creates and places pirate ships, and registers them as observers
     */
    private void createPirates() {
        // Create one fast pirate ship
        PirateShip fastPirate = PirateShipFactory.createPirateShip("fast");
        PirateShip slowPirate = PirateShipFactory.createPirateShip("slow");
    
        if (currentStrategy.equals("fast")) {
            fastPirate.setStrategy(new SlowChaseStrategy()); // roles are swapped
            slowPirate.setStrategy(new FastChaseStrategy());
        } else {
            fastPirate.setStrategy(new FastChaseStrategy());
            slowPirate.setStrategy(new SlowChaseStrategy());
        }
    
        placeEntity(fastPirate);
        placeEntity(slowPirate);
    
        pirates.add(fastPirate);
        pirates.add(slowPirate);
    
        addObserver(fastPirate);
        addObserver(slowPirate);
    
        // Create one patrol pirate ship
        patrolPirate = (PatrolPirateShip) PirateShipFactory.createPirateShip("patrol");
        patrolPirate.setStrategy(new PatrolStrategy());
        placeEntity(patrolPirate);
        pirates.add(patrolPirate);
    }
    
    /*
     * This method creates and places sea monsters as an entity group
     */
    private void createMonsters() {
        EntityGroup monsterGroup = new EntityGroup();
        for (int i = 0; i < 1; i++) {
            // for (int i = 0; i < 1; i++) {
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
        for (int i = 0; i < 20; i++) {
            int[] position;
            do {
                position = new int[] { random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE) };
            } while (occupiedPositions.contains(position[0] + "," + position[1]));
            islands.add(position);
            occupiedPositions.add(position[0] + "," + position[1]);
        }
    }

    /*
     * Place entities(slow pirate, fast pirate, patrol pirate, sea monster) on grid,
     * such that they are not placed within 3 cells around CC starting position (0,0)
     * to avoid early collision
     */
    private void placeEntity(Entity entity) {
        Random random = new Random();
        int[] position;

        while (true) {
            position = new int[] { random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE) };
            String key = position[0] + "," + position[1];

            // Ensure cell is not occupied
            if (occupiedPositions.contains(key))
                continue;

            // Place at least 3 cells away from CC initial position (0,0)
            if ((Math.abs(position[0]) + Math.abs(position[1]) < 3))
                continue;

            // Valid position
            entity.setPosition(position);
            occupiedPositions.add(key);
            break;
        }
    }

    /*
     * This method checks if a given position is directly adjacent to CC's position,
     * to prevent very early collision at the start of the game
     */
    // private boolean isAdjacentToCC(int[] position) {
    // return (position[0] == 0 && position[1] == 1) ||
    // (position[0] == 1 && position[1] == 0) ||
    // (position[0] == 1 && position[1] == 1);
    // }

    /*
     * This method checks if a grid position is occupied by any entity
     * (CC, islands, pirates, monsters, treasure)
     */
    public synchronized boolean isOccupied(int[] position) {
        String posKey = position[0] + "," + position[1];
        return occupiedPositions.contains(posKey);
    }

    /*
     * Toggle chase strategies of fast and slow pirate
     * during runtime using a button click
     */
    public synchronized void togglePirateStrategies() {
        for (PirateShip pirate : pirates) {
            if (pirate.getType().equals("fast")) {
                if (pirate.getStrategy() instanceof FastChaseStrategy) {
                    pirate.setStrategy(new SlowChaseStrategy());
                } else {
                    pirate.setStrategy(new FastChaseStrategy());
                }
            } else if (pirate.getType().equals("slow")) {
                if (pirate.getStrategy() instanceof SlowChaseStrategy) {
                    pirate.setStrategy(new FastChaseStrategy());
                } else {
                    pirate.setStrategy(new SlowChaseStrategy());
                }
            }
        }
        currentStrategy = currentStrategy.equals("slow") ? "fast" : "slow";
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
     * Called when CC is hijacked by Patrolling pirate
     */
    public synchronized void hijackByPirate() {

        collisionStatus = "pirate"; // Mark the collision status

        // Stop all movement
        stopMonsterMovement();
        stopPatrollingPirate();

        // Notify any observing pirates
        notifyObservers();
    }

    /*
     * Get current strategy
     */
    public String getCurrentStrategy() {
        return currentStrategy;
    }
    
    /*
     * Gets current collision status
     * Used by server to see if collision needs to be reported to front end
     */
    public String getCollisionStatus() {
        return collisionStatus;
    }

    // Set collision status
    public void setCollisionStatus(String status) {
        this.collisionStatus = status;
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

    /*
     * Starts scheduled patrolling movement
     */
    public void startPatrollingPirate() {
        stopPatrollingPirate(); // Stop existing scheduler befire starting new one

        // Scheduler for auto-move every 2 seconds
        patrolScheduler = Executors.newSingleThreadScheduledExecutor();
        patrolScheduler.scheduleAtFixedRate(() -> movePatrolPirate(), 2, 2, TimeUnit.SECONDS);

    }

    /*
     * Stops movement if running.
     */
    public void stopPatrollingPirate() {
        if (patrolScheduler != null) {
            patrolScheduler.shutdownNow(); // Shut down scheduler and release resources
            patrolScheduler = null;
        }
    }

    /*
     * Move patrolling pirate according to strategy.
     * This method is called periodically by scheduler.
     */
    private void movePatrolPirate() {
        synchronized (this) { // Ensure thread safety
            if (patrolPirate != null) {
                patrolPirate.move();

                // Check for hijack with CC after move
                int[] cc = ccPosition;
                int[] next = patrolPirate.getPosition();

                if (isSameOrAdjacent(cc, next)) {
                    hijackByPirate();
                }

            }
        }
    }

    // Returns true if two positions are the same or adjacent (including diagonals)
    private boolean isSameOrAdjacent(int[] a, int[] b) {
        int dx = Math.abs(a[0] - b[0]);
        int dy = Math.abs(a[1] - b[1]);
        return dx <= 1 && dy <= 1;
    }

}
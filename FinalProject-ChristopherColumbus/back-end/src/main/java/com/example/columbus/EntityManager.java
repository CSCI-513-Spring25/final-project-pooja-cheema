package com.example.columbus;

import java.util.*;

/**
 * This class manages all game entities including pirates, sea monsters, and islands.
 * Handles placement, strategy toggling, composite operations
 */
public class EntityManager {

    // List of individual game entities
    private List<PirateShip> pirates = new ArrayList<>();
    private List<Entity> monsters = new ArrayList<>();
    private List<int[]> islands = new ArrayList<>();

    // Group containers for composite behaviors
    private EntityGroup monsterGroup = new EntityGroup();
    private EntityGroup pirateGroup = new EntityGroup(); // Composite group for fast & slow pirates

    private GameStateManager stateManager; // For managing occupied positions and game state
    private String currentStrategy = "slow"; // Track current chase strategy mode
    private PatrolPirateShip patrolPirate;

    // Constructor to initialize EntityManager
    public EntityManager(GameStateManager gsm) {
        this.stateManager = gsm;
    }

    /**
     * Initialize all entities: pirates, patrol pirate, sea monsters, and islands.
     * Handles strategy setup and observer registration.
     */
    public void initializeEntities(ObserverManager om) {

        int[] ccStart = new int[] { 0, 0 };

        // Clear previous entities and groups
        pirates.clear();
        monsters.clear();
        islands.clear();
        monsterGroup = new EntityGroup();
        pirateGroup = new EntityGroup();

        Random r = new Random();

        // Create pirate ships with opposite strategies depending on currentStrategy
        PirateShip fast = PirateShipFactory.createPirateShip("fast");
        PirateShip slow = PirateShipFactory.createPirateShip("slow");
        if ("fast".equals(currentStrategy)) {
            fast.setStrategy(new SlowChaseStrategy());
            slow.setStrategy(new FastChaseStrategy());
        } else {
            fast.setStrategy(new FastChaseStrategy());
            slow.setStrategy(new SlowChaseStrategy());
        }

        // Place pirates atleat 3 cells away from CC start position
        place(fast, ccStart, 3);
        place(slow, ccStart, 3);

        // Add to composite group and observers
        pirateGroup.addEntity(fast);
        pirateGroup.addEntity(slow);
        pirates.add(fast);
        pirates.add(slow);
        om.addObserver(fast);
        om.addObserver(slow);

         // Initialize patrol pirate with PatrolStrategy
        patrolPirate = (PatrolPirateShip) PirateShipFactory.createPirateShip("patrol");
        patrolPirate.setStrategy(new PatrolStrategy());
        place(patrolPirate, new int[]{0, 0}, 3);
        pirates.add(patrolPirate);

        // Add sea monsters 3 cells away from CC start position
        for (int i = 0; i < 6; i++) {
            SeaMonster m = new SeaMonster();
            place(m, ccStart, 3);
            monsterGroup.addEntity(m);
            monsters.add(m);
        }

        // Randomly place islands on grid such that they don't overlap with other entities
        for (int i = 0; i < 20; i++) {
            int[] pos;
            do {
                pos = new int[] { r.nextInt(20), r.nextInt(20) };
            } 
            while ( stateManager.isOccupied(pos) || distance(pos, ccStart) < 3 );
            islands.add(pos);
            stateManager.addOccupied(pos);
        }
    }

    // Places entity at a random unoccupied position.
    public void place(Entity e) {
        Random r = new Random();
        int[] pos;
        do {
            pos = new int[] { r.nextInt(20), r.nextInt(20) };
        } while (stateManager.isOccupied(pos));
        e.setPosition(pos);
        stateManager.addOccupied(pos);
    }

    // Helper method for placing entities on grid
    private void place(Entity e, int[] avoidPos, int minDistance) {
        Random r = new Random();
        int[] pos;
        do {
            pos = new int[] { r.nextInt(20), r.nextInt(20) };
        } while (
            stateManager.isOccupied(pos) ||
            (avoidPos != null && distance(pos, avoidPos) < minDistance)
        );
        e.setPosition(pos);
        stateManager.addOccupied(pos);
    }
    
    /**
     * Calculate Chebyshev distance between two positions.
     */
    private int distance(int[] a, int[] b) {
        return Math.max(Math.abs(a[0] - b[0]), Math.abs(a[1] - b[1]));
    }    

    // Toggles fast/slow pirates strategies
    public void toggleStrategies() {
        for (PirateShip p : pirates) {
            if ("fast".equals(p.getType())) {
                p.setStrategy(p.getStrategy() instanceof FastChaseStrategy ? new SlowChaseStrategy()
                        : new FastChaseStrategy());
            } else {
                p.setStrategy(p.getStrategy() instanceof SlowChaseStrategy ? new FastChaseStrategy()
                        : new SlowChaseStrategy());
            }
        }
        currentStrategy = currentStrategy.equals("slow") ? "fast" : "slow";
    }

    // Resets pirate strategies to default "slow"
    public void resetStrategyState() {
        currentStrategy = "slow";
    }

    // Clear all occupied positions in the state manager
    public void clearOccupied() {
        stateManager.clearOccupied();
    } 

    // Activates invisibility cloak effect on pirate group (ignores CC for 5 turns)
    public void activateInvisibilityCloak() {
        pirateGroup.activateIgnoreMode(5); // Ignore CC for 5 turns 
    }

    // Utility methotd to check if an entity is at a given position  
    public boolean isIsland(int[] pos) {
        return islands.stream().anyMatch(p -> Arrays.equals(p, pos));
    }

    public boolean isMonster(int[] pos) {
        return monsters.stream().anyMatch(m -> Arrays.equals(m.getPosition(), pos));
    }

    public boolean isPirate(int[] pos) {
        return pirates.stream().anyMatch(p -> Arrays.equals(p.getPosition(), pos));
    }

    public boolean isOccupied(int[] pos) {
        return stateManager.isOccupied(pos);
    }
    
    // Getters
    public PatrolPirateShip getPatrolPirate() {
        return patrolPirate;
    }

    public List<PirateShip> getPirates() {
        return pirates;
    }

    public List<Entity> getMonsters() {
        return monsters;
    }

    public List<int[]> getIslands() {
        return islands;
    }

    public String getCurrentStrategy() {
        return currentStrategy;
    }

    public EntityGroup getPirateGroup() {
        return pirateGroup;
    }
}

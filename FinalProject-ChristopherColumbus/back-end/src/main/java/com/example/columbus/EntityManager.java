package com.example.columbus;

import java.util.*;

public class EntityManager {
    private List<PirateShip> pirates = new ArrayList<>();
    private List<Entity> monsters = new ArrayList<>();
    private List<int[]> islands = new ArrayList<>();
    private EntityGroup monsterGroup = new EntityGroup();
    private GameStateManager stateManager;
    private String currentStrategy = "slow";

    private EntityGroup pirateGroup = new EntityGroup(); // Composite group for fast & slow pirates

    private PatrolPirateShip patrolPirate;

    public EntityManager(GameStateManager gsm) {
        this.stateManager = gsm;
    }

    public void initializeEntities(ObserverManager om) {

        int[] ccStart = new int[] { 0, 0 };

        pirates.clear();
        monsters.clear();
        islands.clear();
        monsterGroup = new EntityGroup();
        pirateGroup = new EntityGroup();

        Random r = new Random();

        // Pirates
        PirateShip fast = PirateShipFactory.createPirateShip("fast");
        PirateShip slow = PirateShipFactory.createPirateShip("slow");
        if ("fast".equals(currentStrategy)) {
            fast.setStrategy(new SlowChaseStrategy());
            slow.setStrategy(new FastChaseStrategy());
        } else {
            fast.setStrategy(new FastChaseStrategy());
            slow.setStrategy(new SlowChaseStrategy());
        }
        place(fast, ccStart, 3);
        place(slow, ccStart, 3);
        pirateGroup.addEntity(fast);
        pirateGroup.addEntity(slow);
        pirates.add(fast);
        pirates.add(slow);
        om.addObserver(fast);
        om.addObserver(slow);

        // Patrol Pirate
        patrolPirate = (PatrolPirateShip) PirateShipFactory.createPirateShip("patrol");
        patrolPirate.setStrategy(new PatrolStrategy());
        // place(patrolPirate, ccStart, 3);
        place(patrolPirate, new int[]{0, 0}, 3);

        pirates.add(patrolPirate);

        // Monsters
        for (int i = 0; i < 1; i++) {
            SeaMonster m = new SeaMonster();
            place(m, ccStart, 3);
            monsterGroup.addEntity(m);
            monsters.add(m);
        }

        // Islands
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
    
    private int distance(int[] a, int[] b) {
        return Math.max(Math.abs(a[0] - b[0]), Math.abs(a[1] - b[1]));
    }    

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

    public void resetStrategyState() {
        currentStrategy = "slow";
    }

    public void clearOccupied() {
        stateManager.clearOccupied();
    } 

    public void activateInvisibilityCloak() {
        // monsterGroup.activateIgnoreMode(3);
        pirateGroup.activateIgnoreMode(5); // Ignore CC for 5 turns 
    }

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
}

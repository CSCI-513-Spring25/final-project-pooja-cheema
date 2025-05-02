package com.example.columbus;

import java.util.concurrent.*;

/**
 * Controls scheduled movement of dynamic entities like sea monsters and patrol pirates
 * Uses ScheduledExecutorService to run movement logic periodically
 */
public class MovementController {
    private ScheduledExecutorService monsterExec; // Scheduler for sea monsters
    private EntityManager em;
    private GameStateManager gsm;
    private ScheduledExecutorService patrolExec; // Scheduler for patrol pirate

    public MovementController(GameStateManager gsm, EntityManager em) {
        this.gsm = gsm;
        this.em = em;
    }

    // Starts all periodic movement
    public void startAll() {
        startMonsterMovement();
        startPatrollingPirate();
    }

    // Schedules movement for sea monsters every 3 seconds
    public void startMonsterMovement() {
        stopMonsterMovement();  // Ensure old executor is gone before creating new one
        monsterExec = Executors.newSingleThreadScheduledExecutor();
        monsterExec.scheduleAtFixedRate(() -> moveMonsters(), 3, 3, TimeUnit.SECONDS);
    }

    // Cancels monster movement scheduler if running
    public void stopMonsterMovement() {
        if (monsterExec != null && !monsterExec.isShutdown()) {
            monsterExec.shutdownNow();
        }
    }

    /*
     * Move patrolling pirate according to strategy.
     * It moves vertically, patrolling each column from 
     * top to bottom (or bottom to top), then moves to next column. 
     * When reaches vertices of grid(top-left) (top-right) (bottom-left) (bottom-right),
     * it reverses its direction
     * 
     * This method is called periodically by scheduler.
     */
    public void startPatrollingPirate() {

        stopPatrollingPirate(); // Ensures only one executor runs

        if (em.getPatrolPirate() == null)
            return;

        patrolExec = Executors.newSingleThreadScheduledExecutor();
        patrolExec.scheduleAtFixedRate(() -> {

            if (gsm.getCollisionStatus() != null) return; // Skip if modal is active

            em.getPatrolPirate().move(); // Move patrol pirate based on strategy

            int[] cc = gsm.getCcPosition();
            int[] piratePos = em.getPatrolPirate().getPosition();

            // Check if patrol pirate collides or is adjacent to CC
            int dx = Math.abs(piratePos[0] - cc[0]);
            int dy = Math.abs(piratePos[1] - cc[1]);

            if (dx <= 1 && dy <= 1) { // same or adjacent (including diagonals)
                gsm.setCollisionStatus("pirate"); // Inform frontend of collision
            }

        }, 3, 3, TimeUnit.SECONDS);
    }

    //  Cancels patrol pirate movement scheduler if running
    public void stopPatrollingPirate() {
        if (patrolExec != null) {
            patrolExec.shutdownNow();
        }
    }

    // Stops all scheduled movement tasks
    public void stopAll() {
        stopMonsterMovement();
        stopPatrollingPirate();
    }
    
    // Resumes movement tasks after a pause or restart
    public void resumeAll() {
        startMonsterMovement();
        startPatrollingPirate();
    }

    // Restarts all scheduled movement
    public void restart() {
        stopAll();       // Ensures old ones are gone
        startAll();      // Starts new ones
    }
   
    // Moves each monster one step based on its movement logic
    public void moveMonsters() {
        for (Entity e : em.getMonsters()) {
            e.move();
        }
    }
}
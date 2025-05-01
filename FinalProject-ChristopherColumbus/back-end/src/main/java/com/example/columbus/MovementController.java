package com.example.columbus;

import java.util.concurrent.*;

public class MovementController {
    private ScheduledExecutorService monsterExec;
    private EntityManager em;
    private GameStateManager gsm;
    private ScheduledExecutorService patrolExec;

    public MovementController(GameStateManager gsm, EntityManager em) {
        this.gsm = gsm;
        this.em = em;
    }

    public void startAll() {
        startMonsterMovement();
        startPatrollingPirate();
    }

    public void startMonsterMovement() {
        stopMonsterMovement();  // Ensure old executor is gone before creating new one
        monsterExec = Executors.newSingleThreadScheduledExecutor();
        monsterExec.scheduleAtFixedRate(() -> moveMonsters(), 3, 3, TimeUnit.SECONDS);
    }

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
            // patrolExec.scheduleWithFixedDelay(() -> {

            if (gsm.getCollisionStatus() != null) return; // Skip if modal is active

            em.getPatrolPirate().move();

            int[] cc = gsm.getCcPosition();
            int[] piratePos = em.getPatrolPirate().getPosition();

            // Check if same or adjacent

            int dx = Math.abs(piratePos[0] - cc[0]);
            int dy = Math.abs(piratePos[1] - cc[1]);

            if (dx <= 1 && dy <= 1) { // same or adjacent (including diagonals)
                gsm.setCollisionStatus("pirate"); // for frontend to show modal
                // gsm.reset(); // reset the game after modal triggers
            }

        }, 3, 3, TimeUnit.SECONDS);
    }

    public void stopPatrollingPirate() {
        if (patrolExec != null) {
            patrolExec.shutdownNow();
        }
    }

    public void stopAll() {
        stopMonsterMovement();
        stopPatrollingPirate();
    }
    
    public void resumeAll() {
        startMonsterMovement();
        startPatrollingPirate();
    }

    public void restart() {
        stopAll();       // Ensures old ones are gone
        startAll();      // Starts new ones
    }
   
    public void moveMonsters() {
        for (Entity e : em.getMonsters()) {
            e.move();
        }
    }
}
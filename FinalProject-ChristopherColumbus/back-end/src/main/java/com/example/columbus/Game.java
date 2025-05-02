package com.example.columbus;

/**
 * This class implements Facade and Singleton Design patterns 
 * for Columbus game.
 * It manages initialization, state updates, entity logic, and movement scheduling.
 */
public class Game {
    private static Game instance; // For single Game instance
    private GameStateManager stateManager; // Handle game state (positions, collisions)
    private EntityManager entityManager; // Manage pirates, monsters, islands
    private MovementController movementController; // Schedule monsters, patrol pirate movement
    private ObserverManager observerManager; // Handle observer pattern for pirates

    /*
     * Private constructor to implement Singleton pattern
     * Initializes all game components and starts movement
     */
    private Game() {
        stateManager = new GameStateManager();
        entityManager = new EntityManager(stateManager);

        // Inform manager know about entities
        stateManager.setEntityManager(entityManager); 

        observerManager = new ObserverManager();
        movementController = new MovementController(stateManager, entityManager);

        // Place and register initial game entities
        entityManager.initializeEntities(observerManager);

        // Start scheduled movement (patrol pirate, monsters)
        movementController.startAll();
    }

    /*
     * Provides singleton Game instance
     */
    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /**
     * Resets the game to a fresh state and reinitializes entities
     */
    public void start() {
        stateManager.setCollisionStatus(null); // Clear any previous collision
        stateManager.reset(); // Reset positions and status
        entityManager.clearOccupied(); // Clear grid occupation
        entityManager.resetStrategyState(); // Set default pirate strategies
        entityManager.initializeEntities(observerManager); // Recreate pirates/monsters/patrol
        movementController.restart(); // Start monster/patrol scheduler
    }

    // Handle CC ship movement in given direction
    public GameState move(String direction) {
        return stateManager.handleMove(direction, entityManager, observerManager);
    }

    // Sets collision status
    public void setCollisionStatus(String status) {
        stateManager.setCollisionStatus(status);
    }
    
    // Returns current game state 
    public GameState getState() {
        return stateManager.getState();
    }   
    
    // Toggle pirate ship strategies slow/fast dynamically
    public void togglePirateStrategies() {
        entityManager.toggleStrategies();
    }
 
    // Apply invisibility cloak power to CC for 5 turns
    public void activateInvisibilityCloak() {
        ColumbusShip cloaked = new InvisibleColumbusDecorator(stateManager.getColumbus(), 5);
        stateManager.setColumbus(cloaked);
    }

    // Return the current ColumbusShip instance
    public ColumbusShip getColumbus() {
        return stateManager.getColumbus();
    }
    
    // Return currently active pirate strategy (slow/fast)
    public String getCurrentStrategy() {
        return entityManager.getCurrentStrategy();
    }

    // MovementController instance
    public MovementController getMovementController() {
        return movementController;
    }
    
    // GameStateManager instance
    public GameStateManager getGameStateManager() {
        return stateManager;
    }

    // EntityManager instance
    public EntityManager getEntityManager() {
        return entityManager;
    }
} 

package com.example.columbus;

public class Game {
    private static Game instance; // For single Game instance
    private GameStateManager stateManager;
    private EntityManager entityManager;
    private MovementController movementController;
    private ObserverManager observerManager;

    // Private constructor
    private Game() {
        stateManager = new GameStateManager();
        entityManager = new EntityManager(stateManager);

        stateManager.setEntityManager(entityManager); 

        observerManager = new ObserverManager();
        movementController = new MovementController(stateManager, entityManager);

        entityManager.initializeEntities(observerManager);

        movementController.startAll();
    }

    // For singleton
    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void start() {

        stateManager.setCollisionStatus(null); // Clear collision
        stateManager.reset(); // Reset positions and status
        entityManager.clearOccupied();
        
        entityManager.resetStrategyState(); // Reset strategy flag
        entityManager.initializeEntities(observerManager); // Recreate pirates, monsters, patrol
        movementController.restart(); // Start monster/patrol scheduler
    }

    public GameState move(String direction) {
        return stateManager.handleMove(direction, entityManager, observerManager);
    }

    public void setCollisionStatus(String status) {
        stateManager.setCollisionStatus(status);
    }
    
    public GameState getState() {
        return stateManager.getState();
    }   
    
    public void togglePirateStrategies() {
        entityManager.toggleStrategies();
    }

    public void activateInvisibilityCloak() {
        ColumbusShip cloaked = new InvisibleColumbusDecorator(stateManager.getColumbus(), 5);
        stateManager.setColumbus(cloaked);
    }

    public ColumbusShip getColumbus() {
        return stateManager.getColumbus();
    }
    

    public String getCurrentStrategy() {
        return entityManager.getCurrentStrategy();
    }

    public MovementController getMovementController() {
        return movementController;
    }
    
    public GameStateManager getGameStateManager() {
        return stateManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
} 

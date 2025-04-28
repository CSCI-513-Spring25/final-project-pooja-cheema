package com.example.columbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChaseStrategyTest {

    private ChaseStrategy chaseStrategy;
    private PirateShip mockPirateShip;
    private Game mockGame;
    private GameState mockGameState;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize the ChaseStrategy
        chaseStrategy = new ChaseStrategy();

        // Mock the PirateShip, Game, and GameState classes
        mockPirateShip = mock(PirateShip.class);
        mockGame = mock(Game.class);
        mockGameState = mock(GameState.class);

        // Use reflection to set the Game singleton instance
        setGameInstance(mockGame);
        when(mockGame.getState()).thenReturn(mockGameState);
    }

    @Test
    void testMoveTowardsCC() {
        // Set up CC and pirate ship positions
        when(mockGameState.getCcPosition()).thenReturn(new int[]{10, 10});
        int[] piratePosition = new int[]{5, 5};
        when(mockPirateShip.getPosition()).thenReturn(piratePosition);

        // Perform the move
        chaseStrategy.move(mockPirateShip);

        // Verify the pirate ship moved closer to CC
        assertArrayEquals(new int[]{6, 5}, piratePosition, "Pirate ship should move closer to CC");
    }

    @Test
    void testAvoidOccupiedPosition() {
        // Set up CC and pirate ship positions
        when(mockGameState.getCcPosition()).thenReturn(new int[]{10, 10});
        int[] piratePosition = new int[]{5, 5};
        when(mockPirateShip.getPosition()).thenReturn(piratePosition);

        // Mock an occupied position
        when(mockGame.isOccupied(new int[]{6, 5})).thenReturn(true); // Down is occupied
        when(mockGame.isOccupied(new int[]{5, 6})).thenReturn(false); // Right is free

        // Perform the move
        chaseStrategy.move(mockPirateShip);

        // Verify the pirate ship moved to an alternative position
        assertArrayEquals(new int[]{5, 6}, piratePosition, "Pirate ship should move to an alternative position");
    }

    @Test
    void testAvoidCCPosition() {
        // Set up CC and pirate ship positions
        when(mockGameState.getCcPosition()).thenReturn(new int[]{6, 5});
        int[] piratePosition = new int[]{5, 5};
        when(mockPirateShip.getPosition()).thenReturn(piratePosition);

        // Perform the move
        chaseStrategy.move(mockPirateShip);

        // Verify the pirate ship avoided CC's position
        assertArrayEquals(new int[]{5, 6}, piratePosition, "Pirate ship should avoid CC's position");
    }

    @Test
    void testStayInBounds() {
        // Set up CC and pirate ship positions
        when(mockGameState.getCcPosition()).thenReturn(new int[]{0, 0});
        int[] piratePosition = new int[]{0, 19};
        when(mockPirateShip.getPosition()).thenReturn(piratePosition);

        // Perform the move
        chaseStrategy.move(mockPirateShip);

        // Verify the pirate ship stayed within bounds
        assertArrayEquals(new int[]{0, 18}, piratePosition, "Pirate ship should stay within bounds");
    }

    private void setGameInstance(Game mockInstance) throws Exception {
        // Use reflection to set the private static instance of Game
        java.lang.reflect.Field instanceField = Game.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockInstance);
    }
}
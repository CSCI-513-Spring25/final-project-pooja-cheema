package com.example.columbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;


public class SlowPirateShipTest {

    private SlowPirateShip slowPirateShip;
    private Game mockGame;
    private GameState mockGameState;

    @BeforeEach
    void setUp() throws Exception {
        // Mock the Game and GameState classes
        mockGame = mock(Game.class);
        mockGameState = mock(GameState.class);

        setGameInstance(mockGame);
        when(mockGame.getState()).thenReturn(mockGameState);

        // Initialize the SlowPirateShip
        slowPirateShip = new SlowPirateShip();
        slowPirateShip.setPosition(new int[]{5, 5}); // Set initial position
    }

    @Test
    void testMoveTowardsCCAbove() {
        // Set CC position above the pirate
        when(mockGameState.getCcPosition()).thenReturn(new int[]{3, 5});
        when(mockGame.isOccupied(any(int[].class))).thenReturn(false);

        // Perform the move
        slowPirateShip.move();

        // Verify the new position
        assertArrayEquals(new int[]{4, 5}, slowPirateShip.getPosition());
    }

    @Test
    void testMoveTowardsCCBelow() {
        // Set CC position below the pirate
        when(mockGameState.getCcPosition()).thenReturn(new int[]{7, 5});
        when(mockGame.isOccupied(any(int[].class))).thenReturn(false);

        // Perform the move
        slowPirateShip.move();

        // Verify the new position
        assertArrayEquals(new int[]{6, 5}, slowPirateShip.getPosition());
    }

    @Test
    void testMoveTowardsCCLeft() {
        // Set CC position to the left of the pirate
        when(mockGameState.getCcPosition()).thenReturn(new int[]{5, 3});
        when(mockGame.isOccupied(any(int[].class))).thenReturn(false);

        // Perform the move
        slowPirateShip.move();

        // Verify the new position
        assertArrayEquals(new int[]{5, 4}, slowPirateShip.getPosition());
    }

    @Test
    void testMoveTowardsCCRight() {
        // Set CC position to the right of the pirate
        when(mockGameState.getCcPosition()).thenReturn(new int[]{5, 7});
        when(mockGame.isOccupied(any(int[].class))).thenReturn(false);

        // Perform the move
        slowPirateShip.move();

        // Verify the new position
        assertArrayEquals(new int[]{5, 6}, slowPirateShip.getPosition());
    }

    @Test
    void testMoveAvoidsOccupiedPosition() {
        // Set CC position below the pirate
        when(mockGameState.getCcPosition()).thenReturn(new int[]{7, 5});
        when(mockGame.isOccupied(new int[]{6, 5})).thenReturn(true); // Mark the position as occupied
        when(mockGame.isOccupied(new int[]{5, 6})).thenReturn(false); // Alternative position is free

        // Perform the move
        slowPirateShip.move();

        // Verify the new position (alternative path)
        assertArrayEquals(new int[]{5, 6}, slowPirateShip.getPosition());
    }

    @Test
    void testGetType() {
        // Verify the type of the ship
        assertEquals("slow", slowPirateShip.getType());
    }

    private void setGameInstance(Game mockInstance) throws Exception {
    Field instanceField = Game.class.getDeclaredField("instance");
    instanceField.setAccessible(true);
    instanceField.set(null, mockInstance);
}
}

package com.example.columbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SeaMonsterTest {

    private SeaMonster seaMonster;
    private Game mockGame;
    private GameState mockGameState;

    @BeforeEach
    public void setUp() throws Exception {
        // Mock the Game and GameState classes
        mockGame = mock(Game.class);
        mockGameState = mock(GameState.class);

        // Use reflection to set the Game singleton instance
        setGameInstance(mockGame);
        when(mockGame.getState()).thenReturn(mockGameState);

        // Initialize the SeaMonster
        seaMonster = new SeaMonster();
        seaMonster.setPosition(new int[]{5, 5}); // Set initial position
    }

    @Test
    public void testMoveMonsterValidPosition() {
        // Mock CC position and ensure no obstacles
        when(mockGameState.getCcPosition()).thenReturn(new int[]{10, 10});
        when(mockGame.isOccupied(any(int[].class))).thenReturn(false);

        // Perform the move
        seaMonster.move();

        // Verify that the sea monster moved to a valid position
        int[] position = seaMonster.getPosition();
        assertTrue(position[0] >= 0 && position[0] <= 19, "X position out of grid bounds");
        assertTrue(position[1] >= 0 && position[1] <= 19, "Y position out of grid bounds");
    }

    @Test
    public void testMoveMonsterAvoidsOccupiedPosition() {
        // Mock CC position and mark some positions as occupied
        when(mockGameState.getCcPosition()).thenReturn(new int[]{10, 10});
        when(mockGame.isOccupied(new int[]{6, 5})).thenReturn(true); // Mark position as occupied
        when(mockGame.isOccupied(new int[]{5, 6})).thenReturn(false); // Alternative position is free

        // Perform the move
        seaMonster.move();

        // Verify that the sea monster moved to an alternative valid position
        int[] position = seaMonster.getPosition();
        assertFalse(mockGame.isOccupied(position), "SeaMonster moved to an occupied position");
    }

    @Test
    public void testGetPosition() {
        // Verify the initial position
        assertArrayEquals(new int[]{5, 5}, seaMonster.getPosition());
    }

    @Test
    public void testSetPosition() {
        // Set a new position
        seaMonster.setPosition(new int[]{8, 8});

        // Verify the new position
        assertArrayEquals(new int[]{8, 8}, seaMonster.getPosition());
    }

    private void setGameInstance(Game mockInstance) throws Exception {
        // Use reflection to set the private static instance of Game
        java.lang.reflect.Field instanceField = Game.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockInstance);
    }
}

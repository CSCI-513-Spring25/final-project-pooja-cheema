package com.example.columbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;

public class SeaMonsterTest {

    private SeaMonster monster;

    // Initialize SeaMonster instance before each test
    @BeforeEach
    public void setUp() {
        monster = new SeaMonster();
        monster.setPosition(new int[]{5, 5});
    }

    // Test: setPosition updates the monster's position correctly
    @Test
    public void testSetPosition() {
        int[] pos = {2, 3};
        monster.setPosition(pos);
        assertArrayEquals(pos, monster.getPosition());
    }

    // Test: Initial position is properly cloned when setPosition is called
    @Test
    public void testInitialPositionCloning() throws Exception {
        int[] newPos = {4, 4};
        monster.setPosition(newPos);

        Field field = SeaMonster.class.getDeclaredField("initialPosition");
        field.setAccessible(true);
        int[] initial = (int[]) field.get(monster);

        assertNotSame(newPos, initial);
        assertArrayEquals(newPos, initial);
    }

    // Test: move() does not crash, can be called without throwing exceptions
    @Test
    public void testMoveDoesNotCrash() {
        monster.move();
    }

    // Test: Monster does not ignore Columbus by default
    @Test
    public void testIgnoreModeInitiallyFalse() {
        assertFalse(monster.isIgnoringColumbus());
    }

    // Test: Monster ctivating the ignore mode works as expected
    @Test
    public void testActivateIgnoreMode() {
        monster.activateIgnoreMode(3);
        assertTrue(monster.isIgnoringColumbus());
    }

    // Test: Ignore mode count decrements correctly and disables when 0
    @Test
    public void testDecrementIgnoreTurns() {
        monster.activateIgnoreMode(2);
        monster.decrementIgnoreTurns();
        assertTrue(monster.isIgnoringColumbus());
        monster.decrementIgnoreTurns();
        assertFalse(monster.isIgnoringColumbus());
    }

    // Test: IgnoreTurns does not go negative after repeated decrements
    @Test
    public void testIgnoreTurnsDoesNotGoNegative() throws Exception {
        monster.activateIgnoreMode(1);
        monster.decrementIgnoreTurns();
        monster.decrementIgnoreTurns();

        Field field = SeaMonster.class.getDeclaredField("ignoreTurns");
        field.setAccessible(true);
        int turns = (int) field.get(monster);
        assertEquals(0, turns);
    }

    // Test: Reactivation of ignore mode overrides existing ignore countdown
    @Test
    public void testMultipleIgnoreActivations() {
        monster.activateIgnoreMode(2);
        monster.decrementIgnoreTurns();
        monster.activateIgnoreMode(5);
        assertTrue(monster.isIgnoringColumbus());
        for (int i = 0; i < 5; i++) {
            monster.decrementIgnoreTurns();
        }
        assertFalse(monster.isIgnoringColumbus());
    }

    // Test: Monster never moves out of bounds (0-19)
    @Test
    public void testMoveWithinBounds() {
        monster.setPosition(new int[]{1, 1});
        monster.move();
        int[] newPos = monster.getPosition();
        assertTrue(newPos[0] >= 0 && newPos[0] < 20);
        assertTrue(newPos[1] >= 0 && newPos[1] < 20);
    }
}
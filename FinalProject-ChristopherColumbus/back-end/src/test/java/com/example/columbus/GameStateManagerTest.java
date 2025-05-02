package com.example.columbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class GameStateManagerTest {

    private GameStateManager gsm;
    private EntityManager em;
    private ObserverManager om;

    @BeforeEach
    public void setup() {
        gsm = new GameStateManager();
        em = new EntityManager(gsm);
        om = new ObserverManager();
        gsm.setEntityManager(em);
    }

    // Test: Columbus starts at position (0, 0)
    @Test
    public void testInitialPosition() {
        int[] pos = gsm.getCcPosition();
        assertArrayEquals(new int[]{0, 0}, pos);
    }

    // Test: resetting the game places Columbus back at (0, 0)
    @Test
    public void testResetPosition() {
        gsm.reset();
        assertArrayEquals(new int[]{0, 0}, gsm.getCcPosition());
    }

    // Test: Prevents Columbus from moving above the top grid boundary
    @Test
    public void testMoveUpFromTopEdge() {
        gsm.getColumbus().setPosition(new int[]{0, 0});
        GameState state = gsm.handleMove("up", em, om);
        assertArrayEquals(new int[]{0, 0}, state.getCcPosition());
    }

    // Test: Columbus moves right from (0, 0) to (0, 1)
    @Test
    public void testMoveRight() {
        gsm.getColumbus().setPosition(new int[]{0, 0});
        GameState state = gsm.handleMove("right", em, om);
        assertArrayEquals(new int[]{0, 1}, state.getCcPosition());
    }

    // Test: treasure is not placed on Columbus’s starting position
    @Test
    public void testTreasureNotOnStart() {
        gsm.reset();
        assertFalse(Arrays.equals(gsm.getCcPosition(), gsm.getState().getTreasurePosition()));
    }

    // Test: collision is reported when Columbus moves into an island
    @Test
    public void testCollisionWithIsland() {
        gsm.reset();
        int[] pos = new int[]{0, 1};
        em.getIslands().clear();
        em.getIslands().add(pos);
        gsm.addOccupied(pos);
        GameState state = gsm.handleMove("right", em, om);
        assertEquals("island", state.getCollisionStatus());
    }

    // Test: monster collision is detected when moving into a monster's cell
    @Test
    public void testCollisionWithMonster() {
        gsm.getColumbus().setPosition(new int[]{5, 5});
        SeaMonster monster = new SeaMonster() {
            @Override
            public int[] getPosition() {
                return new int[]{5, 6};
            }
        };
        em.getMonsters().clear();
        em.getMonsters().add(monster);
        gsm.addOccupied(monster.getPosition());
        GameState state = gsm.handleMove("right", em, om);
        assertEquals("monster", state.getCollisionStatus());
    }

    // Test: pirate collision resets the game and is properly detected
    @Test
    public void testCollisionWithPirate() {
        gsm.getColumbus().setPosition(new int[]{5, 5});
        PirateShip pirate = new SlowPirateShip();
        pirate.setPosition(new int[]{5, 6});
        em.getPirates().clear();
        em.getPirates().add(pirate);
        gsm.addOccupied(pirate.getPosition());
        GameState state = gsm.handleMove("right", em, om);
        assertEquals("pirate", state.getCollisionStatus());
    }

    // Test: Columbus is unwrapped after invisibility cloak expires
    @Test
    public void testUnwrapColumbus() {
        ColumbusShip decorated = new InvisibleColumbusDecorator(new ConcreteColumbusShip(), 1);
        gsm.setColumbus(decorated);
        decorated.decrementCloak();
        gsm.UnwrapColumbus();
        assertFalse(gsm.getColumbus() instanceof InvisibleColumbusDecorator);
    }
}
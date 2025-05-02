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

    @Test
    public void testInitialPosition() {
        int[] pos = gsm.getCcPosition();
        assertArrayEquals(new int[]{0, 0}, pos);
    }

    @Test
    public void testResetPosition() {
        gsm.reset();
        assertArrayEquals(new int[]{0, 0}, gsm.getCcPosition());
    }

    @Test
    public void testMoveUpFromTopEdge() {
        gsm.getColumbus().setPosition(new int[]{0, 0});
        GameState state = gsm.handleMove("up", em, om);
        assertArrayEquals(new int[]{0, 0}, state.getCcPosition());
    }

    @Test
    public void testMoveRight() {
        gsm.getColumbus().setPosition(new int[]{0, 0});
        GameState state = gsm.handleMove("right", em, om);
        assertArrayEquals(new int[]{0, 1}, state.getCcPosition());
    }

    @Test
    public void testTreasureNotOnStart() {
        gsm.reset();
        assertFalse(Arrays.equals(gsm.getCcPosition(), gsm.getState().getTreasurePosition()));
    }

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

    @Test
    public void testUnwrapColumbus() {
        ColumbusShip decorated = new InvisibleColumbusDecorator(new ConcreteColumbusShip(), 1);
        gsm.setColumbus(decorated);
        decorated.decrementCloak();
        gsm.UnwrapColumbus();
        assertFalse(gsm.getColumbus() instanceof InvisibleColumbusDecorator);
    }
}
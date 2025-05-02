package com.example.columbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SlowChaseStrategyTest {

    private SlowChaseStrategy strategy;
    private PirateShip pirate;
    private ColumbusShip cc;
    // private GameStateManager gsm;

    @BeforeEach
    public void setup() {
        strategy = new SlowChaseStrategy();
        pirate = PirateShipFactory.createPirateShip("slow");

        // pirate = new SlowPirateShip();
        cc = new ConcreteColumbusShip();
        // gsm = new GameStateManager();

        Game game = Game.getInstance();
        // game.setGameStateManager(gsm);
        // game.setColumbus(cc);
        game.getGameStateManager().setColumbus(cc);
    }

    @Test
    public void testMoveUp() {
        cc.setPosition(new int[]{10, 10});
        pirate.setPosition(new int[]{15, 10});
        strategy.move(pirate);

        // Should move up from (15, 10) to (14, 10)
        assertArrayEquals(new int[]{14, 10}, pirate.getPosition());
    }

    @Test
    public void testMoveDown() {
        cc.setPosition(new int[]{15, 10});
        pirate.setPosition(new int[]{10, 10});
        strategy.move(pirate);

        // Should move down from (10, 10) to (11, 10)
        assertArrayEquals(new int[]{11, 10}, pirate.getPosition());
    }

    @Test
    public void testMoveLeft() {
        cc.setPosition(new int[]{10, 10});
        pirate.setPosition(new int[]{10, 15});
        strategy.move(pirate);

        // Should move left from (10, 15) to (10, 14)
        assertArrayEquals(new int[]{10, 14}, pirate.getPosition());
    }

    @Test
    public void testMoveRight() {
        cc.setPosition(new int[]{10, 15});
        pirate.setPosition(new int[]{10, 10});
        strategy.move(pirate);

        // Should move right from (10, 10) to (10, 11)
        assertArrayEquals(new int[]{10, 11}, pirate.getPosition());
    }

    @Test
    public void testNoMoveWhenInvisible() {
        ColumbusShip invisible = new InvisibleColumbusDecorator(new ConcreteColumbusShip(), 3);
        invisible.setPosition(new int[]{10, 10});
        // Game.getInstance().setColumbus(invisible);
        Game.getInstance().getGameStateManager().setColumbus(invisible);
        pirate.setPosition(new int[]{15, 10});

        strategy.move(pirate);
        // Should not move when Columbus is invisible
        assertArrayEquals(new int[]{15, 10}, pirate.getPosition());
    }

    @Test
    public void testMoveAtEdgeBottomRight() {
        cc.setPosition(new int[]{19, 18});
        pirate.setPosition(new int[]{19, 19});

        strategy.move(pirate);
        // Should move left to (19, 18)
        assertArrayEquals(new int[]{19, 18}, pirate.getPosition());
    }

    @Test
    public void testMoveWhenDiagonal() {
        cc.setPosition(new int[]{8, 8});
        pirate.setPosition(new int[]{10, 10});

        strategy.move(pirate);
        // Should prefer vertical: move up to (9, 10)
        assertArrayEquals(new int[]{9, 10}, pirate.getPosition());
    }
}
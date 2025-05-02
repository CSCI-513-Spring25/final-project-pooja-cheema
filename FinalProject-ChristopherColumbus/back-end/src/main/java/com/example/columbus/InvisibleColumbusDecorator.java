package com.example.columbus;

/**
 * Decorator class for CC ship
 * Make CC ship invisible to pirates
 */
public class InvisibleColumbusDecorator implements ColumbusShip {
    private ColumbusShip wrappedShip;
    private int remainingTurns;

    public InvisibleColumbusDecorator(ColumbusShip ship, int turns) {
        this.wrappedShip = ship;
        this.remainingTurns = turns;
    }

    @Override
    public int[] getPosition() {
        return wrappedShip.getPosition();
    }

    @Override
    public void setPosition(int[] position) {
        wrappedShip.setPosition(position);
    }

    @Override
    public boolean isInvisible() {
        return remainingTurns > 0;
    }

    @Override
    public void decrementCloak() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    public ColumbusShip unwrapIfExpired() {
        return remainingTurns <= 0 ? wrappedShip : this;
    }
}

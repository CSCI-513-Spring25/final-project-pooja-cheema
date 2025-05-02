package com.example.columbus;

/**
 * Decorator class for CC ship
 * Make CC ship invisible to pirates
 */
public class InvisibleColumbusDecorator implements ColumbusShip {

    private ColumbusShip wrappedShip; // CC ship to be decorated
    private int remainingTurns; // Turns left for invisibility cloak to stay active

    /*
     * Constructs an invisibility decorator over CC ship
     */
    public InvisibleColumbusDecorator(ColumbusShip ship, int turns) {
        this.wrappedShip = ship;
        this.remainingTurns = turns;
    }

    // Delegate position retrieval to wrapped ship
    @Override
    public int[] getPosition() {
        return wrappedShip.getPosition();
    }

    // Delegate position setting to wrapped ship
    @Override
    public void setPosition(int[] position) {
        wrappedShip.setPosition(position);
    }

    // Indicates invisibility status based on remaining turns
    @Override
    public boolean isInvisible() {
        return remainingTurns > 0;
    }

    // Decrease invisibility turns by 1 turn
    @Override
    public void decrementCloak() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    /*
     * If invisibility expired, unwrap and return the original ship.
     * Otherwise, return the decorator (still invisible)
     */
    public ColumbusShip unwrapIfExpired() {
        return remainingTurns <= 0 ? wrappedShip : this;
    }
}

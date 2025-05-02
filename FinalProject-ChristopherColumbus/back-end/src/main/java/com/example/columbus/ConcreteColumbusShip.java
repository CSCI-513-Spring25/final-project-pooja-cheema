package com.example.columbus;

public class ConcreteColumbusShip implements ColumbusShip {
    private int[] position = new int[] {0, 0};

    @Override
    public int[] getPosition() {
        return position;
    }

    @Override
    public void setPosition(int[] position) {
        this.position = position;
    }

    @Override
    public boolean isInvisible() {
        return false; // Default: ship is visible
    }

    @Override
    public void decrementCloak() {
        // Nothing to do in base class
    }
}

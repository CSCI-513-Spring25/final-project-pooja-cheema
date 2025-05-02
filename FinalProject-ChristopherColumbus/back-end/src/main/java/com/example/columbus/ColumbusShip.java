package com.example.columbus;

public interface ColumbusShip {
    int[] getPosition();
    void setPosition(int[] position);
    boolean isInvisible();
    void decrementCloak();
}

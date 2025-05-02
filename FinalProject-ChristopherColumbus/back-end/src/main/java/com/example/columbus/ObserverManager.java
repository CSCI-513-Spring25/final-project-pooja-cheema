package com.example.columbus;

import java.util.*;

/**
 * Manages list of observers that track CC ship
 * Notifies all observers when the CC ship moves
 */
public class ObserverManager {

    // List of all registered observers
    private List<Observer> observers = new ArrayList<>();

    // Adds a new observer to list
    public void addObserver(Observer o) {
        observers.add(o);
    }

    // Notifies all observers of new CC ship position
    public void notifyObservers(int[] ccPosition) {
        for (Observer o : observers) {
            o.update(ccPosition);
        }
    }
}
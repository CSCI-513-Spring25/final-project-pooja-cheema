package com.example.columbus;

import java.util.*;

public class ObserverManager {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void notifyObservers(int[] ccPosition) {
        for (Observer o : observers) {
            o.update(ccPosition);
        }
    }
}
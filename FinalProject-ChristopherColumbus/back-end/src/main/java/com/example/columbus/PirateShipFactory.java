package com.example.columbus;

/**
 * Factory class for creating PirateShip instances based on type.
 * It encapsulates object creation logic
 */
public class PirateShipFactory {

    /*
     * This method creates pirate ship of specified type (fast/slow etc)
     */
    public static PirateShip createPirateShip(String type) {

        if (type.equals("fast")) { 
            return new FastPirateShip(); // Return fast pirate ship
        } 
        else if (type.equals("slow")) {
            return new SlowPirateShip(); // Return slow pirate ship
        }

        else if (type.equals("patrol")) {
            return new PatrolPirateShip(); // Return slow pirate ship
        }
        

        return null; // Return null if type is not recognized
    }
}
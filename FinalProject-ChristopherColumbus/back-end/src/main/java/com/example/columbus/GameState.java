package com.example.columbus;

import com.google.gson.Gson;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the state of game, including all entity positions, 
 * and is responsible for serializing itself to JSON for API responses
 */
public class GameState {

    private int[] ccPosition; // CC position (x,y)
    private int[] treasurePosition; // Treasure position (x,y)
    private List<PirateShipState> pirates; // List of pirate ships' states
    private List<SeaMonster> seaMonsters; // List of sea monsters
    private List<int[]> islands; // Positions of all islands
    private String collision; // Information about collision of CC with island/monster/pirate

    /*
     * GameState constructor to capture current game state
     */
    public GameState(int[] ccPosition, int[] treasurePosition, List<PirateShip> pirates, List<Entity> monsters,
            List<int[]> islands, String collision) {
        this.ccPosition = ccPosition;
        this.treasurePosition = treasurePosition;

        // Convert PirateShip objects to their serializable states (position + type)
        this.pirates = pirates.stream()
                .map(p -> new PirateShipState(p.getPosition(), p.getType()))
                .collect(Collectors.toList());

        // Filter and cast entities to SeaMonster
        this.seaMonsters = monsters.stream()
                .filter(entity -> entity instanceof SeaMonster)
                .map(entity -> (SeaMonster) entity)
                .collect(Collectors.toList());
        this.islands = islands;
        this.collision = collision;

        // Print info for logging
        System.out.println("this.collision: " + collision);
    }

    /*
     * toJson() method is serializing the game state to a JSON string (for API responses)
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Getters for serialization (if needed)
    // CC position
    public int[] getCcPosition() {
        return ccPosition;
    }

    // Treasure position
    public int[] getTreasurePosition() {
        return treasurePosition;
    }

    // Pirate ships
    public List<PirateShipState> getPirates() {
        return pirates;
    }

    //Sea monsters
    public List<SeaMonster> getSeaMonsters() {
        return seaMonsters;
    }

    // Islands
    public List<int[]> getIslands() {
        return islands;
    }

    // Collision information
    public String getCollision() {
        return collision;
    }

    /*
     * This inner class represents the state of a pirate ship
     * It is a serilizable representation of a pirate ship, and
     * is used to send only relevant data to front-end
     */
    public class PirateShipState {
        private int[] position;
        private String type;

        public PirateShipState(int[] position, String type) {
            this.position = position;
            this.type = type;
        }

        public int[] getPosition() {
            return position;
        }

        public String getType() {
            return type;
        }
    }
}
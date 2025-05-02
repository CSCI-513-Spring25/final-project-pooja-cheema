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
    private boolean[][] occupiedPositions; // Grid to track occupied positions
    private boolean columbusInvisible; // When CC invisible

     
    /*
     * GameState constructor to capture current game state
     */
    public GameState(int[] ccPosition, int[] treasurePosition, List<PirateShip> pirates, List<Entity> monsters,
            List<int[]> islands, String collision, ColumbusShip columbus) {
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

        this.columbusInvisible = columbus.isInvisible();

        this.occupiedPositions = new boolean[20][20]; // Initialize the grid
        updateOccupiedPositions(); // Update occupied positions initially
    }

    /*
     * toJson() method is serializing the game state to a JSON string (for API responses)
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Check if CC i invisible
    public boolean isColumbusInvisible() {
        return columbusInvisible;
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


    // Check if a position is occupied
    public boolean isOccupied(int[] position) {
        return occupiedPositions[position[0]][position[1]];
    }

    // Mark a position as occupied
    public void markPositionOccupied(int[] position) {
        occupiedPositions[position[0]][position[1]] = true;
    }

    // Set a position as occupied
    public void setOccupied(int[] position, boolean occupied) {
        occupiedPositions[position[0]][position[1]] = occupied;
    }

    // Check if a position is occupied by a fast pirate
    public boolean isOccupiedByFastPirate(int[] position) {
        for (PirateShipState pirate : pirates) {
            if (pirate.getType().equals("fast") && pirate.getPosition()[0] == position[0] && pirate.getPosition()[1] == position[1]) {
                return true;
            }
        }
        return false;
    }

    // Method to update occupied positions based on current state
    private void updateOccupiedPositions() {
        // Clear the grid
        for (int i = 0; i <20; i++) {
            for (int j = 0; j < 20; j++) {
                occupiedPositions[i][j] = false;
            }
        }

        // Mark positions occupied by pirates
        for (PirateShipState pirate : pirates) {
            occupiedPositions[pirate.getPosition()[0]][pirate.getPosition()[1]] = true;
        }

        // Mark positions occupied by sea monsters
        for (SeaMonster monster : seaMonsters) {
            occupiedPositions[monster.getPosition()[0]][monster.getPosition()[1]] = true;
        }

        // Mark positions occupied by islands
        for (int[] island : islands) {
            occupiedPositions[island[0]][island[1]] = true;
        }

        // Mark position occupied by CC
        occupiedPositions[ccPosition[0]][ccPosition[1]] = true;
    }


    // Method to update the position of a pirate
    public void updatePiratePosition(int[] oldPosition, int[] newPosition) {
        setOccupied(oldPosition, false);
        setOccupied(newPosition, true);
        updateOccupiedPositions();
    }

    // Method to update the position of a sea monster
    public void updateSeaMonsterPosition(int[] oldPosition, int[] newPosition) {
        setOccupied(oldPosition, false);
        setOccupied(newPosition, true);
        updateOccupiedPositions();
    }
    
    public void setCollision(String collision) { 
        this.collision = collision; 
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
package com.example.columbus;

// Import NanoHTTPD server library and other unilities
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.Map;

/**
 * This class extends NanoHTTPD to provide a REST API for Christopher Columbus
 * game.
 * It handles HTTP requests for starting the game, moving ships, and getting
 * game state.
 */
public class ColumbusGameServer extends NanoHTTPD {
    private Game game; // Singleton instance of game logic

    // Constructor to initialize server on given port and start it
    public ColumbusGameServer(int port) throws IOException {

        super(port); // Calling NanoHTTPD constructor within specified port

        game = Game.getInstance(); // Get singleton instance of game

        start(SOCKET_READ_TIMEOUT, false); // Start HTTP server
        System.out.println("Server started on port " + port);
    }

    // Handle incoming HTTP requests
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri(); // Get requested uri
        Method method = session.getMethod(); // Get HTTP method (GET, POST etc)
        Map<String, String> params = session.getParms(); // Get query ot POST parameters

        Response response; // Will hold HTTP response

        // Handle preflight (CORS) requests
        if (method == Method.OPTIONS) {
            response = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "");
        }

        // Handle POST /api/start: start the game
        else if (uri.equals("/api/start") && method == Method.POST) {
            game.start(); // Start the game

            response = newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\":\"Game started\"}");
        }

        // Handle POST /api/reset: fully restart the game
        else if (uri.equals("/api/reset") && method == Method.POST) {
            game.start(); // Restart the game (clear all positions, entities, and regenerate everything)
            response = newFixedLengthResponse(Response.Status.OK, "application/json",
                    "{\"status\":\"Game reset successfully\"}");
        }

        // Handle POST /api/move: move ship in a direction
        else if (uri.equals("/api/move") && method == Method.POST) {
            String direction = params.get("direction");
            GameState result = game.move(direction); // Move player and get new state
            response = newFixedLengthResponse(Response.Status.OK, "application/json", result.toJson());
        }

        else if (uri.equals("/api/state") && method == Method.GET) {
            GameState state = game.getState();
            response = newFixedLengthResponse(Response.Status.OK, "application/json", state.toJson());
        }

        // Handle POST /api/pause: pause monster movement
        else if (uri.equals("/api/pause") && method == Method.POST) {
            // game.stopMonsterMovement();
            game.getMovementController().stopAll();
            response = newFixedLengthResponse(Response.Status.OK, "application/json",
                    "{\"status\":\"Monster movement paused\"}");
        }

        // Handle POST /api/resume: resume monster movement
        else if (uri.equals("/api/resume") && method == Method.POST) {
            // game.startMonsterMovement();
            game.getMovementController().startAll();
            response = newFixedLengthResponse(Response.Status.OK, "application/json",
                    "{\"status\":\"Monster movement resumed\"}");
        }

        // Handle POST /api/resume: clear collision status
        else if (uri.equals("/api/clear-collision") && method == Method.POST) {
            game.setCollisionStatus(null);
            response = newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\":\"cleared\"}");
        }

        else if (uri.equals("/api/toggleStrategy") && method == Method.POST) {
            game.togglePirateStrategies(); // or Game.getInstance().togglePirateStrategies();
            response = newFixedLengthResponse(Response.Status.OK, "application/json",
                    "{\"status\":\"Strategies toggled\"}");
        }

        // Handle GET /api/currentStrategy: return current strategy type
        else if (uri.equals("/api/currentStrategy") && method == Method.GET) {
            String strategy = game.getCurrentStrategy();
            response = newFixedLengthResponse(Response.Status.OK, "application/json",
                    "{\"strategy\":\"" + strategy + "\"}");
        }

        // Handle GET /api/activateInvisibility: Invisibility power to Cc
        else if (uri.equals("/api/activateInvisibility") && method == Method.POST) {
            game.activateInvisibilityCloak();
            response = newFixedLengthResponse(Response.Status.OK, "application/json",
                "{\"status\":\"Cloak activated\"}");
        }
        

        // Handle any other routes: return 404
        else {
            response = newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
        }

        // Add CORS headers for cross-origin access by front-end
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        return response;
    }

    // Main method to start server on port 8080
    public static void main(String[] args) {
        try {
            new ColumbusGameServer(8080); // Start server
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if server fails to start
        }
    }
}
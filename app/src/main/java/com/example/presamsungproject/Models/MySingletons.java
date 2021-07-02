package com.example.presamsungproject.Models;

import android.content.Context;
import com.example.presamsungproject.ConnectionObjects.Client;
import com.example.presamsungproject.ConnectionObjects.Server;

public class MySingletons {
    private static boolean lobby;
    private static MyResources myResources = null;
    private static Server server = null;
    private static Client client = null;
    private static Game game = null;

    public static void createMyResources(Context context) {
        myResources = new MyResources(context);
        myResources.createWallpaper(context);
    }

    public static MyResources getMyResources() {
        return myResources;
    }

    public static void createServer() {
        if (server != null)
            server.stop();
        server = new Server();
        server.start();
    }

    public static Server getServer() {
        return server;
    }

    public static void createClient(String serverIP) {
        if (client != null)
            client.stop();
        client = new Client();
        client.start(serverIP);
    }

    public static Client getClient() {
        return client;
    }

    public static void createGame(String name, GameOptions gameOptions) {
        game = new Game(name, gameOptions);
    }

    public static Game getGame() {
        return game;
    }

    public static void setLobby(boolean isLobby) {
        MySingletons.lobby = isLobby;
    }

    public static boolean isLobby() {
        return lobby;
    }
}

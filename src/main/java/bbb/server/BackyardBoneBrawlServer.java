package bbb.Server;

import bbb.HttpServer.HttpWebServer;


/**
 * @author - Tori
 * this is the main game server
 */
public class BackyardBoneBrawlServer{
    private static final int PORT = 8080;

    public static void main(String[] args) {
        new HttpWebServer(PORT, () -> new BackyardBoneBrawlRequestHandler()).start();
    }
}

package bbb.model.server;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bbb.HttpServer.HttpRequest;
import bbb.HttpServer.HttpRequest.Method;
import bbb.Server.BackyardBoneBrawlRequestHandler;
import bbb.model.GameState;

/**
 * @ author - Tori
 * testing the BBBHandler(INCLUDING error handling)
 */
public class BackyardBoneBrawlHandlerTest {
    private BackyardBoneBrawlRequestHandler handler;

    public void reset(){ //resetting the RequestHandler object before each test
        handler = new BackyardBoneBrawlRequestHandler();
    }


    //testing GET /gamestate
    @Test
    public void testGetGameState(){
        reset();
        HttpRequest request = new HttpRequest(Method.GET, "/gamestate", new HashMap<>(), null);

        String response = handler.handleRequest(request);
        String expectedResponse = GameState.NOT_STARTED.toString();

        assertNotNull(response);
        assertEquals(expectedResponse, response);
    }


    //testing GET /playerYard
    @Test
    public void testGetPlayerYard(){
        reset();
        HttpRequest request = new HttpRequest(Method.GET, "/playeryard", new HashMap<>(), null);
        String response = handler.handleRequest(request);
        System.out.println("Response: [" + response + "]");


        assertNotNull(response);
        assertTrue(response.contains("0"));
        assertTrue(response.contains("A"));
    }


    //testinf GET /cyberpupyard
    @Test
    public void testGetCyberPupYard(){
        reset();

        HttpRequest request = new HttpRequest(Method.GET, "/cyberpupyard", new HashMap<>(), null);
        String response = handler.handleRequest(request);

        assertNotNull(response);
        assertTrue(response.contains("0"));
        assertTrue(response.contains("A"));
    }


    //testing POST /takeTurn
    @Test
    public void testTakeTurnValid(){
        reset();
        HttpRequest request = new HttpRequest(Method.POST, "/taketurn", new HashMap<>(), "A0");
        String response = handler.handleRequest(request);

        assertNotNull(response);
        assertTrue(response.contains("PLAYER_DIG_COORDINATE: A0"));
        assertTrue(response.contains("PLAYER_DIG_RESULT"));
    }


    //testing invalid coordinate
    @Test
    public void testTakeTurnInvalidCoordinate(){
        reset();
        HttpRequest request = new HttpRequest(Method.POST, "/taketurn", new HashMap<>(), "Z9");
        String response = handler.handleRequest(request);

        assertNotNull(response);
        assertTrue(response.contains("PLAYER_DIG_RESULT: INVALID"));
    }


    //testing invalid uri
    @Test
    public void testInvalidUri(){
        reset();
        HttpRequest request = new HttpRequest(Method.POST, "/invalid", new HashMap<>(), "");
        String response = handler.handleRequest(request);
        String expected = "ERROR: Unknown request";

        assertEquals(expected, response);
    }


    //testing GET turnHistory
    @Test
    public void testTurnHistory(){
        reset();

        //taking two turns to create the turnHistory
        HttpRequest turn1 = new HttpRequest(Method.POST, "/taketurn", new HashMap<>(), "A0");
        handler.handleRequest(turn1);

        HttpRequest turn2 = new HttpRequest(Method.POST, "/taketurn", new HashMap<>(), "B1");
        handler.handleRequest(turn2);

        //getting the turnHistory
        HttpRequest historyRequest = new HttpRequest(Method.GET, "/getturnhistory", new HashMap<>(), null);
        String response = handler.handleRequest(historyRequest);

        assertNotNull(response);
        assertEquals(response.charAt(0), '['); //shoudl be starting with a bracket
        assertEquals(response.charAt(response.length() - 1), ']'); //should be ending with a bracket

        //testing to make sure turnHistory has two entries
        String content = response.substring(1, response.length() - 1); //getting the stuff inbetween brackets
        assertFalse(content.isEmpty());

        String[] entries = content.split(","); //getting the separate entries
        assertEquals(entries.length, 2); //checking if there are two entries
    }
}

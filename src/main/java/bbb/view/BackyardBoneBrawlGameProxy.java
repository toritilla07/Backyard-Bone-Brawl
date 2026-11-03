package bbb.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import bbb.HttpServer.HttpRequestBuilder;
import bbb.HttpServer.HttpResponse;
import bbb.HttpServer.HttpResponseParser;
import bbb.HttpServer.HttpStream;
import bbb.model.BackyardBoneBrawlGame;
import bbb.model.DigResult;
import bbb.model.GameState;
import bbb.model.Symbol;
import bbb.model.TurnResult;
import bbb.model.YardCoordinate;


/**
 * @ author - Tori, Landon, Elijah
 * provides a network interface for TopDog, while not exposing the implementation
 */
public class BackyardBoneBrawlGameProxy implements BackyardBoneBrawlGame {
    private final String sessionId; //makes it possible for there to be multiple HTTP clients 
    private Socket socket;
    private PrintWriter writer;
    private InputStream inputStream;
    HttpStream httpStream;


    //constructor for the proxy
    public BackyardBoneBrawlGameProxy(String host, int port) {
        this.sessionId = UUID.randomUUID().toString();
        try {
            this.socket = new Socket(host, port);
            this.writer = new PrintWriter(socket.getOutputStream());
            this.inputStream = socket.getInputStream();
            this.httpStream = new HttpStream(inputStream);
        } catch (IOException e) { e.printStackTrace(); }
    }


    /**
     * each method is converting the model objects passed as parameters into HTTP requests,
     * then it sends the requests to the server
     * the method is then converting the HTTP response body strings received from the server
     * into model objects and return them to the caller
     */ 
    @Override
    public TurnResult takeTurn(YardCoordinate playerDigCoordinate) {
        String uri = "/taketurn";
        String body = "" + playerDigCoordinate.getRow() + playerDigCoordinate.getColumn();
        String request = HttpRequestBuilder.buildPostRequest(uri, sessionId, body);

        writer.print(request);
        writer.flush();

        try{
            HttpResponse response = HttpResponseParser.parseResponse(httpStream);
            String bodyText = response.getBody();

            // Parsing the response; Format is made by handler and broken up by newline spaces (\n) ; Using a stream of the body's lines => Array
            String[] lines = bodyText.split("\n");
            
            // Array should be formatted [label: coord][label: coord][label: coord][label: coord]
            // Do not need to parse for playerDigCoordinate, was provided in initial request.
            DigResult playerDigResult = parseDigResult(lines[1].split(": ")[1]);
            
            String cyberCoord = lines[2].split(": ")[1];
            char cyberRow = cyberCoord.charAt(0);
            int cyberCol = Integer.parseInt(cyberCoord.substring(1));

            YardCoordinate cyberPupDigCoordinate = new YardCoordinate(cyberRow, cyberCol); //turning the row and column into YardCoordinates
            
            DigResult cyberPupDigResult = parseDigResult(lines[3].split(": ")[1]);
            return new TurnResult(playerDigCoordinate, playerDigResult, cyberPupDigCoordinate, cyberPupDigResult);
        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }


    /**
     * Helper method specifically to parse response packets/frames in takeTurn();
     *      Utilizes a switch case that checks for a valid result (or returns null if/when applicable).
     * Returns a working DigResult object
     */
    private DigResult parseDigResult(String strResult){
        switch(strResult){
            case("TOY_PART"):
                return DigResult.TOY_PART;
            case("NOTHING"):
                return DigResult.NOTHING;
            case("FULL_TOY"):
                return DigResult.FULL_TOY;
            case("INVALID"):
                return DigResult.INVALID;
            default:
                return null;
        }
    }



    /**
     * overrides the getKnownPlayerYard, returning a string version of the HTTPResponse of the knownPlayerYard
     */
    @Override
    public String getPlayerYard() {
        String uri = "/playeryard";
        String request = HttpRequestBuilder.buildGetRequest(uri, sessionId); //converting the request to a string

        writer.print(request); //sending the request out through the writer
        writer.flush();
        
        try {
            String response = HttpResponseParser.parseResponse(httpStream) + ""; //turning the response into a string 
            return response;

        } catch (IOException e) { e.printStackTrace(); }

        return null;        
    }



    /**
     * overrides the getCyberPupYard, returning a string version of the HTTPResponse of the cyberPupYard
     */
    @Override
    public String getCyberPupYard() {
        String uri = "/cyberpupyard";
        String request = HttpRequestBuilder.buildGetRequest(uri, sessionId); //converting the request to a string

        writer.print(request); //sending the request out through the writer
        writer.flush();
        
        try {
            String response = HttpResponseParser.parseResponse(httpStream) + ""; //turning the response into a string 
            return response;

        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }



    /**
     * overrides the getGameState, returning a string version of the HTTPResponse of the getGameState
     */
    @Override
    public GameState getGameState() {
        String uri = "/gamestate";
        String request = HttpRequestBuilder.buildGetRequest(uri, sessionId); //converting the request to a string

        writer.print(request); //sending the request out through the writer
        writer.flush();

        HttpResponse response;
        try {
            response = HttpResponseParser.parseResponse(httpStream); //turning the response into a string 
            return GameState.valueOf(response.getBody());

        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }



    /**
     * converting the string representation of the yard() from the server into a 2D array
     * @param body - the response returned from the server
     * @return a 2D array version of the string representation of the yard
     */
    private Symbol[][] parseYard(String body){
        String[] rows = body.split("\n"); //creating an array of rows
        int numRow = rows.length; //getting the number of rows in the yard
        int numCol = rows[0].split(" ").length; //gettign the number of columns in the yard

        Symbol[][] yard = new Symbol[numRow][numCol]; //initializing a yard

        for(int row = 0; row < numRow; row ++){ //looping through the rows
            String[] cols = rows[row].split(" "); //splitting the row into individual tokens

            for(int col = 0; col < numCol; col ++){ //looping through the columns
                yard[row][col] = Symbol.valueOf(cols[col]); //converting each symbol into the enum constant
            }
        }

        return yard;
    }



    /**
     * overrides the getPlayerVisibleYard, returning a 2D array of the symbols returned from the server
     */
    @Override
    public Symbol[][] getPlayerVisibleYard() {
        String uri = "/getplayervisibleyard";
        String request = HttpRequestBuilder.buildGetRequest(uri, sessionId); //converting the request to a string

        writer.print(request); //sending the request out through the writer
        writer.flush();

        HttpResponse response;
        try {
            response = HttpResponseParser.parseResponse(httpStream); //turning the response into a string 
            String body = response.getBody();
            return parseYard(body);

        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }



    /**
     * overrides the getCyberPupHiddenYard, returning a 2D array of the symbols returned from the server
     */
    @Override
    public Symbol[][] getCyberPupHiddenYard() {
        String uri = "/getcyberpuphiddenyard";
        String request = HttpRequestBuilder.buildGetRequest(uri, sessionId); //converting the request to a string

        writer.print(request); //sending the request out through the writer
        writer.flush();

        HttpResponse response;
        try {
            response = HttpResponseParser.parseResponse(httpStream); //turning the response into a string 
            String body = response.getBody();
            return parseYard(body);

        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }

    

    /**
     * overrides the getCyberPupVisibleYard, returning a 2D array of the symbols returned from the server
     */
    @Override
    public Symbol[][] getCyberPupVisibleYard() {
        String uri = "/getcyberpupvisibleyard";
        String request = HttpRequestBuilder.buildGetRequest(uri, sessionId); //converting the request to a string

        writer.print(request); //sending the request out through the writer
        writer.flush();

        HttpResponse response;
        try {
            response = HttpResponseParser.parseResponse(httpStream); //turning the response into a string 
            String body = response.getBody();
            return parseYard(body);

        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }


    private List<TurnResult> parseTurnHistory(String body){
        List<TurnResult> turnHistory = new LinkedList<>();

        //removing the brackets from the body
        body = body.replace("[", "");
        body = body.replace("]", "");

        //if the turnHistory is empty then return the empty list
        if(body.isEmpty()){
            return turnHistory;
        }

        //splitting the body into each turn entry
        String[] entries = body.split(",");

        for(String entry : entries){
            //splitting entry into playerCoord, playerResult, cyberCoord, and cyberResult
            String[] tokens = entry.split("");

            char playerRow = tokens[0].charAt(0); //getting the row of the turn
            int playerCol = Integer.parseInt(tokens[0].substring(1)); //getting the column of the turn

            YardCoordinate plyayerCoord = new YardCoordinate(playerRow, playerCol);
            DigResult playerRes = DigResult.valueOf(tokens[1]); //getting the dig result

            //cyberpup could have not had a turn so set the cyberpup yardcoord and digresult to null and invalid
            YardCoordinate cyberCoord = null;
            DigResult cyberRes = DigResult.INVALID;

            //if cyberPup did have a turn, over-write the null and invalid with the real data
            if(tokens.length == 4){ 
                char cyberRow = tokens[2].charAt(0);
                int cyberCol = Integer.parseInt(tokens[2].substring(1));

                cyberCoord = new YardCoordinate(cyberRow, cyberCol);
                cyberRes = DigResult.valueOf(tokens[3]);

            }

            //adding the entry in correct formatting to the turnHistory list
            turnHistory.add(new TurnResult(plyayerCoord, playerRes, cyberCoord, cyberRes));
        }

        //after all entries have been formatted into the list, return the list
        return turnHistory;
    }


    /**
     * overrides the getTurnHistory, returning a list of TurnResults returned from the server
     */
    @Override
    public List<TurnResult> getTurnHistory() {
        String uri = "/getturnhistory";
        String request = HttpRequestBuilder.buildGetRequest(uri, sessionId); //converting the request to a string

        writer.print(request); //sending the request out through the writer
        writer.flush();

        HttpResponse response;
        try {
            response = HttpResponseParser.parseResponse(httpStream); //turning the response into a string 
            String body = response.getBody();
            return parseTurnHistory(body);

        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }

    /**
     * overrides the getTurnHistory, returning a list of TurnResults returned from the server
     */
    @Override
    public int getTurnCount() {
        String uri = "/getturncount";
        String request = HttpRequestBuilder.buildGetRequest(uri, sessionId); //converting the request to a string

        writer.print(request); //sending the request out through the writer
        writer.flush();

        HttpResponse response;
        try {
            response = HttpResponseParser.parseResponse(httpStream); //turning the response into a string 
            int body = Integer.parseInt(response.getBody());
            return body;

        } catch (IOException e) { e.printStackTrace(); }

        return -1;
    }
}
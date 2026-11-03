package bbb.Server;

import java.util.Arrays;

import bbb.HttpServer.HttpRequest;
import bbb.HttpServer.RequestHandler;
import bbb.model.BackyardBoneBrawlGame;
import bbb.model.BackyardBoneBrawlGameImpl;
import bbb.model.TurnResult;
import bbb.model.YardCoordinate;


/**
 * @author - Tori, Landon
 * handles each of the requests passed in by the HttpWebServer
 */
public class BackyardBoneBrawlRequestHandler implements RequestHandler{
    private final BackyardBoneBrawlGame game; //creating a game object

    public BackyardBoneBrawlRequestHandler(){
        this.game = new BackyardBoneBrawlGameImpl();
    }


    @Override
    public String handleRequest(HttpRequest request) {
        String response = "";
        
        //if the request is a get request (uri switch).
        if(request.getMethod() == HttpRequest.Method.GET){
            switch(request.getUri()){
                case "/gamestate": //if the request is a get request and the uri is /gamestate
                    response = game.getGameState().toString();
                    break;

                case "/playeryard": //if the request is a get request and the uri is /playeryard
                    response = game.getPlayerYard();
                    break;

                case "/cyberpupyard": //if the request is a get request and the uri is /cyberpupyard
                    response = game.getCyberPupYard();
                    break;

                case "/getplayervisibleyard":
                    response = Arrays.toString(game.getPlayerVisibleYard());
                    break;

                case "/getcyberpuphiddenyard":
                    response = Arrays.toString(game.getCyberPupHiddenYard());
                    break;

                case "/getturnhistory":
                    response = game.getTurnHistory().toString();
                    break;
            }
         //if the request is a post request and the uri is /taketurn
        } else if((request.getMethod() == HttpRequest.Method.POST) && (request.getUri().equals("/taketurn"))){
            String body = request.getBody();
            YardCoordinate coord = new YardCoordinate(body);
            TurnResult result = game.takeTurn(coord);

            StringBuilder stringBuild = new StringBuilder(); //will be used to format the response
            
            stringBuild.append("PLAYER_DIG_COORDINATE: ") //adding PLAYER_DIG_COORDINATE string to the returned string
                        .append(result.getPlayerDigCoordinate().getRow()) //adding the row the player dug in
                        .append(result.getPlayerDigCoordinate().getColumn()) //adding the column the player dug in
                        .append("\n"); //adding a new line

            stringBuild.append("PLAYER_DIG_RESULT: ") //adding PLAYER_DIG_RESULT string to the returned string
                        .append(result.getPlayerDigResult()) //adding the player's dig result
                        .append("\n"); //adding a new line

            //cyberpup is only going to dig if the player's result was valid
            if(result.getCyberPupDigCoordinate() != null){
                stringBuild.append("CYBERPUP_DIG_COORDINATE: ") //adding CYBERPUP_DIG_COORDINATE string to the returned string
                            .append(result.getCyberPupDigCoordinate().getRow()) //adding the row the player dug in
                            .append(result.getCyberPupDigCoordinate().getColumn()) //adding the column the player dug in
                            .append("\n");

                stringBuild.append("CYBERPUP_DIG_RESULT: ") //adding CYBERPUP_DIG_RESULT string to the returned string
                        .append(result.getCyberPupDigResult()) //adding the cyberpup's dig result
                        .append("\n"); //adding a new line
            }

            response = stringBuild.toString();
        } else {
            response = "ERROR: Unknown request";
        }

        return response;
    }


}

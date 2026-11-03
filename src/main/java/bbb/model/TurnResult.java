package bbb.model;

/**
 * @author Tori, Landon
 * simulates taking a turn in the game
 */
public class TurnResult{
    private final YardCoordinate playerDigCoordinate;//represents the yard coordinate the player is digging at
    private final DigResult playerDigResult; //representing the result of the player's dig

    private final YardCoordinate cyberPupDigCoordinate;//represents the yard coordinate the cyberpup is digging at
    private final DigResult cyberPupDigResult;//representing the result of the cyber pup's dig


    /**
     * creating the result of either the player or the cyberpup's turn
     * @param playerDigCoordinate - represents the yard coordinate the player is digging at
     * @param playerDigResult - representing the result of the player's dig
     * @param cyberPupDigCoordinate - represents the yard coordinate the cyberpup is digging at
     * @param cyberPupDigResult - representing the result of the cyber pup's dig
     */
    public TurnResult(YardCoordinate playerDigCoordinate, DigResult playerDigResult, YardCoordinate cyberPupDigCoordinate, DigResult cyberPupDigResult){
        this.playerDigCoordinate = playerDigCoordinate;
        this.playerDigResult = playerDigResult;

        this.cyberPupDigCoordinate = cyberPupDigCoordinate;
        this.cyberPupDigResult = cyberPupDigResult;
    }

    /**
     * Accessor methods
     */
    public YardCoordinate getPlayerDigCoordinate(){
        return playerDigCoordinate;
    }

    public DigResult getPlayerDigResult(){
        return playerDigResult;
    }

    public YardCoordinate getCyberPupDigCoordinate(){
        return cyberPupDigCoordinate;
    }

    public DigResult getCyberPupDigResult(){
        return cyberPupDigResult;
    }
}

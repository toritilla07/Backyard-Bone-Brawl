package bbb.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author Tori, Elijah, Landon
 * implements the core game logic, control, and state management
 */
public class BackyardBoneBrawlGameImpl implements BackyardBoneBrawlGame {
    private static final int GRID_SIZE = 10; //represents the size of the grid
    
    private final Yard playerYard; //represents the player's yard
    private final Yard knownPlayerYard; //represents the player yard that the cyber pup has uncovered
    private final Yard cyberPupYard; //represents the cyberpup's yard, under the hood
    private final Yard knownPupYard; //represents the cyberpup's yard being displayed

    private final Symbol[][] originalPlayerYard;

    private GameState gameState;
    private final Random RNG = new Random(); //creating a random object for the Cyberpup's coordinates later

    private final List<TurnResult> turnHistory; //a list of the turnHistory
    private int turnCount;
    
    public BackyardBoneBrawlGameImpl(){
        playerYard = new Yard(GRID_SIZE, true, false);
        knownPlayerYard = new Yard(GRID_SIZE, false, true);
        cyberPupYard = new Yard(GRID_SIZE, false, false);
        knownPupYard = new Yard(GRID_SIZE, false, true);

        // Copy of the playerYard so it DOES NOT CHANGE. this is so the smart dig can check if the toy has already been fully dug by not replacing the toys with '@'
        Symbol[][] original = playerYard.getYard();
        originalPlayerYard = new Symbol[GRID_SIZE][GRID_SIZE];
        for(int i = 0; i < GRID_SIZE; i++) {
            for(int j = 0; j < GRID_SIZE; j++) {
                originalPlayerYard[i][j] = original[i][j];
            }
        }

        gameState = GameState.NOT_STARTED;
        this.turnHistory = new LinkedList<>();
        this.turnCount = 0;
    }

    /**
    * Creates a blank yard array filled with only grass symbols
    * so players only see where they have dug.
    * @param yard - the yard being made blank
    */
   public void createBlankYard(Yard yard){
        Symbol[][] blankYardArray = yard.getYard();

        for(int row = 0; row < GRID_SIZE; row ++){ //looping through rows
            for(int col = 0; col < GRID_SIZE; col ++){ //looping through columns
                blankYardArray[row][col] = Symbol.GRASS; //setting symbol to be grass
            }
        }
   }

   //Looks through every spot of the hidden and visible yards. If the part is not grass (aka toy) in the hidden yard but is still covered by grass in the visible yard,
   //the method will return false. Otherwise, if every part is uncovered in the visible yard it will return true.
   public boolean allToysFound(Symbol[][] hidden, Symbol[][] visible) {
        for(int i = 0; i < GRID_SIZE; i++){
            for(int j = 0; j < GRID_SIZE; j++){
                if(hidden[i][j] != Symbol.GRASS) {
                    if(visible[i][j] == Symbol.GRASS){
                        return false;
                    }
                }
            }
        }
        return true;
   }
    
    /**
     * executes the player's dig at the given coordinates, 
     * if the dig is valid and the game isn't over then the cyber pup will take its turn
     */
    @Override
    public TurnResult takeTurn(YardCoordinate playerDigCoordinate) {
        //if the player of the cyberpup won then just return the turnresult now, no one takes their turn
        if(gameState == GameState.PLAYER_WON || gameState == GameState.CYBERPUP_WON){
            return new TurnResult(playerDigCoordinate, DigResult.INVALID, null, DigResult.INVALID);
        }

        char playerRowChar = playerDigCoordinate.getRow(); //storing what row the player dug at
       
        int playerRow = playerRowChar - 'A'; //converting from a char to an int
        int playerCol = playerDigCoordinate.getColumn(); //storing what column the player dug at

        DigResult playerDigResult;

        // bounds check
        if(playerRow < 0 || playerRow >= GRID_SIZE || playerCol < 0 || playerCol >= GRID_SIZE){
            playerDigResult = DigResult.INVALID; //set the result to be invalid and return the turnResult now
            TurnResult turnResult = new TurnResult(playerDigCoordinate, playerDigResult, null, DigResult.INVALID);

            return turnResult;
        }

        Symbol[][] cyberHidden = cyberPupYard.getYard(); //represents the hidden syls(for cyberpup yard)
        Symbol[][] cyberVisible = knownPupYard.getYard(); //represents the shown symbols(for cyberpup yard)
        Symbol[][] playerHiddenToPup = playerYard.getYard(); //represents the hidden symbols(for player yard)
        Symbol[][] playerVisibleToPup = knownPlayerYard.getYard(); //represents the shown symbols(for player yard)
        

        //if the spot has been dug already
        if(cyberVisible[playerRow][playerCol] != Symbol.GRASS){
            playerDigResult = DigResult.INVALID; //set the result to be invalid and return the turnResult now
            TurnResult turnResult = new TurnResult(playerDigCoordinate, playerDigResult, null, DigResult.INVALID);

            return turnResult;
        }

        Symbol hidden = cyberHidden[playerRow][playerCol]; //the symbol that is at the dig spot in cyberpup's yard
        
        if(hidden == Symbol.GRASS){ //if the player digs grass
            cyberVisible[playerRow][playerCol] = Symbol.HOLE;
            playerDigResult = DigResult.NOTHING; //the visible symbol in cyberpup yard becomes a hole
        
        } else { //otherwise if the player dug up a toy
            cyberVisible[playerRow][playerCol] = Symbol.PART; //reveal the toy
            playerDigResult = DigResult.TOY_PART; //set the player dig result to the toy part they dug
            Symbol completedSymbol = toyCompletionCheck(hidden);

            if (completedSymbol != Symbol.PART) {
                cyberVisible[playerRow][playerCol] = completedSymbol;
                playerDigResult = DigResult.FULL_TOY;
            }
        }

        //checking if the player just won
        if(allToysFound(cyberHidden, cyberVisible)){
            gameState = GameState.PLAYER_WON; //set the gameState to player won
            TurnResult turnResult = new TurnResult(playerDigCoordinate, playerDigResult, null, DigResult.INVALID); //return the turnresult now

            turnHistory.add(turnResult); //adding the turnResult to the history of turnResults
            turnCount++;
            return turnResult;
        }
        



        //cyberpup needs to take their turn
        YardCoordinate cyberPupDigCoordinate = null; //setting default coordinate to null
        DigResult cyberPupDigResult = DigResult.INVALID; //setting default dig result to invalid
        int pupRow;
        int pupCol;
        boolean hasDug = false; // flag to ensure cyber pup only digs once per turn

        //If the last dig was a miss but the two before that were hits and the toy hasnt been fully uncovered go back to before the first hit and go the opposite way
        if(!hasDug && turnHistory.size() > 2 
                && turnHistory.getLast().getCyberPupDigResult() == DigResult.NOTHING 
                && turnHistory.get(turnCount - 2).getCyberPupDigResult() == DigResult.TOY_PART 
                && turnHistory.get(turnCount - 3).getCyberPupDigResult() == DigResult.TOY_PART 
                && originalPlayerYard[turnHistory.get(turnCount - 2).getCyberPupDigCoordinate().getRow() - 65][turnHistory.get(turnCount - 2).getCyberPupDigCoordinate().getColumn()] == originalPlayerYard[turnHistory.get(turnCount - 3).getCyberPupDigCoordinate().getRow() - 65][turnHistory.get(turnCount - 3).getCyberPupDigCoordinate().getColumn()] 
                && !toyIsCompletedFORPUP(originalPlayerYard[turnHistory.get(turnCount - 2).getCyberPupDigCoordinate().getRow() - 65][turnHistory.get(turnCount - 2).getCyberPupDigCoordinate().getColumn()])) {


            int lastRow = turnHistory.get(turnCount - 2).getCyberPupDigCoordinate().getRow() - 65;
            int lastCol = turnHistory.get(turnCount - 2).getCyberPupDigCoordinate().getColumn();
            int firstRow = 0;
            int firstCol = 0;
            for(TurnResult result : turnHistory) {
                if(originalPlayerYard[result.getCyberPupDigCoordinate().getRow() - 65][result.getCyberPupDigCoordinate().getColumn()] == originalPlayerYard[turnHistory.get(turnCount - 2).getCyberPupDigCoordinate().getRow() - 65][turnHistory.get(turnCount - 2).getCyberPupDigCoordinate().getColumn()]) {
                    firstRow = result.getCyberPupDigCoordinate().getRow() - 65;
                    firstCol = result.getCyberPupDigCoordinate().getColumn();
                    break;
                }
            }

            if(firstRow < lastRow) {
                pupRow = firstRow - 1;
                pupCol = firstCol;

            } else if(firstRow > lastRow) {
                pupRow = firstRow + 1;
                pupCol = firstCol;

            } else if(firstCol < lastCol) {
                pupRow = firstRow;
                pupCol = firstCol - 1;

            } else {
                pupRow = firstRow;
                pupCol = firstCol + 1;
            }

            boolean canContinue = true;
            if(pupRow < 0 || pupRow >= GRID_SIZE || pupCol < 0 || pupCol >= GRID_SIZE) {
                canContinue = false;
            }

            if(canContinue && playerVisibleToPup[pupRow][pupCol] == Symbol.GRASS) {
                Symbol hiddenSymbol = playerHiddenToPup[pupRow][pupCol];

                if(hiddenSymbol == Symbol.GRASS){
                    cyberPupDigResult = DigResult.NOTHING;
                    playerVisibleToPup[pupRow][pupCol] = Symbol.HOLE;
                    playerHiddenToPup[pupRow][pupCol] = Symbol.HOLE;
                } else {
                    cyberPupDigResult = DigResult.TOY_PART;
                    playerVisibleToPup[pupRow][pupCol] = Symbol.PART;
                    playerHiddenToPup[pupRow][pupCol] = Symbol.PART;
                }

                cyberPupDigCoordinate = new YardCoordinate((char)(pupRow + 'A'), pupCol);
                hasDug = true;
            }


        } else {
            for(int i = 2; i < 5 && !hasDug; i++) {
                //if the previous dig was a miss but the turn before that was a hit it dig around the one before to see if there are hits around it
                if(turnHistory.size() > i - 1 
                        && turnHistory.getLast().getCyberPupDigResult() == DigResult.NOTHING 
                        && turnHistory.get(turnCount - i).getCyberPupDigResult() == DigResult.TOY_PART 
                        && !toyIsCompletedFORPUP((originalPlayerYard[turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getRow() - 65][turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getColumn()]))) {
                    
                    int attempt = 0;
                    while(true) {
                        switch(attempt) {
                            case(0):
                                //we subtract 65 to get the row number from the row letter and then subtract another 1 to get the space one row above
                                pupRow = turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getRow() - 66;
                                pupCol = turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getColumn();
                                break;
                            
                            case(1):
                                //we subtract 65 to get the row number from the row letter
                                pupRow = turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getRow() - 65;
                                pupCol = turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getColumn() + 1;
                                break;

                            case(2):
                                //we subtract 65 to get the row number from the row letter and then add another 1 to get the space one row below
                                pupRow = turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getRow() - 64;
                                pupCol = turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getColumn();
                                break;

                            case(3):
                                //we subtract 65 to get the row number from the row letter
                                pupRow = turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getRow() - 65;
                                pupCol = turnHistory.get(turnCount - i).getCyberPupDigCoordinate().getColumn() - 1;
                                break;
                            
                            default:
                                pupRow = RNG.nextInt(GRID_SIZE);
                                pupCol = RNG.nextInt(GRID_SIZE);
                        }
                        
                        //this makes sure the guess isn't out of range
                        if(pupRow < 0 || pupRow >= GRID_SIZE || pupCol < 0 || pupCol >= GRID_SIZE) {
                            attempt++;
                            continue;
                        }

                        if(playerVisibleToPup[pupRow][pupCol] == Symbol.GRASS) {
                            Symbol hiddenSymbol = playerHiddenToPup[pupRow][pupCol];

                            if(hiddenSymbol == Symbol.GRASS){
                                cyberPupDigResult = DigResult.NOTHING;
                                playerVisibleToPup[pupRow][pupCol] = Symbol.HOLE;
                                playerHiddenToPup[pupRow][pupCol] = Symbol.HOLE;
                            } else {
                                cyberPupDigResult = DigResult.TOY_PART;
                                playerVisibleToPup[pupRow][pupCol] = Symbol.PART;
                                playerHiddenToPup[pupRow][pupCol] = Symbol.PART;
                            }

                            cyberPupDigCoordinate = new YardCoordinate((char)(pupRow + 'A'), pupCol);
                            hasDug = true;
                            break;
                        }

                        attempt++;
                    }
                }
            }
        }



        //cyber pup will next next to last dig if part of a toy was dug up.
        //It will also dig along the same axis if two parts were found.
        if(!hasDug && !turnHistory.isEmpty() && turnHistory.getLast().getCyberPupDigResult() == DigResult.TOY_PART 
                && !toyIsCompletedFORPUP((originalPlayerYard[turnHistory.getLast().getCyberPupDigCoordinate().getRow() - 65][turnHistory.getLast().getCyberPupDigCoordinate().getColumn()]))) {
            
            boolean moreThanOneHitTogether = false;

            int upRow    = turnHistory.getLast().getCyberPupDigCoordinate().getRow() - 66;
            int downRow  = turnHistory.getLast().getCyberPupDigCoordinate().getRow() - 64;
            int sameRow  = turnHistory.getLast().getCyberPupDigCoordinate().getRow() - 65;
            int sameCol  = turnHistory.getLast().getCyberPupDigCoordinate().getColumn();

            if(upRow >= 0 && playerVisibleToPup[upRow][sameCol] == Symbol.PART) {
                if(downRow < GRID_SIZE && playerVisibleToPup[downRow][sameCol] != Symbol.HOLE) {
                    pupRow = downRow;
                    pupCol = sameCol;
                } else {
                    pupRow = upRow - 1;
                    pupCol = sameCol;
                }

                moreThanOneHitTogether = true;

            } else if(sameCol + 1 < GRID_SIZE && playerVisibleToPup[sameRow][sameCol + 1] == Symbol.PART) {
                if(sameCol - 1  >= 0 && playerVisibleToPup[sameRow][sameCol - 1] != Symbol.HOLE) {
                    pupRow = sameRow;
                    pupCol = sameCol - 1;
                } else {
                    pupRow = sameRow;
                    pupCol = sameCol + 2;
                }

                moreThanOneHitTogether = true;

            } else if(downRow < GRID_SIZE && playerVisibleToPup[downRow][sameCol] == Symbol.PART) {
                if(upRow >= 0 && playerVisibleToPup[upRow][sameCol] != Symbol.HOLE) {
                    pupRow = upRow;
                    pupCol = sameCol;
                } else {
                    pupRow = downRow + 1;
                    pupCol = sameCol;
                }

                moreThanOneHitTogether = true;

            } else if(sameCol - 1 >= 0 && playerVisibleToPup[sameRow][sameCol - 1] == Symbol.PART) {
                if(sameCol + 1 < GRID_SIZE && playerVisibleToPup[sameRow][sameCol + 1] != Symbol.HOLE) {
                    pupRow = sameRow;
                    pupCol = sameCol + 1;
                } else {
                    pupRow = sameRow;
                    pupCol = sameCol - 2;
                }

                moreThanOneHitTogether = true;
            
            } else {
                int attempt = 0;
                while(true) {
                    switch(attempt) {
                        case(0):
                            //we subtract 65 to get the row number from the row letter and then subtract another 1 to get the space one row above
                            pupRow = turnHistory.getLast().getCyberPupDigCoordinate().getRow() - 66;
                            pupCol = turnHistory.getLast().getCyberPupDigCoordinate().getColumn();
                            break;
                        
                        case(1):
                            //we subtract 65 to get the row number from the row letter
                            pupRow = turnHistory.getLast().getCyberPupDigCoordinate().getRow() - 65;
                            pupCol = turnHistory.getLast().getCyberPupDigCoordinate().getColumn() + 1;
                            break;

                        case(2):
                            //we subtract 65 to get the row number from the row letter and then add another 1 to get the space one row below
                            pupRow = turnHistory.getLast().getCyberPupDigCoordinate().getRow() - 64;
                            pupCol = turnHistory.getLast().getCyberPupDigCoordinate().getColumn();
                            break;

                        case(3):
                            //we subtract 65 to get the row number from the row letter
                            pupRow = turnHistory.getLast().getCyberPupDigCoordinate().getRow() - 65;
                            pupCol = turnHistory.getLast().getCyberPupDigCoordinate().getColumn() - 1;
                            break;
                        
                        default:
                            pupRow = RNG.nextInt(GRID_SIZE);
                            pupCol = RNG.nextInt(GRID_SIZE);
                    }
                    
                    //this makes sure the guess isn't out of range
                    if(pupRow < 0 || pupRow >= GRID_SIZE || pupCol < 0 || pupCol >= GRID_SIZE) {
                        attempt++;
                        continue;
                    }

                    if(playerVisibleToPup[pupRow][pupCol] == Symbol.GRASS) {
                        Symbol hiddenSymbol = playerHiddenToPup[pupRow][pupCol];

                        if(hiddenSymbol == Symbol.GRASS){
                            cyberPupDigResult = DigResult.NOTHING;
                            playerVisibleToPup[pupRow][pupCol] = Symbol.HOLE;
                            playerHiddenToPup[pupRow][pupCol] = Symbol.HOLE;
                        } else {
                            cyberPupDigResult = DigResult.TOY_PART;
                            playerVisibleToPup[pupRow][pupCol] = Symbol.PART;
                            playerHiddenToPup[pupRow][pupCol] = Symbol.PART;
                        }

                        cyberPupDigCoordinate = new YardCoordinate((char)(pupRow + 'A'), pupCol);
                        break;
                    }

                    attempt++;
                }
            }

            if(moreThanOneHitTogether) {
                //this makes sure the guess isn't out of range
                    while(true) {
                        if(pupRow < 0 || pupRow >= GRID_SIZE || pupCol < 0 || pupCol >= GRID_SIZE) {
                            pupRow = RNG.nextInt(GRID_SIZE);
                            pupCol = RNG.nextInt(GRID_SIZE);
                        } else {
                            break;
                        }
                    }

                    if(!hasDug && playerVisibleToPup[pupRow][pupCol] == Symbol.GRASS) {
                        Symbol hiddenSymbol = playerHiddenToPup[pupRow][pupCol];

                        if(hiddenSymbol == Symbol.GRASS){
                            cyberPupDigResult = DigResult.NOTHING;
                            playerVisibleToPup[pupRow][pupCol] = Symbol.HOLE;
                            playerHiddenToPup[pupRow][pupCol] = Symbol.HOLE;
                        } else {
                            cyberPupDigResult = DigResult.TOY_PART;
                            playerVisibleToPup[pupRow][pupCol] = Symbol.PART;
                            playerHiddenToPup[pupRow][pupCol] = Symbol.PART;
                        }

                        cyberPupDigCoordinate = new YardCoordinate((char)(pupRow + 'A'), pupCol);
                        hasDug = true;
                    }
            }
            
        } else if (!hasDug) {
            //if the previous dig wasnt a toy, the cyber pup digs at random coords
            while(true) {
                pupRow = RNG.nextInt(GRID_SIZE);
                pupCol = RNG.nextInt(GRID_SIZE);

                if(playerVisibleToPup[pupRow][pupCol] == Symbol.GRASS) {
                    Symbol hiddenSymbol = playerHiddenToPup[pupRow][pupCol];

                    if(hiddenSymbol == Symbol.GRASS){
                        cyberPupDigResult = DigResult.NOTHING;
                        playerVisibleToPup[pupRow][pupCol] = Symbol.HOLE;
                        playerHiddenToPup[pupRow][pupCol] = Symbol.HOLE;
                    } else {
                        cyberPupDigResult = DigResult.TOY_PART;
                        playerVisibleToPup[pupRow][pupCol] = Symbol.PART;
                        playerHiddenToPup[pupRow][pupCol] = Symbol.PART;
                    }

                    cyberPupDigCoordinate = new YardCoordinate((char)(pupRow + 'A'), pupCol);
                    hasDug = true;
                    break;
                }
            }
        }

        

        //check if cyberPup won
        if(allToysFound(playerHiddenToPup, playerVisibleToPup)) {
            gameState = GameState.CYBERPUP_WON;
        }

        //return the turnresult
        TurnResult turnResult = new TurnResult(playerDigCoordinate, playerDigResult, cyberPupDigCoordinate, cyberPupDigResult);
        turnHistory.add(turnResult); //adding the turnResult to the history of turnResults
        turnCount++;

        return turnResult;
    }

    /**
     * Helper method - explanatory
     * @return turn history
     */
    @Override
    public List<TurnResult> getTurnHistory(){
        return turnHistory;
    }

    /**
     * Helper method - explanatory
     * @return turn count
     */
    @Override
    public int getTurnCount() { return turnCount; }



    /**
     * Loops through the known yard to see if the toy is completed.
     * Uses a switch case to determine expected toy lengths based on Symbol.
     * 
     * If a toy is complete, returns toy symbol instead of toy part symbol.
     * Loops throw known yard to turn previous toy parts into specific toy symbol.
     * 
     * @return Specific Toy symbol || Toy part Symbol
     */
    public Symbol toyCompletionCheck(Symbol toy){
        int expectedToyLength;

        /* Toy Symbol enum to Toy enum (latter has lengths) */
        switch(toy){
            case Symbol.STICK:
                expectedToyLength = Toy.STICK.getLength();
                break;
            case Symbol.BONE:
                expectedToyLength = Toy.BONE.getLength();
                break;
            case Symbol.CHEW_ROPE:
                expectedToyLength = Toy.CHEW.getLength();
                break;
            case Symbol.SQUEAKER:
                expectedToyLength = Toy.SQUEAKER.getLength();
                break;
            case Symbol.SHOE:
                expectedToyLength = Toy.SHOE.getLength();
                break;
            default:
                throw new IllegalArgumentException("INVALID TOY");
        }

        
        /* Counts yard to determine if player yard has completed a toy */

        Symbol[][] knownPup = knownPupYard.getYard();
        Symbol[][] hiddenPup = cyberPupYard.getYard();
        int count = 0;
        
        for(int row = 0; row < knownPup.length; row++){
            for(int col = 0; col < knownPup[row].length; col++){
                if(knownPup[row][col] == Symbol.PART && hiddenPup[row][col] == toy){
                    count++;
                }
            }
        }

        /* returns Toy Part Symbol if incomplete, otherwise, loops through and replaces toy parts with completed toy */
        if(expectedToyLength == count) {
            for(int row = 0; row < knownPup.length; row++){
                for(int col = 0; col < knownPup[row].length; col++) {
                // No need to validate known yard if toy is complete.
                    if(knownPup[row][col] == Symbol.PART && hiddenPup[row][col] == toy) knownPup[row][col] = toy; 
                }
            }
            return toy;
        } else { return Symbol.PART; } 
    }


    /**
     * Returns if a given toy has been fully dug 
     * (Helper method for CyberPup operations)
     * @param toy
     * @return true/false
     */
    public boolean toyIsCompletedFORPUP(Symbol toy){
        int expectedToyLength;

        /* Toy Symbol enum to Toy enum (latter has lengths) */
        switch(toy){
            case Symbol.STICK:
                expectedToyLength = Toy.STICK.getLength();
                break;
            case Symbol.BONE:
                expectedToyLength = Toy.BONE.getLength();
                break;
            case Symbol.CHEW_ROPE:
                expectedToyLength = Toy.CHEW.getLength();
                break;
            case Symbol.SQUEAKER:
                expectedToyLength = Toy.SQUEAKER.getLength();
                break;
            case Symbol.SHOE:
                expectedToyLength = Toy.SHOE.getLength();
                break;
            default:
                throw new IllegalArgumentException("INVALID TOY");
        }

        
        /* Counts yard to determine if player yard has completed a toy */
        Symbol[][] yard = knownPlayerYard.getYard();
        int count = 0;
        
        for(int row = 0; row < yard.length; row++){
            for(int col = 0; col < yard[row].length; col++){
                if(yard[row][col] == Symbol.PART && originalPlayerYard[row][col] == toy){
                    count++;
                }
            }
        }

        /* returns Toy Part Symbol if incomplete, otherwise, loops through and replaces toy parts with completed toy */
        if(expectedToyLength == count){
            return true;
        } else {
            return false;
        } 
    }



    /**
     * @return - the overall game status
     */
    @Override
    public GameState getGameState() {
        return gameState;
    }
    
    /**
     * @return - String representation of CyberPup's Yard
     */
    @Override
    public String getCyberPupYard(){
        return knownPupYard.toString();
    }

    /**
     * @return - String representation of the Player's Yard
     */
    @Override
    public String getPlayerYard() {
        return playerYard.toString();
    }

    /**
     * @return - Array representation of what has been dug in the Player's Yard
     */
    @Override
    public Symbol[][] getPlayerVisibleYard() {
        return playerYard.getYard();
    }

    /**
     * @return - Array representation of what has exists in cyberPup's Yard
     */
    @Override
    public Symbol[][] getCyberPupHiddenYard() {
        return cyberPupYard.getYard();
    }

    /**
     * @return - Array representation of what has been dug in the CyberPup's Yard
     */
    @Override
    public Symbol[][] getCyberPupVisibleYard() {
        return knownPupYard.getYard();
    }
}
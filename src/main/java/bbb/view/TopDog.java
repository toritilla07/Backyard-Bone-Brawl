package bbb.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import bbb.model.BackyardBoneBrawlGame;
import bbb.model.DigResult;
import bbb.model.GameState;
import bbb.model.Symbol;
import bbb.model.TurnResult;
import bbb.model.Yard;
import bbb.model.YardCoordinate;


/**
 * @author Tori, Landon, Elijah
 * plays the game by prompting the user, 
 * calling the methods from BBB,
 * and displaying the game status to the user
 */
public class TopDog{
    private final BackyardBoneBrawlGame game; //represents the game that the user is playing
    private static final int GRID_SIZE = 10;
    //Below is used in promptForTurn() in order to display available commands.
    private static final String HELP_COMMAND_TABLE = "Available Commands:\n" + "dig <coordinate> - digs at the given coordinate, e.g. dig A5\n" + "turn history - displays the turn history in the game so far (see Tracking Turns below)\n" + "yards - displays the CyberPup's and Player's current yards\n" + "help - lists the commands available\n" + "quit - quits the game";

    private Yard playerYard; //a yard to represent the player's yard
    private Yard cyberYard; //a yard to represent the cyberpup's yard

    public TopDog(BackyardBoneBrawlGame game){
        this.game = game;
    }


    /**
     * prints out the current yard, with correct symbols and colors
     * @param yardString - the string version of the yard
     * @param playerYard - the player's yard
     */
    private void printPlayerYard(Yard playerYard){
        //Symbol[] yardSymbol = playerYard.createYard(GRID_SIZE);

        int size = (int) GRID_SIZE;

        System.out.print("  ");

        for(int i = 0; i < size; i ++){
            System.out.print(i + " ");
        }

        System.out.println();

        for(int row = 0; row < size; row ++){ //looping through the rows
            char labelRow = (char)('A' + row);
            System.out.print(labelRow + " "); //making the spaces between each row label

            for(int col = 0; col < size; col ++){ //looping through the cols
                Symbol symbol = game.getPlayerVisibleYard()[row][col];
                System.out.print(playerYard.createSymbol(symbol) + " ");
            }

            System.out.println("");
        }
    }


    /**
     * prints out the current yard, with correct symbols and colors
     * @param yardString - the string version of the yard
     * @param playerYard - the cyberpup's yard
     */
    private void printCyberYard(Yard cyberYard){
        //Yard yard = new Yard(GRID_SIZE);
        //Symbol[] yardSymbol = yard.createYard(GRID_SIZE);

        int size = (int) GRID_SIZE;

        System.out.print("  ");

        for(int i = 0; i < size; i ++){
            System.out.print(i + " ");
        }

        System.out.println();

        for(int row = 0; row < size; row ++){ //looping through to create the row
            char labelRow = (char)('A' + row);
            System.out.print(labelRow + " "); //making the spaces between each row label

            for(int col = 0; col < size; col ++){//looping through to create the cols
                Symbol symbol = game.getCyberPupVisibleYard()[row][col];
                System.out.print(cyberYard.createSymbol(symbol) + " ");
            }

            System.out.println("");
        }
    }


    /**
     * prints out the welcome message for when the user first starts the game
     */
    public void welcome(){
        System.out.println("Welcome to Backyard Bone Brawl!");
        System.out.println("Can you dig it?");

        System.out.println();

        //Makes a new playerYard
        playerYard = new Yard(GRID_SIZE, true, false);
        //Makes a new cyberPupYard
        cyberYard = new Yard(GRID_SIZE, false, false);

        //printing out what the player's yard looks like at the start of the game
        System.out.println("Player's Yard: ");
        printPlayerYard(playerYard);
            

        //printing out what the cyberPup's yard looks like at the start of the game
        System.out.println("CyberPup's Yard: ");
        printCyberYard(cyberYard);
    }
            
    
    /**
     * Asks the user to enter in dig coordinates and returning them
     * @param scanner - the scanner object being used to collect user input
     * @return the player's coordinates that they took
     */
    public String promptForTurn(Scanner scanner){
        while(true){
            System.out.print("Enter command(enter help to see the command options): ");
            // Converts taken commands to uppercase to ensure that returned coordinates in within range.
            String playerInput = scanner.nextLine().toUpperCase();
            // If contains a dig command, send potential coordinates.
            if(playerInput.contains("DIG")) { //needs further validation to avoid throwing errors.
                return playerInput.split(" ")[1];
            } 
            switch (playerInput){
                case("HELP"):
                    System.out.println(HELP_COMMAND_TABLE);
                    continue;
                case("TURN HISTORY"):
                    System.out.println("Turn history: " ); // prints turn history
                    printTurnHistory();
                    continue;
                case("YARDS"):
                    printAllYards();
                    continue;
                case("QUIT"):
                    return "quit";
                default:
                    System.out.println("Not a valid command, try again.");
                    continue;
            }
        }
    }


    /**
     * turns the DigResult into readable text
     * @param - the result from the player or cyberpup's dig
     */
    private String formatResult(DigResult result){
        String formatted = "";
        
        if(result.equals(DigResult.NOTHING)){//if the player dug up nothing
            formatted = "Nothing!";

        } else if (result.equals(DigResult.TOY_PART)){//if the player dug up a toy part
            formatted = "Toy Part!";

        } else if (result.equals(DigResult.FULL_TOY)){//if the player dug up a full toy
            formatted = "Final Toy Part!";

        } else { //if the player's dig was invalid
            formatted = "Invalid!";
        }

        return formatted;
    }

    /**
     * Prints player and CyberPup yards to Command line interface.
     */
    private void printAllYards(){
        //printing out what the player's yard looks like mid-game
        System.out.println("Player's Yard: ");
        printPlayerYard(playerYard);
            
        //printing out what the cyberpup's yard looks like mid-game
        System.out.println("Cyberpup's Yard: ");
        printCyberYard(cyberYard);
    }

    /**
     * displays the result of the player and cyberpup's turn
     */
    public void displayTurnResult(TurnResult result){
        YardCoordinate playerCoord = result.getPlayerDigCoordinate();
        DigResult playerResult = result.getPlayerDigResult();

        System.out.println("You dug at " + playerCoord.getRow() + playerCoord.getColumn() +": " + formatResult(playerResult));

        if((playerResult != DigResult.INVALID)){
            YardCoordinate cyberCoord = result.getCyberPupDigCoordinate();
            DigResult cyberResult = result.getCyberPupDigResult();
            System.out.println("CyberPup dug at " + cyberCoord.getRow() + cyberCoord.getColumn() + ": " + formatResult(cyberResult));
        }

        System.out.println();

        //printing out what the player's yard looks like mid-game
        System.out.println("Player's Yard: ");
        printPlayerYard(playerYard);
            

        //printing out what the cyberpup's yard looks like mid-game
        System.out.println("Cyberpup's Yard: ");
        printCyberYard(cyberYard);
    }



    public void printTurnHistory(){
        List<TurnResult> turnHistory = new LinkedList<>();
        turnHistory = game.getTurnHistory();
        if(turnHistory.isEmpty()){
            System.out.println("No turns have been taken yet.");
            return;
        }

        int turn = 1;
        for(TurnResult t : turnHistory){
            YardCoordinate playerCoord = t.getPlayerDigCoordinate();
            DigResult playerRes = t.getPlayerDigResult();

            YardCoordinate pupCoord = t.getCyberPupDigCoordinate();
            DigResult pupRes = t.getCyberPupDigResult();

            System.out.print(turn + ") ");
            System.out.println("You dug at " + playerCoord + ": " + playerRes +
                                " - CyberPup dug at " + pupCoord + ": " + pupRes);

            turn += 1;
        }
    }
    


    /**
     * plays the game by prompting for user input 
     * calling BackyardBoneBrawl methods, 
     * and displaying the game status to the user
     */
    public void play(){
        // Closed @ game-end
        Scanner scanner = new Scanner(System.in);

        welcome();

        //play until the gameState is not in progress
        while((game.getGameState() == GameState.IN_PROGRESS || game.getGameState() == GameState.NOT_STARTED)){
            String input = promptForTurn(scanner);

            if(input.equals("quit")){ //if the user types quit, stop playing the game
                System.out.println("sniff you later!");
                break;
            }

            if(input.equals("turn history")){
                printTurnHistory();
                break;
            }

            TurnResult result = game.takeTurn(new YardCoordinate(input)); //the user takes their turn and the result is stored

            if(result.getPlayerDigResult() == DigResult.INVALID){ //if the player enters in invalid coordinates
                System.out.println("Invalid coordinates. Please try again.");
            }

            displayTurnResult(result); //print out the player's turn result

            if(game.getGameState() == GameState.PLAYER_WON){ //if the player won print out the message that the player won
                System.out.println("Game Over!");
                System.out.println("Hot diggity dog! You win!");
                scanner.close();
                break;

            } else if (game.getGameState() == GameState.CYBERPUP_WON){ //if the cyperPup won print out the message that the cyberPup won
                System.out.println("Game Over!");
                System.out.println("CyberPup wins this round!");
                scanner.close();
                break;
            }
        }
    }
}

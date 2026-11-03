package bbb.model;
/**
 * @author Elijah, Tori, Landon
 * Creates a yard object utilizing Symbol object arrays.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import bbb.view.AnsiColorCodes;

public class Yard {
    private final Symbol[][] yardArray;
    private final int GRID_SIZE;
    private final Toy[] ALL_TOYS = Toy.values(); // Allows for adaptable toy placement


    /**
     * Yard Constuctors with an overload option to create a blank (undug) yard.
     * @param grid_size
     */
   public Yard(int grid_size, boolean isPlayerYard, boolean isBlank){
        this.GRID_SIZE = grid_size;
        this.yardArray = new Symbol[GRID_SIZE][GRID_SIZE];

        String filePath = "data/player_yard.txt";
        File file = new File(filePath);

        if(isPlayerYard && file.exists()){ //if the yard is a player yard and the file exists build the yard using the file
            filePlace(filePath);

        } else if(isBlank) {
            //makes a blank yard (for known pup yard)
            for(int row = 0; row < GRID_SIZE; row ++){
                for(int col = 0; col < GRID_SIZE; col ++){
                    yardArray[row][col] = Symbol.GRASS;
                }
            }
        } else {
            //filling the yard with grass
            for(int row = 0; row < GRID_SIZE; row ++){
                for(int col = 0; col < GRID_SIZE; col ++){
                    yardArray[row][col] = Symbol.GRASS;
                }
            }

            YardSizeValidation();//validating yard size
            place(new Random()); //placing the toys randomly in the yard
        }
   }

   /**
    * places the toys in the yard based off of the player_yard.txt file
    */
   public void filePlace(String filename){
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
        String line; //represents a line in the file
        int row = 0; //keeping track of the row/line number

        //while the line isnt null and the row is less than the grid_size
        while(((line = br.readLine()) != null) && (row < GRID_SIZE)){
            for(int col = 0; col < GRID_SIZE; col ++){ //looping through the columns(characters) in the line
                char character = line.charAt(col); //getting the character at the column in the line

                switch(character){ //making the yardArray have the same character as in the file
                    case 'B':
                        yardArray[row][col] = Symbol.BONE;
                        break;

                    case 'C':
                        yardArray[row][col] = Symbol.CHEW_ROPE;
                        break;

                    case 'Q':
                        yardArray[row][col] = Symbol.SQUEAKER;
                        break;

                    case 'S':
                        yardArray[row][col] = Symbol.STICK;
                        break;

                    case 'H':
                        yardArray[row][col] = Symbol.SHOE;
                        break;

                    default:
                        yardArray[row][col] = Symbol.GRASS;
                }
            }

            row ++; //moving to the next row(line)
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
   }


   /**
    * places the toys randomly in the yard
    * @param RNG - a random object used to create the random row and col where the toy will be placed
    */
   public void place(Random RNG){
        //iterating the toys and placing them randomly int he yard
        for(Toy toy : ALL_TOYS){
            boolean placed = false;//creatign a flag to represent if the toy has been placed

            //until the toy is placed do the following
            while(!placed){
                int row = RNG.nextInt(GRID_SIZE); //a random row
                int col = RNG.nextInt(GRID_SIZE); //a random column
                boolean horizontal = RNG.nextBoolean(); //randomly chooses if it is horizontal or not

                if(canPlace(row, col, horizontal, toy)){ //if the toy can be placed
                    for(int i = 0; i < toy.getLength(); i ++){
                        int r = row;
                        int c = col;

                        if(horizontal) //if the toy is being placed horizontally
                            c = col + i;

                        else 
                            r = row + i;

                        yardArray[r][c] = toy.getSymbol(); //placing the toy at the row and col
                    }

                    placed = true; //changing the flag to be true to end the loop for that toy
                }
            }
        }
   }


   /**
    * checking to see if the randomly generated row and col can have a toy placed at it
    */
   private boolean canPlace(int row, int col, boolean horizontal, Toy toy){
        // This checks to see it the block can be placed at its random row and col coordinates that were generated.
        for(int i = 0; i < toy.getLength(); i++) {
            int checkRow = row; //the row beign checked if for if a toy can be placed there
            int checkCol = col; //the col beign checked if for if a toy can be placed there
            boolean canPlace; //stores if the toy can be placed at the randomly chosen location

            if(horizontal) { //if the toy is being placed horizontally
                checkCol = col + i;

            } else { //if the toy is being placed vertically
                checkRow = row + i;
            }

            //checking if the chosen spot to place the toy meets the constraints
            //if it doesn't false is returned from the method, otherwise true is
            if(checkRow >= GRID_SIZE || checkCol >= GRID_SIZE) {
                canPlace = false;
                return canPlace;
            }

            if(yardArray[checkRow][checkCol] != Symbol.GRASS) {
                canPlace = false;
                return canPlace;
            }
        }

        //if the code gets to this point then the randomly chosen spots meets all the contraints and a toy can be placed here
        return true;
   }


   /**
    * Input validation for the Yard grid_size (will throw exception otherwise)
    * @return true (for valid grid sizes)
    */
   private boolean YardSizeValidation() throws IllegalArgumentException{
        //Total length toys will take up in grid.
        int totalToyLength = 0;

        for(Toy item : ALL_TOYS){
            totalToyLength+=item.getLength();
        }
        // If the toy length is greater or equal to grid space, throws exception. 
        if (totalToyLength >= GRID_SIZE*GRID_SIZE){
            throw new IllegalArgumentException("Grid Size Too Small to contain all toys.");
        }

        return true;
   }
   
   /**
    * Basic accessor for the Yard object's array.
    * @return yard_array
    */
   public Symbol[][] getYard(){
        return yardArray;
   }


    /**
     * prints the symbols in the yard out properly
     * @param symbol - the symbol being converted
     * @return - the converted symbol
     */
    public String createSymbol(Symbol symbol){
        String transformed = "";

        if(symbol.equals(Symbol.GRASS)){ //if the symbol is grass
            transformed = AnsiColorCodes.GREEN + "#" + AnsiColorCodes.RESET;

        } else if(symbol.equals(Symbol.HOLE)){ //if the symbol is hole
            transformed = AnsiColorCodes.WHITE + " " + AnsiColorCodes.RESET;

        } else if(symbol.equals(Symbol.PART)){ //if the symbol is part
            transformed = AnsiColorCodes.YELLOW + "@" + AnsiColorCodes.RESET;

        } else if(symbol.equals(Symbol.STICK)){ //if the symbol is stick
            transformed = AnsiColorCodes.BROWN + "S" + AnsiColorCodes.RESET;

        } else if(symbol.equals(Symbol.BONE)){ //if the symbol is bone
            transformed = AnsiColorCodes.WHITE + "B" + AnsiColorCodes.RESET;

        } else if(symbol.equals(Symbol.SQUEAKER)){ //if the symbol is squeaker
            transformed = AnsiColorCodes.MAGENTA+ "Q" + AnsiColorCodes.RESET;

        } else if(symbol.equals(Symbol.SHOE)){ //if the symbol is shoe
            transformed = AnsiColorCodes.CYAN + "H" + AnsiColorCodes.RESET;

        } else if(symbol.equals(Symbol.CHEW_ROPE)){ //if the symbol is chew rope
            transformed = AnsiColorCodes.ORANGE + "C" + AnsiColorCodes.RESET;
        }

        return transformed;
    }



   /**
    * Basic override method for when the object needs to be printed
    * returns the yard array as a String with the built-in Arrays method.
    */
   @Override
   public String toString(){
    StringBuilder stringBuild = new StringBuilder();
    stringBuild.append("");
    
    for(int col = 0; col < GRID_SIZE; col ++){
        stringBuild.append(col).append(" ");
    }

    stringBuild.append("\n");

    for(int row = 0; row < GRID_SIZE; row ++){ //looping through the rows
        char rowLabel = (char)('A' + row);//adding the row label
        stringBuild.append(rowLabel).append(" ");

        for(int col = 0; col < GRID_SIZE; col ++){
            stringBuild.append(yardArray[row][col].getAsciiChar());
        }

        stringBuild.append("\n");
    }

    return stringBuild.toString();
   }
}
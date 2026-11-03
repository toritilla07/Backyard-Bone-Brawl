package bbb.model;
/**
 * Parses coordinates and creates an object to hold the row/column
 * Coordinates being instantiated may be from program or parsed from user input.
 * 
 * @author Elijah DeLeon
 */

public class YardCoordinate {
    private char row; // Notation of A,B,C,D,...
    private int column; // Notation of 1,2,3,4,...

    /**
     * Used for creating a YardCoordinate object from known variables. 
     * @param row
     * @param column
     */
    public YardCoordinate(char row, int column){
        this.row = row;
        this.column = column;
    }

    /**
     * Used for parsing user-inputted coordinates (EX: 'A5').
     * @param coordinate
     */
    public YardCoordinate(String coordinate){
        String[] coords = coordinate.split("");
        this.row = coords[0].charAt(0);
        this.column = Integer.parseInt(coords[1]);
    }

    /**
     * Accessor Methods
     * @return Row || Column
     */
    public char getRow() {return row;}
    public int getColumn(){ return column;}


    //toString methof to convert the orw and column into a string
    @Override
    public String toString(){
        return getRow() + "" + getColumn();
    }
}

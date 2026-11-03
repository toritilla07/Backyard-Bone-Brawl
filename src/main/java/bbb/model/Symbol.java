/**
 * This enum has the types of toys associated with
 * their respective ascii values.
 * @author Landon
 */

package bbb.model;

public enum Symbol {
    GRASS('#'),
    HOLE(' '),
    PART('@'),
    STICK('S'),
    BONE('B'),
    SQUEAKER('Q'),
    SHOE('H'),
    CHEW_ROPE('C');

    private final char asciiChar;

    private Symbol(char asciiChar) {
        this.asciiChar = asciiChar;
    }

    public char getAsciiChar() { return asciiChar; }

    
}

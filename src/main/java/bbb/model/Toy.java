/**
 * This enum has methods for getters and also has a getter to return
 * a set of every ascii character assigned to a symbol in Symbol.java
 * @author Landon
 */   

package bbb.model;

import java.util.HashSet;
import java.util.Set;

public enum Toy {
    STICK(Symbol.STICK, 5),
    BONE(Symbol.BONE, 4),
    CHEW(Symbol.CHEW_ROPE, 3),
    SQUEAKER(Symbol.SQUEAKER, 3),
    SHOE(Symbol.SHOE, 2);

    private final Symbol symbol;
    private final int length;


    private Toy(Symbol symbol, int length) {
        this.symbol = symbol;
        this.length = length;
    }


    public Symbol getSymbol() { return symbol; }
    public int getLength() { return length; }


    public Set<Character> getAllAsciiChars() {
        Set<Character> valueSet = new HashSet<>(); 
        Symbol[] valueArray = Symbol.values();
        
        for(Symbol value : valueArray) {
            valueSet.add(value.getAsciiChar());
        } 
        
        return valueSet;
    }
}

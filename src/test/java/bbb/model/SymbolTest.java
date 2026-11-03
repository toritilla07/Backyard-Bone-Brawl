package bbb.model.model;

/**
 * @author Landon H.
 * Tests the enum Symbol.
 * testSymbolValues() ensures that the value of each enum variable is returned correctly.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import bbb.model.Symbol;

public class SymbolTest {
    @Test
    public void testSymbolValues() {
        assertEquals(Symbol.GRASS, Symbol.valueOf("GRASS"));
        assertEquals(Symbol.HOLE, Symbol.valueOf("HOLE"));
        assertEquals(Symbol.PART, Symbol.valueOf("PART"));
        assertEquals(Symbol.STICK, Symbol.valueOf("STICK"));
        assertEquals(Symbol.BONE, Symbol.valueOf("BONE"));
        assertEquals(Symbol.SQUEAKER, Symbol.valueOf("SQUEAKER"));
        assertEquals(Symbol.SHOE, Symbol.valueOf("SHOE"));
        assertEquals(Symbol.CHEW_ROPE, Symbol.valueOf("CHEW_ROPE"));
    }
}

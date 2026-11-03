package bbb.model.model;

/**
 * @author Landon
 * Tests the enum Toy.
 * testToyLength() tests to make sure each toy has the correct size.
 * testToySymbol() tests to make sure each toy has the correct symbol.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import bbb.model.Symbol;
import bbb.model.Toy;

public class ToyTest {
    @Test
    public void testToyLength() {
        assertEquals(Toy.STICK.getLength(), 5);
        assertEquals(Toy.BONE.getLength(), 4);
        assertEquals(Toy.CHEW.getLength(), 3);
        assertEquals(Toy.SQUEAKER.getLength(), 3);
        assertEquals(Toy.SHOE.getLength(), 2);
    }

    @Test
    public void testToySymbol() {
        assertEquals(Toy.STICK.getSymbol(), Symbol.STICK);
        assertEquals(Toy.BONE.getSymbol(), Symbol.BONE);
        assertEquals(Toy.SQUEAKER.getSymbol(), Symbol.SQUEAKER);
        assertEquals(Toy.SHOE.getSymbol(), Symbol.SHOE);
        assertEquals(Toy.CHEW.getSymbol(), Symbol.CHEW_ROPE);
    }
}

package bbb.model.model;
/**
 * @author Elijah, Tori
 * Tests the Yard Class
 *      testValidYardLength() makes sure that a valid Yard length creates a valid Yard.
 *      testInvalidYardLength() makes sure that invalid yard lengths throw an IOexception.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bbb.model.Symbol;
import bbb.model.Yard;

public class YardTest {
    Yard yard = new Yard(10, false, false);


    //yardLengthTests
    @Test
    public void testValidYardLength(){
        int gridSize = 10;
        Yard yard = new Yard(100, false, false);
        int expectedLength = gridSize * gridSize;
        assertEquals((yard.getYard()).length, expectedLength);
    }

    @Test
    public void testInvalidYardLength(){
        int grid_size = 3;
        assertThrows(IllegalArgumentException.class,() -> {
            new Yard(grid_size, false, false);
        });
    }


    //testing getYard
    @Test
    void testGetYard_NotNull() {
        assertNotNull(yard.getYard());
    }



    //testing place()
    @Test
    void testPlace_existingToyIsThere(){
        Symbol[][] grid = yard.getYard();

        int toyCount = 0; //a counter for the toys int he yard

        for(int row = 0; row < 10; row ++){
            for(int col = 0; col < 10; col ++){
                if(grid[row][col] != Symbol.GRASS){
                    toyCount ++;
                }
            }
        }

        //the toycount should be greater than zero, but less than the entire field
        assertEquals((toyCount > 0), true);
        assertEquals((toyCount < 100), true);
    }


    //testing toString
    @Test
    void testToString_NotNull(){
        String result = yard.toString();

        assertNotNull(result);
    }

    @Test
    void testToString_containsSymbols(){
        Yard yard = new Yard(10, true, false);
        System.out.println(yard);
        String result = yard.toString();
        System.out.println("Test yard:");
        System.out.println(result);

        assertTrue(result.contains("#"));
    }
}

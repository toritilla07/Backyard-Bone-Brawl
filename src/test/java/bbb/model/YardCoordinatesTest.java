package bbb.model.model;

/**
 * @author Landon
 * Tests YardCoordinate.
 * testValidCoordinate() tests to see if, when given a valid coord, it will be able to return the correct row and column.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import bbb.model.YardCoordinate;

public class YardCoordinatesTest {
    @Test
    public void testValidCoordinate() {
        YardCoordinate coord = new YardCoordinate("A5");
        assertEquals('A', coord.getRow());
        assertEquals(5, coord.getColumn());
    }
}

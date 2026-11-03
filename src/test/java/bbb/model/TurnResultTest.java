package bbb.model.model;

/**
 * @author Landon
 * Tests TurnResult.
 * testTurnResult() makes sure that, when given a new TurnResult object with coords and DigResults, it will store and return the correct coords and DigResults.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import bbb.model.DigResult;
import bbb.model.TurnResult;
import bbb.model.YardCoordinate;

public class TurnResultTest {
    @Test
    public void testTurnResult() {
        TurnResult result = new TurnResult(new YardCoordinate("A1"), DigResult.NOTHING, new YardCoordinate("B2"), DigResult.TOY_PART);
        assertEquals(DigResult.NOTHING, result.getPlayerDigResult());
        assertEquals(DigResult.TOY_PART, result.getCyberPupDigResult());
        assertEquals('A', result.getPlayerDigCoordinate().getRow());
        assertEquals(1, result.getPlayerDigCoordinate().getColumn());
        assertEquals('B', result.getCyberPupDigCoordinate().getRow());
        assertEquals(2, result.getCyberPupDigCoordinate().getColumn());
    }
}

package bbb.model.model;

/**
 * @author Landon, Tori
 * Tests BackYardBoneBrawlGameImpl. 
 * testValidTurn() tests to see if, when valid coords are given, the dig result is not INVALID.
 * testTakeTurnInvalidCoordinate() tests to see if, when invalid coords are given, the dig result is INVALID.
 * testTakeTurnSameSpotTwice() tests to see if, when the same coords are given twice, the dig result is INVALID.
 * testCyberPupMovesAfterPlayer() tests to make sure that the CyberPup always takes a turn after the human player as long as the human hasn't won or given an invalid coord.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import bbb.model.BackyardBoneBrawlGameImpl;
import bbb.model.DigResult;
import bbb.model.GameState;
import bbb.model.Symbol;
import bbb.model.TurnResult;
import bbb.model.Yard;
import bbb.model.YardCoordinate;

public class BackyardBoneBrawlGameImplTest {
    private BackyardBoneBrawlGameImpl game = new BackyardBoneBrawlGameImpl();


    @Test
    public void testValidTurn() {
        TurnResult result = game.takeTurn(new YardCoordinate("A1"));

        assertEquals(result.getPlayerDigResult() == DigResult.NOTHING ||
                    result.getPlayerDigResult() == DigResult.TOY_PART ||
                    result.getPlayerDigResult() == DigResult.FULL_TOY, true);
    }

    @Test
    public void testTakeTurnInvalidCoordinate() {
        TurnResult result = game.takeTurn(new YardCoordinate("Z10"));

        assertEquals(DigResult.INVALID, result.getPlayerDigResult());
    }

    @Test
    public void testTakeTurnSameSpotTwice() {
        YardCoordinate coord = new YardCoordinate("B2");

        game.takeTurn(coord);
        TurnResult result = game.takeTurn(coord);

        assertEquals(DigResult.INVALID, result.getPlayerDigResult());
    }

    @Test
    public void testCyberPupMovesAfterPlayer() {
        TurnResult result = game.takeTurn(new YardCoordinate("C3"));

        if (result.getPlayerDigResult() != DigResult.INVALID &&
            game.getGameState() != GameState.PLAYER_WON) {

            assertNotNull(result.getCyberPupDigCoordinate());
        }
    }


    //testing to make sure that creating an all grass yard works
    @Test
    void testCreateBlankYard_AllGrass(){
        Yard yard = new Yard(10, false, true);
        Symbol[][] grid = yard.getYard();

        for(int row = 0; row < 10; row ++){
            for(int col = 0; col < 10; col ++){
                assertEquals(grid[row][col], Symbol.GRASS);
            }
        }
    }


    //testing to see if all toys are being found is working
    @Test
    void testAllToysFound_allFound(){
        Symbol[][] hidden = new Symbol[10][10];
        Symbol[][] visible = new Symbol[10][10];

        for(int row = 0; row < 10; row ++){
            for(int col = 0; col < 10; col ++){
                hidden[row][col] = Symbol.GRASS;
                visible[row][col] = Symbol.GRASS;
            }
        }

        assertEquals(game.allToysFound(hidden, visible), true); //return true because all toys have been found
    }

    @Test
    void testAllToysFound_toyNotRevealed(){
        Symbol[][] hidden = new Symbol[10][10];
        Symbol[][] visible = new Symbol[10][10];

        for(int row = 0; row < 10; row ++){
            for(int col = 0; col < 10; col ++){
                hidden[row][col] = Symbol.GRASS;
                visible[row][col] = Symbol.GRASS;
            }
        }

        hidden[0][0] = Symbol.BONE; //making it so not all the toys have been found

        assertEquals(game.allToysFound(hidden, visible), false); //return false because not all toys have been found
    }



    //testing the take_turn method
    @Test
    void testTakeTurn_outOfBounds(){
        YardCoordinate coord = new YardCoordinate('Z', 0);
        TurnResult turnResult = game.takeTurn(coord);

        assertEquals(DigResult.INVALID, turnResult.getPlayerDigResult());
    }

    @Test
    void testTakeTurn_nothing(){
        Symbol[][] cyberYard = game.getCyberPupHiddenYard();

        //making sure that the yard is all grass
        for(int row = 0; row < 10; row ++){
            for(int col = 0; col < 10; col ++){
                cyberYard[row][col] = Symbol.GRASS;
            }
        }

        YardCoordinate coord = new YardCoordinate('A', 0);
        TurnResult turnResult = game.takeTurn(coord);

        assertEquals(DigResult.NOTHING, turnResult.getPlayerDigResult());
    }

    @Test
    void testTakeTurn_invalid_alreadDug(){
        Symbol[][] cyberYard = game.getCyberPupHiddenYard();

        //making sure that the yard is all grass
        for(int row = 0; row < 10; row ++){
            for(int col = 0; col < 10; col ++){
                cyberYard[row][col] = Symbol.GRASS;
            }
        }

        YardCoordinate coord = new YardCoordinate('A', 0);
        TurnResult turnResult = game.takeTurn(coord);
        turnResult = game.takeTurn(coord);

        assertEquals(DigResult.INVALID, turnResult.getPlayerDigResult());
    }

    
    
    //toy completion checks
    @Test
    void testToyCompletion_Incomplete(){
        Symbol result = game.toyCompletionCheck(Symbol.BONE);
        assertEquals(Symbol.PART, result);
    }

    
    //game state tests
    @Test
    void testInitialGameState(){
        assertEquals(GameState.NOT_STARTED, game.getGameState());
    }

    @Test
    void testInitialGameState_NotNull(){
        assertNotNull(game.getPlayerVisibleYard());

        assertNotNull(game.getCyberPupHiddenYard());
        assertNotNull(game.getCyberPupVisibleYard());

        assertNotNull(game.getCyberPupYard());
        assertNotNull(game.getPlayerYard());
    }
}

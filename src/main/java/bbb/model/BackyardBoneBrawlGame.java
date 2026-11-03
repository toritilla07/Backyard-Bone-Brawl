package bbb.model;

import java.util.List;

/**
 * @author Tori, Landon
 * an interface being used by BBBGI and TurnResult
 */
public interface BackyardBoneBrawlGame{
    public TurnResult takeTurn(YardCoordinate playerDigCoordinate);
    public GameState getGameState();
    public String getCyberPupYard();
    public String getPlayerYard();
    public Symbol[][] getPlayerVisibleYard();
    public Symbol[][] getCyberPupHiddenYard();
    public Symbol[][] getCyberPupVisibleYard();
    public List<TurnResult> getTurnHistory();
    public int getTurnCount();
}

/**
 * Enum for the diffrent possible game states.
 * @author Landon
 */

package bbb.model;

public enum GameState {
    NOT_STARTED("NOT_STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    PLAYER_WON("PLAYER_WON"),
    CYBERPUP_WON("CYBERPUP_WON");

    private final String state;

    private GameState(String state) {
        this.state = state;
    }

    public String getState() { return state; }
}

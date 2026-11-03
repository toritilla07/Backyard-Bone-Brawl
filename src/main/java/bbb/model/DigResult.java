/**
 * Enum for the diffrent possible dig results.
 * @author Landon, Elijah
 */

package bbb.model;

public enum DigResult {
    TOY_PART("TOY_PART"), //the part of the toy that was found
    NOTHING("NOTHING"), //no toy part was found
    FULL_TOY("FULL_TOY"), //the final part was undug and the full toy has been found
    INVALID("INVALID"); //the player's coordinate was off the yard grid, bad format, or already dug

    private final String result;

    private DigResult(String result){
        this.result = result;
    }

    public String getResult(){ return result;}
}

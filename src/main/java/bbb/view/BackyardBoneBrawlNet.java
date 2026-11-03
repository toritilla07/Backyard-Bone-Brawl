package bbb.view;

/**
 * @author - Tori
 * initiates the game
 */
public class BackyardBoneBrawlNet {
    public static void main(String[] args) {
        //creates a BackyardBonBrawlProxy object
        BackyardBoneBrawlGameProxy game = new BackyardBoneBrawlGameProxy("localhost", 8080);
        TopDog topDog = new TopDog(game);//creates a TopDog Object(with a BBB object)
        topDog.play(); //calls play() on the topdog object
    }
}

package bbb.view;

import bbb.model.BackyardBoneBrawlGame;
import bbb.model.BackyardBoneBrawlGameImpl;

/**
 * Launches the local CLI version of Backyard Bone Brawl.
 * 
 * Main:
 * - Creates the game object.
 * - Creates a TopDog object that plays the game.
 * - Starts the game loop.
 * 
 */

public class BackyardBoneBrawlLocal{
    public static void main(String[] args) {
        BackyardBoneBrawlGame game = new BackyardBoneBrawlGameImpl();
        TopDog topDog = new TopDog(game);
        topDog.play();
    }
}
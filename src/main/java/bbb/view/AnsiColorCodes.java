package bbb.view;

/**
 * Provides ANSI escape-code constants used to color text in the
 * Backyard Bone Brawl CLI.
 * 
 * Example:
 *   System.out.println(AsciiColorCodes.GREEN + "#" + AsciiColorCodes.RESET);
 */
public class AnsiColorCodes {
    public static String RESET = "\u001b[0m";
    public static String RED = "\u001b[38;5;9m";
    public static String BLUE = "\u001b[38;5;12m";
    public static String ORANGE = "\u001b[38;5;130m";
    public static String GREEN = "\u001b[38;5;28m";
    public static String WHITE = "\u001b[38;5;15m";
    public static String BROWN = "\u001b[38;5;94m";
    public static String YELLOW = "\u001b[38;5;11m";
    public static String MAGENTA = "\u001b[38;5;13m";
    public static String GOLD = "\u001b[38;5;220m";
    public static String PURPLE = "\u001b[38;5;5m";
    public static String LT_GRAY = "\u001b[38;5;7m";
    public static String CYAN = "\u001b[38;5;14m";
}

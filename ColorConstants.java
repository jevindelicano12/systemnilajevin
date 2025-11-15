/**
 * ANSI Color Constants for Terminal Output
 * Provides color codes for a professional, eye-catching terminal UI
 */
public class ColorConstants {
    
    // ANSI Reset
    public static final String RESET = "\u001B[0m";
    
    // Foreground Colors - Regular
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // Foreground Colors - Bright
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";
    
    // Background Colors
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_MAGENTA = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_WHITE = "\u001B[47m";
    
    // Text Styles
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    
    // Color Combinations for Different UI Elements
    
    // Status Indicators
    public static final String SUCCESS = BRIGHT_GREEN;
    public static final String ERROR = BRIGHT_RED;
    public static final String WARNING = BRIGHT_YELLOW;
    public static final String INFO = BRIGHT_CYAN;
    public static final String ACTIVE = BRIGHT_GREEN;
    public static final String INACTIVE = BRIGHT_RED;
    
    // UI Elements
    public static final String HEADER = BRIGHT_BLUE + BOLD;
    public static final String MENU_TITLE = BRIGHT_MAGENTA + BOLD;
    public static final String MENU_ITEM = BRIGHT_CYAN;
    public static final String MENU_SELECTED = BRIGHT_GREEN + BOLD;
    public static final String BORDER = BRIGHT_WHITE;
    public static final String PRICE = BRIGHT_YELLOW + BOLD;
    public static final String PRODUCT_NAME = BRIGHT_WHITE;
    public static final String DESCRIPTION = CYAN;
    public static final String INPUT_PROMPT = BRIGHT_CYAN;
    
    // Box Background Colors
    public static final String BOX_BG_DARK = BG_BLUE;
    public static final String BOX_BG_ACCENT = BG_BLACK;
    public static final String HEADER_BOX_BG = BG_BLUE;
    public static final String MENU_BOX_BG = BG_BLACK;
    
    // Helper method to create colored text
    public static String colorize(String text, String color) {
        return color + text + RESET;
    }
    
    // Helper method for bold text
    public static String bold(String text) {
        return BOLD + text + RESET;
    }
    
    // Helper method for colored bold text
    public static String colorBold(String text, String color) {
        return color + BOLD + text + RESET;
    }
}

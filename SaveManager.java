import java.io.*;
import java.util.List;

/**
 * Simple save/load system using Java serialization.
 * Saves the entire GameManager state to a file.
 */
public class SaveManager {
    private static final String SAVE_FILE = "savegame.dat";

    /**
     * Save the game. Call this when player clicks "Save".
     */
    public static void save(GameManager gm) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(gm);
            System.out.println("[SAVE] Game saved.");
        } catch (IOException e) {
            System.out.println("[SAVE] Failed: " + e.getMessage());
        }
    }

    /**
     * Load a saved game. Returns null if no save exists.
     */
    public static GameManager load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            GameManager gm = (GameManager) in.readObject();
            System.out.println("[LOAD] Game loaded.");
            return gm;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[LOAD] No save file found.");
            return null;
        }
    }

    /**
     * Check if a save file exists.
     */
    public static boolean saveExists() {
        return new File(SAVE_FILE).exists();
    }
}
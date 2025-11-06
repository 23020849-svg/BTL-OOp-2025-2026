package arkanoid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveManager {
    private static final String SAVE_FILE = "game_save.dat";
    private static SaveManager instance;

    private SaveManager() {
        // Private constructor to prevent instantiation
    }

    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;

    }

    
    public boolean save(GameState state) {
    try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(SAVE_FILE))) {
        oos.writeObject(state);
        System.out.println("Game saved successfully!");
        return true;
    } catch (Exception e) {
        System.err.println("Error saving game: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
    
    public GameState load() {
    File file = new File(SAVE_FILE);
    if (!file.exists()) {
        System.out.println("No saved game found.");
        return null;
    }
    
    try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(SAVE_FILE))) {
        GameState state = (GameState) ois.readObject();
        System.out.println("Game loaded successfully!");
        return state;
    } catch (Exception e) {
        System.err.println("Error loading game: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

    public boolean hasSavedGame() {
        File file = new File(SAVE_FILE);
         if (!file.exists()) {
            return false;
    }

    GameState state = load();
    return state != null && state.isValid();
    }

    public boolean deleteSave() {

        File file = new File(SAVE_FILE);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Save file deleted.");
            }
            return deleted;
        }
        return false;
    }

    //lay thong tin save

    public String getSaveInfo() {
        GameState state = load();

        if (state == null) {
            return "No valid save data.";
        } 

        return String.format("Level %d | Score: %d | Lives: %d | %s",
            state.levelIndex,
            state.score,
            state.lives,
            state.getFormattedSaveTime())
            
    ;
    }
}
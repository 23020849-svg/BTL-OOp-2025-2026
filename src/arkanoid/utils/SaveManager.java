package arkanoid.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveManager {
    private static final String SAVE_FILE = "game_save.dat";
    
    public static void save(GameState state) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(state);
        }
    }
    
    public static GameState load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FILE))) {
            return (GameState) ois.readObject();
        }
    }
}
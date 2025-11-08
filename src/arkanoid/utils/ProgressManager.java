package arkanoid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ProgressManager {
    

    private static final String PROGRESS_FILE = "game_progress.dat";    
    private static ProgressManager instance;
    private GameProgress currentProgress;

    private ProgressManager() {
        loadProgress();
    }

    // singleton pattern
    public static ProgressManager getInstance() {
        if (instance == null) {
            instance = new ProgressManager();
        }
        return instance;
    }

    //lay tien do hien tai

    public GameProgress getProgress() {

        if (currentProgress == null) {
            currentProgress = new GameProgress();
        }
        return currentProgress;
    }

    //luu tien do vao file

    public boolean saveProgress() {
         try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(PROGRESS_FILE))) {
            oos.writeObject(currentProgress);
            System.out.println("Game progress saved successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error saving progress: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    //tai tien do tu file
    public boolean loadProgress() {
        File file = new File(PROGRESS_FILE);
        
        if (!file.exists()) {
            System.out.println("No saved progress found. Creating new progress.");
            currentProgress = new GameProgress();
            return false;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(PROGRESS_FILE))) {
            currentProgress = (GameProgress) ois.readObject();
            System.out.println("Game progress loaded successfully.");
            System.out.println("Highest level unlocked: " + currentProgress.getHightestLevelUnlocked());
            return true;
        } catch (Exception e) {
            System.err.println("Error loading progress: " + e.getMessage());
            e.printStackTrace();
            currentProgress = new GameProgress();
            return false;
        }
    }

    //hoan thanh level

    public void completeLevel (int level, int score, long playTime) {
        currentProgress.completeLevel(level, score, playTime);
        saveProgress();
    }

    //that bai level

    public void failLevel(int level) {
        currentProgress.failLevel(level);
        saveProgress();
    }

    /**
     * Kiểm tra level đã unlock chưa
     */
    public boolean isLevelUnlocked(int level) {
        return currentProgress.isLevelUnlocked(level);
    }

     /**
     * Reset toàn bộ tiến độ
     */
    public void resetProgress() {
        currentProgress.reset();
        saveProgress();
        System.out.println("Game progress has been reset.");
    }


     /**
     * Xóa file tiến độ
     */
    public void deleteProgressFile() {
        File file = new File(PROGRESS_FILE);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Progress file deleted.");
            }
        }
        currentProgress = new GameProgress();
    }
    


    
}

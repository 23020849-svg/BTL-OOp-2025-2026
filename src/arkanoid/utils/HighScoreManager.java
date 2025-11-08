package arkanoid.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private static final String SCORE_FILE = "highscores.dat";
    private List<ScoreEntry> scores;
    
    public static class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
        public String name;
        public int score;
        public long timestamp;
        
        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score);  // Giảm dần
        }
    }
    
    public HighScoreManager() {
        loadScores();
    }
    
    private void loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SCORE_FILE))) {
            scores = (List<ScoreEntry>) ois.readObject();
        } catch (Exception e) {
            scores = new ArrayList<>();
        }
    }
    
    public void addScore(String name, int score) {
        scores.add(new ScoreEntry(name, score));
        Collections.sort(scores);
        
        // Giữ top 100
        if (scores.size() > 100) {
            scores = scores.subList(0, 100);
        }
        
        saveScores();
    }
    
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SCORE_FILE))) {
            oos.writeObject(scores);
        } catch (Exception e) {
        }
    }
    
    public List<ScoreEntry> getTopScores(int count) {
        return scores.subList(0, Math.min(count, scores.size()));
    }
}
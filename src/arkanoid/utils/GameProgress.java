package arkanoid.utils;

import java.io.Serializable;

public class GameProgress implements Serializable {
    private static final long serialVersionUID = 1L;

    private int hightestLevelUnlocked; //level cao nhất đã mở khóa
    private int totalScore;
    private long totalPlayTime;

    //thông tin từng level
    private LevelProgress[] levelProgresses;


    // constructor mặc định

    public GameProgress() {
        this.hightestLevelUnlocked = 1;
        this.totalScore = 0;
        this.totalPlayTime = 0;
        this.levelProgresses = new LevelProgress[5]; //giả sử có tối đa


        //khởi tạo progess cho từng level
        for (int i=0; i< levelProgresses.length; i++) {
            levelProgresses[i] = new LevelProgress(i +1);
        }
    }

    // cập nhật tiến độ khi hoàn thành level

    public void completeLevel(int level, int score, long playTime) {
        if (level < 1 || level > levelProgresses.length) {
            return; //level không hợp lệ
        }

        LevelProgress lp = levelProgresses[level -1];
        lp.setCompleted(true);
        lp.incrementAttempts();

        if (score > lp.getBestScore()) {
            lp.setBestScore(score);
        }
        
        // Cập nhật best time nếu nhanh hơn
        if (!lp.hasBestTime() || playTime < lp.getBestTime()) {
            lp.setBestTime(playTime);
        }

         if (level >= hightestLevelUnlocked && level < levelProgresses.length) {
            hightestLevelUnlocked = level + 1;
        }

        totalScore += score;
        totalPlayTime += playTime;
    }

    public void failLevel(int level) {
        if (level < 1 || level > levelProgresses.length) {
            return; //level không hợp lệ
        }
        levelProgresses[level -1].incrementAttempts();
       
    }

    public boolean isLevelUnlocked(int level) {
        if (level < 1 || level > levelProgresses.length) {
            return false; //level không hợp lệ
        }
        return level <= hightestLevelUnlocked ;
    }

    /**
     * Lấy thông tin level
     */
    public LevelProgress getLevelProgress(int level) {
        if (level < 1 || level > levelProgresses.length) return null;
        return levelProgresses[level - 1];
    }

    //reset toan bo tien do

    public void reset() {
        this.hightestLevelUnlocked = 1;
        this.totalScore = 0;
        this.totalPlayTime = 0;

        for(int i=0; i<levelProgresses.length; i++) {
            levelProgresses[i] = new LevelProgress(i +1);
        }
    }

    public int getHightestLevelUnlocked() {
        return hightestLevelUnlocked;
    }

    public void setHightestLevelUnlocked(int hightestLevelUnlocked) {
        this.hightestLevelUnlocked = hightestLevelUnlocked;
    }

    public int getTotalScore() {
        return totalScore;
    }
    
   public long getTotalPlayTime() {
        return totalPlayTime;
    }

    public LevelProgress[] getLevelProgresses() {
        return levelProgresses;
    }

    //luu thong tin tien do tung level

    public static class LevelProgress implements Serializable {
        private static final long serialVersionUID = 1L;

        private int levelNumber;
        private boolean completed;
        private int bestScore;
        private long bestTime; //thời gian hoàn thành nhanh nhất
        private int attempts; //số lần thử
        private boolean hasBestTime;

        public LevelProgress(int levelNumber) {
            this.levelNumber = levelNumber;
            this.completed = false;
            this.bestScore = 0;
            this.bestTime = Long.MAX_VALUE; //không có thời gian tốt nhất ban đầu
            this.attempts = 0;
            this.hasBestTime = false;
        }
        // Getters and Setters
        public int getLevelNumber() { return levelNumber; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public int getBestScore() { return bestScore; }
        public void setBestScore(int bestScore) { this.bestScore = bestScore; }
        public long getBestTime() { return bestTime; }
        public void setBestTime(long bestTime) { 
            this.bestTime = bestTime;
            this.hasBestTime = true;
        }
        public boolean hasBestTime() { return hasBestTime; }
        public int getAttempts() { return attempts; }
        public void incrementAttempts() { this.attempts++; }
        
        public String getFormattedTime() {
            if (!hasBestTime) return "--:--";
            long seconds = bestTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }


    

}

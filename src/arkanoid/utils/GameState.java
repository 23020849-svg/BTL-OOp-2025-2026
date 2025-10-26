package arkanoid.utils;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int VERSION =2;
    public int version = VERSION;

    //tổng quan
    public int levelIndex;
    public int score;
    public int lives;

    //paddle
    public double paddleX;
    public double paddleY;
    public double paddleWidth;
    public boolean isPaddleExpanded;
    public long paddleExpandRemainMs;
    public List<BallState> balls;
    public List<BrickState> bricks;


    //ball
     public static class BallState implements Serializable {
        public double x, y, vx, vy;
        public double speedMultiplier; // để khôi phục cảm giác nhanh/chậm
        public long fastRemainMs;     
        public int radius;            

    }

   
    //bricks
    public static class BrickState implements Serializable {
        public int x, y, w, h;
        public int hp;
        public String type;
        public int row;
        public int col;
    }

    

   
    
}



import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
public class Renderer {
    public void draw(Graphics g, Paddle paddle, Ball ball, List<Brick> bricks, java.util.List<PowerUp> powerUps, int score, int lives) {
  
    // vẽ các thành phần
    paddle.render(g);
    ball.render(g);
    
    for (Brick brick : bricks) {
        brick.render(g);
    }

  }
}

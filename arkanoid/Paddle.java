import java.awt.Color;
import java.awt.Graphics;
public class Paddle extends MovableObject {
    private int speed;
    private int currentPowerUp;

    public Paddle(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.speed = 10;
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0; // giới hạn biên trái
    }

    public void moveRight() {
        x += speed;
        if (x + width > GameManager.WIDTH)
            x = GameManager.WIDTH - width; // giới hạn biên phải
    }

    public void applyPowerUp(int powerUpType) {
        //this.currentPowerUp = powerUpType;
        // Logic to apply power-up effects
    }
    
    @Override
    public void update() {
        // Update paddle state if needed
        if (x < 0) x = 0;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }


    

}

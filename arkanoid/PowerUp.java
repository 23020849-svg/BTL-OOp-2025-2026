import java.awt.Color;
import java.awt.Graphics;
public class PowerUp extends GameObject{
    //Các vật phẩm tăng sức mạnh rơi ra khi phá gạch
    private String type; //Loại power-up (ví dụ: mở rộng thanh, nhiều bóng, mạng thêm)
    private int duration; //Thời gian hiệu lực của power-up
    
    public PowerUp(int x, int y, int width, int height, String type, int duration) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
    }

    public void applyEffect(Paddle paddle) {
        // Logic to apply power-up effect to the paddle
    }

    public void removeEffect(Paddle paddle) {
        // Logic to remove power-up effect from the paddle
    }

    @Override
    public void update() {
        // Update power-up state if needed
    }

    @Override
    public void render(Graphics g) {
        // Vẽ power-up lên Graphics
    }



    
}

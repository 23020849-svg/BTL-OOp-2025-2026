import java.awt.*;

public class Paddle extends MovableObject{
      int speed;
      private String currentPowerUp;

      private final int leftBorder;
      private final int rightBorder;

      public Paddle(int x, int y, int width, int height,int speed, int leftBorder, int rightBorder) {
          super(x, y, width, height);
          this.speed = speed;
          this.leftBorder = leftBorder;
          this.rightBorder = rightBorder;
          this.currentPowerUp = "None";
      } 

      //the paddle vẽ nó ra
        @Override
         public void draw(Graphics2D g) {
        g.setColor(Color.PINK);
        g.fillRect(x, y, width, height);
    }

        //di chuyển qua trái
      public void moveLeft(){
        x-=speed;
        if(x<leftBorder) x=leftBorder;
      }

      public void moveRight(){
        x+=speed;
        if(x+width>rightBorder) x=rightBorder-width;
      }

       // kéo chuột: GameView gọi setX(), ta kẹp biên ở đây
    @Override
    public void setX(int newX) {
        this.x = Math.max(leftBorder, Math.min(newX, rightBorder - width));
    }
      public void applyPowerUp(String type){
        currentPowerUp=type;
      }
      public int getWidth() { return width; }
      public void setWidth(int with) { this.width = width; }

  

}

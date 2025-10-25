

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static javax.imageio.ImageIO.read;

public class Renderer {

	public final expandpaddle ex = new expandpaddle();
	public final fasst fastPanel = new fasst();
	public final extraball exball = new extraball();

    /** Vẽ toàn bộ frame: entities + HUD + overlay */
    public void draw(Graphics g,
                     Paddle paddle,
                     List<Ball> balls,
                     List<Brick> bricks,
                     List<PowerUp> powerUps,
                     int score,
                     int lives,
                     boolean ballLaunched,
                     double launchAngle,
                     boolean paused,
                     List<PowerUp> activePowerUps) {

        Graphics2D g2 = (Graphics2D) g;

        // Khử răng cưa
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // ===== Ball =====
        if (balls != null) {
            for (Ball ball : balls) { // Lặp qua để vẽ từng quả bóng
                if (ball == null) continue;

                Ellipse2D circle = ball.getShape();
                Color base= new Color(190, 60, 255);

                g2.setColor(Color.WHITE);
                g2.fill(circle);
                for(int i = 2; i >= 1; i--) {
                    float t = (float)i / 2f;
                    float alpha = 0.08f + 0.28f * t; //alpha từ 6% đen 40%
                    int a255 = (int)(alpha * 255);
                    g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), a255));
                    g2.setStroke(new BasicStroke(
                        5f + 8f * t,    //độ dày nét
                        BasicStroke.CAP_ROUND, //đầu nét tròn
                        BasicStroke.JOIN_ROUND)); //góc nối tròn
                    g2.draw(circle);
                }
            }
        }

        // ===== Bricks =====
        if (bricks != null) {
            for (Brick b : bricks) {
                if (b == null || b.isDestroyed()) continue;
                Rectangle r = b.getBounds();
              int hp= b.getHitPoints();
                Color base=colorForHP(hp);

                Graphics2D g2c = (Graphics2D) g2.create();
                int m=8;
                //g2c.setClip(r.x+m, r.y+m, r.width-2*m, r.height-2*m);
                drawNeonBrick(g2c, r, base);
                g2c.dispose();
            }
        }

        // ===== Paddle =====
        if (paddle != null) {
            Rectangle r = paddle.getBounds();
            float arc=10f;
            RoundRectangle2D rr = new RoundRectangle2D.Float(r.x+1f, r.y+1f, r.width-2f, r.height-2f, arc, arc);
            Color base= new Color(80,240,255);

            
            for(int i=2;i>=1;i--)
            {
                float t= (float)i/2f;
                float alpha = 0.03f + 0.18f*t; //alpha từ 6% đen 40%
                int a255=(int)(alpha*255);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), a255));
                g2.setStroke(new BasicStroke(
                    4f + 6f*t,    //độ dày nét
                    BasicStroke.CAP_ROUND, //đầu nét tròn
                    BasicStroke.JOIN_ROUND)); //góc nối tròn
                     g2.draw(rr);


            }
            //core
            g2.setColor(base);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(rr);

            //lõi trắng
            g2.setColor(Color.WHITE);       
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(rr);

            

        }

        // ===== PowerUps =====
        if (powerUps != null) {
    for (PowerUp p : powerUps) {
        if (p == null || !p.isActive()) continue;
        Rectangle r = p.getBounds();
           if (p instanceof ExpandPaddlePowerUp) {
        	   ImageIcon gif = ex.getGifIcon();
       	    if (gif != null) {
       	        Image img = gif.getImage();
       	        g2.drawImage(img, r.x, r.y,100,60 , null);
       	    }
        } else if (p instanceof FastBallPowerUp) {
        	 ImageIcon gif = fastPanel.getGifIcon();
        	    if (gif != null) {
        	        Image img = gif.getImage();
        	        g2.drawImage(img, r.x, r.y,100,60 , null);
        	    }
        } else if (p instanceof MultiBallPowerUp) {
        	 ImageIcon gif = exball.getGifIcon();
        	    if (gif != null) {
        	        Image img = gif.getImage();
        	        g2.drawImage(img, r.x, r.y,100,60 , null);
        	    }
        } else {
            // Mặc định: hình chữ nhật vàng + viền xám
            g2.setColor(Color.YELLOW);
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(r.x, r.y, r.width, r.height);
        }
    }
}

        // ===== HUD =====
        g2.setColor(Color.WHITE);
        int pad = 10;
        int y = pad + g2.getFontMetrics().getAscent();
        String scoreText = "Score: " + score;
        String livesText = "Lives: " + lives;
        g2.drawString(scoreText, pad, y);

        int w = (g.getClipBounds() != null) ? g.getClipBounds().width : GameManager.WIDTH;
        int h = (g.getClipBounds() != null) ? g.getClipBounds().height : GameManager.HEIGHT;
        g2.drawString(livesText, w - pad - g2.getFontMetrics().stringWidth(livesText), y);

        // Hiển thị tất cả PowerUp đang hoạt động
        int yOffset = 300;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        // Hiển thị thời gian PowerUp từ Paddle và Ball
        if (paddle.getExpandRemainingTime() > 0) {
            g.drawString("PowerUp: ExpandPaddlePowerUp – " + paddle.getExpandRemainingTime() + "s", 10, yOffset);
            yOffset += 30;
        }
        
        // Kiểm tra thời gian power-up từ danh sách balls
        if (balls != null && !balls.isEmpty()) {
            // Tất cả bóng đều có cùng hiệu ứng, nê chỉ cần kiểm tra bóng đầu tiên
            int fastTime = balls.get(0).getFastRemainingTime();
            if (fastTime > 0) {
                g.drawString("PowerUp: FastBallPowerUp - " + fastTime + "s", 10, yOffset);
                yOffset += 30;
            }
        }
        


        // ===== Overlay: hướng dẫn & mũi tên ngắm khi chưa bắn =====
        if (!ballLaunched && !balls.isEmpty()) {
            Ball firstBall = balls.get(0);
            g2.setColor(Color.WHITE);
            g2.drawString("Press SPACE to launch", w / 2 - 60, h / 2 - 10);

            g2.setColor(Color.RED);
            double rad = Math.toRadians(launchAngle);
            int cx = firstBall.getX() + firstBall.getWidth() /2;
            int cy = firstBall.getY() + firstBall.getHeight() / 2;
            int lineLength = 60;
            int endX = (int) (cx + lineLength * Math.cos(rad));
            int endY = (int) (cy + lineLength * Math.sin(rad));
            
            // vẽ đường ngắm nét đứt
            float thickness = 3f;
            float[] dash = {8f, 6f};
            Graphics2D g2c = (Graphics2D) g2.create();
            Color r= new Color(255,90,90);
            g2c.setColor(r);
            g2c.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dash, 0f));
            g2c.drawLine(cx, cy, endX, endY);
            g2.drawString("Use 4/6 to aim", w / 2 - 50, h / 2 + 20);
        }

        // ===== Overlay: PAUSED =====
        if (paused) {
            String text = "PAUSED";
            Font old = g2.getFont();
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h + fm.getAscent()) / 2 - 10;
            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);
            g2.setFont(old);
        }
    }

    // Màu gạch theo độ bền (HP)
    private Color colorForHP(int hp) {
        if (hp >= 3) return new Color(0x7f8c8d);
        if (hp == 2) return new Color(0x9b59b6);
        return Color.ORANGE;
    }



    //màu neon
    private void drawNeonBrick(Graphics2D g2, Rectangle r, Color base) {
       float arc=10f;
       RoundRectangle2D rr = new RoundRectangle2D.Float(r.x + 1f, r.y + 1f, r.width-2f, r.height-2f, arc, arc);
       g2.setColor(new Color(10,35,50));
       g2.fill(rr);

       Color halo=base.darker();
       Color core= base.brighter();

       int layers=3;
       for(int i=layers;i>=1;i--)
       {
        float t= (float)i/layers;
        float alpha = 0.03f + 0.18f*t; //alpha từ 6% đen 40%
        int a255=(int)(alpha*255);
        Color haloA = new Color(halo.getRed(), halo.getGreen(), halo.getBlue(), a255);
        g2.setColor(haloA);
        g2.setStroke(new BasicStroke(
            5f + 10f*t,    //độ dày nét
            BasicStroke.CAP_ROUND, //đầu nét tròn
            BasicStroke.JOIN_ROUND)); //góc nối tròn
             g2.draw(rr);
       }
       // viền core
       g2.setColor(core);
       g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
       g2.draw(rr);

       // Lõi trắng
       g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2.draw(rr);

    }

    // hình bao ngoài của power up
    private void renderCapsule(Graphics2D g2, int x, int y, int w, int h, Image arrowFrame) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        /* vỏ
        Shape outer = new RoundRectangle2D.Float(x, y, w, h, h, h);
        LinearGradientPaint metal = new LinearGradientPaint(
                x, y, x, y + h,
                new float[]{0f, 1f},
                new Color[]{new Color(255,255,255,200), new Color(200,200,200,50)}
        );
        g2.setPaint(metal);
        g2.fill(outer);

         */

        // ô bên trong
        int pad = 6;
        int iw = w - 2*pad;
        int ih = h - 2*pad;
        int ix = x + pad;
        int iy = y + pad;

        Shape inner = new RoundRectangle2D.Float(ix, iy, iw, ih, ih, ih);
        g2.setColor(new Color(20,20,22));
        g2.fill(inner);

        // mũi tên
        int imgW = arrowFrame.getWidth(null);
        int imgH = arrowFrame.getHeight(null);

        Shape oldClip = g2.getClip();
        g2.setClip(inner);
        if (imgW > 0 && imgH > 0) {
            g2.drawImage(arrowFrame, ix + (iw - imgW)/2, iy + (ih - imgH)/2, null);
        }
        g2.setClip(oldClip);
    }




}
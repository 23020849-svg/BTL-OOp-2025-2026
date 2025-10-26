package arkanoid.view;

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
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.ImageIcon;

import arkanoid.core.GameManager;
import arkanoid.entities.Ball;
import arkanoid.entities.Paddle;
import arkanoid.entities.bricks.Brick;
import arkanoid.entities.powerups.ExpandPaddlePowerUp;
import arkanoid.entities.powerups.FastBallPowerUp;
import arkanoid.entities.powerups.MultiBallPowerUp;
import arkanoid.entities.powerups.PowerUp;

public class Renderer {

    private arkanoid.view.expandpaddle ex =  new arkanoid.view.expandpaddle();
    private arkanoid.view.extraball exball = new arkanoid.view.extraball();
    private arkanoid.view.fasst fast = new arkanoid.view.fasst();
   
    private Image ballImage;
    private Image heart;
    private static final double BALL_SCALE = 2.5; // phóng to khi VẼ
    private static final int ARROW_GAP = 2;       // khoảng hở giữa mép bóng & mũi tên
    private static final int LIFE_ICON_SIZE = 35;

     public Renderer() {
        try {
            ballImage = new ImageIcon(getClass().getResource("/ball.png")).getImage();
            heart = new ImageIcon(getClass().getResource("/heart.png")).getImage();

        } catch (Exception e) {
            System.err.println("Không thể load ảnh ball.png: " + e.getMessage());
        }
    }
  
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
               int bx = (int) ball.getX();
                int by = (int) ball.getY();
                int bw = ball.getWidth();
                int bh = ball.getHeight();

                int cx = bx + bw / 2;
                int cy = by + bh / 2;
                int rLogic = Math.min(bw, bh) / 2;
                int rDraw  = (int) Math.round(rLogic * BALL_SCALE); // bán kính để VẼ (2.5x)

                // ---- Vệt sáng (trail) theo TÂM, tỉ lệ theo rDraw ----
                List<double[]> trail = ball.getTrail();
/*
 * Nếu trail đang lưu góc trên-trái thay vì tâm, dùng:
 *   double tx = pos[0] + bw/2.0;
 *   double ty = pos[1] + bh/2.0;
 * và thay (pos[0],pos[1]) bằng (tx,ty) bên dưới.
 */
                for (int i = 0; i < trail.size(); i++) {
                 double[] pos = trail.get(i);
                 float alpha = (float) (i + 1) / trail.size();

                int core = (int) (rDraw * (0.22 + 0.28 * alpha)); // cỡ đốm
                g2.setColor(new Color(236, 72, 153, (int)(alpha * 80))); // hồng đậm
                g2.fillOval((int)pos[0] - core, (int)pos[1] - core, core * 2, core * 2);

    // 2 lớp halo trắng
    for (int j = 2; j >= 1; j--) {
        float t = j / 2f;
        int haloA  = (int)(alpha * 50 * t);
        int haloSz = (int)(core * (1 + 0.55f * t));
        g2.setColor(new Color(255, 255, 255, haloA));
        g2.fillOval((int)pos[0] - haloSz, (int)pos[1] - haloSz, haloSz * 2, haloSz * 2);
    }
        }

        // ---- Vẽ ảnh bóng phóng to 2.5x, căn giữa theo (cx, cy) ----
        if (ballImage != null) {
    int drawD = rDraw * 2;
    g2.drawImage(ballImage, cx - rDraw, cy - rDraw, drawD, drawD, null);
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
                g2.drawImage(img, r.x, r.y, 100, 60, null);
            }

        } else if (p instanceof FastBallPowerUp) {
            ImageIcon gif = fast.getGifIcon();
            if (gif != null) {
                Image img = gif.getImage();
                g2.drawImage(img, r.x, r.y, 100, 60, null);
            }
        } else if (p instanceof MultiBallPowerUp) {
                ImageIcon gif = exball.getGifIcon();
                if (gif != null) {
                    Image img = gif.getImage();
                    g2.drawImage(img, r.x, r.y, 100, 60, null);
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
        //g2.setColor(Color.WHITE);
        //int pad = 10;
        //int y = pad + g2.getFontMetrics().getAscent();
        //String scoreText = "Score: " + score;
        //String livesText = "Lives: " + lives;
        //g2.drawString(scoreText, pad, y);

        int w = (g.getClipBounds() != null) ? g.getClipBounds().width : GameManager.WIDTH;
        int h = (g.getClipBounds() != null) ? g.getClipBounds().height : GameManager.HEIGHT;

        Font hudFont = MenuRenderer.loadCustomFont(22f);
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        int pad = 10;
        FontMetrics fm = g2.getFontMetrics();
        int textY = pad + fm.getAscent();

        //vẽ score ở góc trái
        String scoreText = "Score: " + score;
        g2.drawString(scoreText, pad, textY);
        drawLivesWithIcons(g2,lives,w,textY,fm,pad);


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
    int fbw = firstBall.getWidth();
    int fbh = firstBall.getHeight();
    int fcx = (int) firstBall.getX() + fbw / 2;
    int fcy = (int) firstBall.getY() + fbh / 2;
    int frLogic = Math.min(fbw, fbh) / 2;
    int frDraw  = (int) Math.round(frLogic * BALL_SCALE);

    double rad = Math.toRadians(launchAngle);
    int lineLength = 60;

    // Bắt đầu từ mép ảnh đã phóng to + khoảng hở
    int startX = (int) (fcx + (frDraw + ARROW_GAP) * Math.cos(rad));
    int startY = (int) (fcy + (frDraw + ARROW_GAP) * Math.sin(rad));
    int endX   = (int) (fcx + (frDraw + ARROW_GAP + lineLength) * Math.cos(rad));
    int endY   = (int) (fcy + (frDraw + ARROW_GAP + lineLength) * Math.sin(rad));

    Graphics2D g2c = (Graphics2D) g2.create();
    g2c.setColor(new Color(244, 63, 94)); // hồng đậm #F43F5E
    g2c.setStroke(new BasicStroke(
        3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
        new float[]{8f, 6f}, 0f
    ));
    g2c.drawLine(startX, startY, endX, endY);
    g2c.dispose();

    g2.setColor(Color.WHITE);
    g2.drawString("Press SPACE to launch", w / 2 - 60, h / 2 - 10);
    g2.drawString("Use 4/6 to aim",       w / 2 - 50, h / 2 + 20);
        }

        // ===== Overlay: PAUSED =====
        if (paused) {
            String text = "PAUSED";
            Font old = g2.getFont();
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics pm = g2.getFontMetrics();
            int tx = (w - pm.stringWidth(text)) / 2;
            int ty = (h + pm.getAscent()) / 2 - 10;
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

     private void drawLivesWithIcons(Graphics2D g2, int lives, int screenWidth, int textY, FontMetrics fm, int pad) {
        String livesLabel = "Lives: ";
        int labelWidth = fm.stringWidth(livesLabel);
        
        // Tính toán vị trí bắt đầu (căn phải)
        int totalIconsWidth = lives * (LIFE_ICON_SIZE + 5); // 5px spacing giữa các icon
        int startX = screenWidth - pad - labelWidth - totalIconsWidth;
        
        // Vẽ text "Lives: "
        g2.drawString(livesLabel, startX, textY);
        
        // Vẽ các icon bóng
        if (ballImage != null) {
            int iconX = startX + labelWidth;
            int iconY = textY - LIFE_ICON_SIZE + 3; // Căn giữa với text
            
            for (int i = 0; i < lives; i++) {
                g2.drawImage(heart, iconX, iconY+5, LIFE_ICON_SIZE, LIFE_ICON_SIZE, null);
                iconX += LIFE_ICON_SIZE + 5; // Khoảng cách giữa các icon
            }
        } else {
            // Fallback: vẽ hình tròn nếu không load được ảnh
            int iconX = startX + labelWidth;
            int iconY = textY - LIFE_ICON_SIZE / 2;
            
            for (int i = 0; i < lives; i++) {
                g2.setColor(new Color(236, 72, 153)); // Màu hồng giống trail
                g2.fillOval(iconX, iconY, LIFE_ICON_SIZE, LIFE_ICON_SIZE);
                g2.setColor(Color.WHITE);
                g2.drawOval(iconX, iconY, LIFE_ICON_SIZE, LIFE_ICON_SIZE);
                iconX += LIFE_ICON_SIZE + 5;
            }
        }
    }

    //hình bao ngoài của power up

    // hình bao ngoài của power up
private void renderCapsule(Graphics2D g2, int x, int y, int w, int h, Image arrowFrame) {
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // vỏ
    Shape outer = new RoundRectangle2D.Float(x, y, w, h, h, h);
    LinearGradientPaint metal = new LinearGradientPaint(
        x, y, x, y + h,
        new float[]{0f, 1f},
        new Color[]{new Color(255,255,255,200), new Color(200,200,200,50)}
    );
    g2.setPaint(metal);
    g2.fill(outer);

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
    //g2.setClip(inner);
    if (imgW > 0 && imgH > 0) {
        g2.drawImage(arrowFrame, ix + (iw - imgW)/2, iy + (ih - imgH)/2, null);
    }
    g2.setClip(oldClip);
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


}

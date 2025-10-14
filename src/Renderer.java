package src;

import java.awt.*;
import java.util.List;

public class Renderer {

    /** Vẽ toàn bộ frame: entities + HUD + overlay */
    public void draw(Graphics g,
            Paddle paddle,
            Ball ball,
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
        if (ball != null) {
            g2.setColor(Color.RED);
            g2.fill(ball.getShape());
        }

        // ===== Bricks =====
        if (bricks != null) {
            for (Brick b : bricks) {
                if (b == null || b.isDestroyed())
                    continue;
                Rectangle r = b.getBounds();
                g2.setColor(colorForHP(b.getHitPoints()));
                g2.fillRect(r.x, r.y, r.width, r.height);
                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(r.x, r.y, r.width, r.height);
            }
        }

        // ===== Paddle =====
        if (paddle != null) {
            g2.setColor(Color.BLUE);
            g2.fill(paddle.getBounds());
        }

        // ===== PowerUps =====
        if (powerUps != null) {
            for (PowerUp p : powerUps) {
                if (p == null || !p.isActive())
                    continue;

                Rectangle r = p.getBounds();

                if (p instanceof ExpandPaddlePowerUp) {
                    // Power-up mở rộng paddle: oval xanh lá + viền xám
                    g2.setColor(Color.GREEN);
                    g2.fillOval(r.x, r.y, r.width, r.height);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(r.x, r.y, r.width, r.height);

                } else if (p instanceof FastBallPowerUp) {
                    // Power-up tăng tốc bóng: oval xanh ngọc + viền xám
                    g2.setColor(Color.CYAN);
                    g2.fillOval(r.x, r.y, r.width, r.height);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawOval(r.x, r.y, r.width, r.height);

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

        // ===== Overlay: hướng dẫn & mũi tên ngắm khi chưa bắn =====
        if (!ballLaunched && ball != null) {
            g2.setColor(Color.WHITE);
            g2.drawString("Press SPACE to launch", w / 2 - 60, h / 2 - 10);

            g2.setColor(Color.RED);
            double rad = Math.toRadians(launchAngle);
            int cx = ball.getX() + ball.getWidth() / 2;
            int cy = ball.getY() + ball.getHeight() / 2;
            int lineLength = 60;
            int endX = (int) (cx + lineLength * Math.cos(rad));
            int endY = (int) (cy + lineLength * Math.sin(rad));
            g2.drawLine(cx, cy, endX, endY);
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
        // Hiển thị tất cả PowerUp đang hoạt động
        int yOffset = 300;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        for (PowerUp p : activePowerUps) { // activePowerUps là danh sách các power-up đang kích hoạt
            if (p.getRemainingTime() > 0) {
                g.drawString("PowerUp: " + p.getClass().getSimpleName() + " – " + p.getRemainingTime() + "s", 10,
                        yOffset);
                yOffset += 30; // Dịch xuống dòng kế tiếp
            }
        }

    }

    // Màu gạch theo độ bền (HP)
    private Color colorForHP(int hp) {
        if (hp >= 3)
            return new Color(0x7f8c8d);
        if (hp == 2)
            return new Color(0x9b59b6);
        return Color.ORANGE;
    }

}

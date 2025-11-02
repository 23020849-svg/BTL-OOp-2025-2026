package arkanoid.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

public class MenuRenderer {

    // Colors
    private static final Color BACKGROUND_COLOR = new Color(10, 15, 25);
    private static final Color PRIMARY_COLOR = new Color(80, 240, 255);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color SELECTED_COLOR = new Color(255, 200, 0);

    private static Font ARKANOID_TITLE_FONT = null;

    private Rectangle leaderboardButtonBounds = new Rectangle();

    public static Font loadCustomFont(float size) {

        if (ARKANOID_TITLE_FONT == null) {
            try {

                InputStream is = MenuRenderer.class.getResourceAsStream("/font.otf");

                if (is == null) {
                    throw new IOException("Font file not found in resources: /font.otf");
                }


                ARKANOID_TITLE_FONT = Font.createFont(Font.TRUETYPE_FONT, is);
                is.close();
            } catch (IOException | FontFormatException e) {
                e.printStackTrace();
                System.err.println("Không thể tải font tùy chỉnh. Sử dụng font Arial mặc định.");
                ARKANOID_TITLE_FONT = new Font("Arial", Font.BOLD, (int) size);
            }
        }

        // Trả về font với kích thước mong muốn
        return ARKANOID_TITLE_FONT.deriveFont(size);
    }


    /**
     * Vẽ menu chính
     */
    public void drawMainMenu(Graphics g, String[] options, int selectedOption, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/menu.gif"));
            Image img = icon.getImage();

            if (img != null) {
                g2.drawImage(img, 0, 0, screenWidth, screenHeight, null);
            } else {
                drawBackground(g2, screenWidth, screenHeight);
            }
        } catch (Exception e) {
            drawBackground(g2, screenWidth, screenHeight);
        }

        // Title
        drawTitle(g2, screenWidth, screenHeight);
        
        // Menu options
        drawMenuOptions(g2, options, selectedOption, screenWidth, screenHeight);
       
        // Instructions
        drawFooterInstructions(g2, screenWidth, screenHeight);
    }

    private void drawTitle(Graphics2D g2, int screenWidth, int screenHeight) {
        String title = "ARKANOID";
        int y = 200;
        float titleSize = 100f; // Tăng kích thước tiêu đề

        // Lấy Font tùy chỉnh
        Font customFont = loadCustomFont(titleSize);
        g2.setFont(customFont);

        FontMetrics fm = g2.getFontMetrics();
        int x = (screenWidth - fm.stringWidth(title)) / 2;


        for (int i = 3; i >= 1; i--) {

            g2.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50 / i));


            g2.setFont(customFont.deriveFont(titleSize + i * 2));

            FontMetrics glowFm = g2.getFontMetrics();
            int glowX = (screenWidth - glowFm.stringWidth(title)) / 2;
            int glowY = y + i * 2;
            g2.drawString(title, glowX, glowY);
        }

        // Main title (Màu chính: PRIMARY_COLOR)
        g2.setColor(PRIMARY_COLOR);
        g2.setFont(customFont.deriveFont(titleSize));
        g2.drawString(title, x, y);
    }


    /**
     * Vẽ các tùy chọn menu
     */
    private void drawMenuOptions(Graphics2D g2, String[] options, int selectedOption, int screenWidth, int screenHeight) {
        int startY = 350;
        int spacing = 60;


        Font menuFont = loadCustomFont(28f).deriveFont(Font.PLAIN);

        for (int i = 0; i < options.length; i++) {
            int y = startY + i * spacing;

            if (i == selectedOption) {
                // Selected option - highlighted
                drawSelectedOption(g2, options[i], y, screenWidth);
            } else {
                // Normal option
                g2.setColor(TEXT_COLOR);
                g2.setFont(menuFont); // ÁP DỤNG FONT TÙY CHỈNH
                FontMetrics fm = g2.getFontMetrics();
                int x = (screenWidth - fm.stringWidth(options[i])) / 2;
                g2.drawString(options[i], x, y);
            }
        }
    }

    /**
     * Vẽ tùy chọn được chọn với hiệu ứng
     */
    private void drawSelectedOption(Graphics2D g2, String text, int y, int screenWidth) {
        Font selectedFont = loadCustomFont(28f).deriveFont(Font.BOLD); // ÁP DỤNG FONT TÙY CHỈNH
        g2.setFont(selectedFont);

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (screenWidth - textWidth) / 2;

        // Background highlight
        RoundRectangle2D background = new RoundRectangle2D.Float(
                x - 20, y - 30, textWidth + 40, 40, 20, 20
        );

        // Glow effect (giữ nguyên màu SELECTED_COLOR)
        g2.setColor(new Color(SELECTED_COLOR.getRed(), SELECTED_COLOR.getGreen(), SELECTED_COLOR.getBlue(), 50));
        g2.fill(background);

        // Border
        g2.setColor(SELECTED_COLOR);
        g2.setStroke(new BasicStroke(2));
        g2.draw(background);

        // Text
        g2.setColor(SELECTED_COLOR);
        g2.drawString(text, x, y);
    }

    // vẽ Leaderboard
    private void drawLeaderboardButton(Graphics2D g2, int width, int height){
        String label = "BẢNG XẾP HẠNG";
        Font btnFont = loadCustomFont(20f).deriveFont(Font.BOLD);
        g2.setFont(btnFont);

        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(label);
        int textH = fm.getAscent();

        int padX = 22;
        int padY= 14;
        int w = textW + padX*2;
        int h = textH + padY;
         int x = Math.max(24, width - w - 36);   // cách mép phải 36px
        int y = Math.max(24, height - h - 36);  // cách mép dưới 36px
        
        RoundRectangle2D pill = new RoundRectangle2D.Float(x,y,w,h,h,h);

        g2.setColor(new Color(255,255,255,28));
        g2.fill(pill);
        g2.setColor(PRIMARY_COLOR);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(pill);
        g2.setColor(PRIMARY_COLOR);
        int tx = x + (w - textW) / 2;
        int ty = y + (h + textH) / 2 - 3;
        g2.drawString(label, tx, ty);

        // save bounds (nới thêm 4px cho dễ bấm)
        leaderboardButtonBounds = new Rectangle(x - 4, y - 4, w + 8, h + 8);


    }

    /**
     * Vẽ hướng dẫn ở cuối màn hình (Giữ nguyên font Arial nhỏ)
     */
    private void drawFooterInstructions(Graphics2D g2, int screenWidth, int screenHeight) {
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        String instruction = "Use ↑↓ or MOUSE to navigate, ENTER or CLICK to select, ESC to go back";
        FontMetrics fm = g2.getFontMetrics();
        int x = (screenWidth - fm.stringWidth(instruction)) / 2;
        int y = screenHeight - 30;
        g2.drawString(instruction, x, y);
    }


    public void drawSettings(Graphics g, boolean soundEnabled, int difficulty, String[] difficultyNames, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        drawBackground(g2, screenWidth, screenHeight);

        // Title
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String title = "SETTINGS";
        int titleX = (screenWidth - fm.stringWidth(title)) / 2;
        int titleY = 150;
        g2.drawString(title, titleX, titleY);

        // Sound setting
        int y = 250;
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        String soundText = "Sound: " + (soundEnabled ? "ON" : "OFF");
        g2.setColor(soundEnabled ? PRIMARY_COLOR : Color.GRAY);
        g2.drawString(soundText, 100, y);


        y += 60;
        g2.setColor(TEXT_COLOR);
        String diffText = "Difficulty: " + difficultyNames[difficulty - 1];
        g2.drawString(diffText, 100, y);


        int indicatorX = 400;
        int indicatorY = y - 20;
        for (int i = 0; i < 3; i++) {
            Color color = (i < difficulty) ? PRIMARY_COLOR : Color.GRAY;
            g2.setColor(color);
            g2.fillOval(indicatorX + i * 30, indicatorY, 15, 15);
        }

        // Instructions
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("Use LEFT/RIGHT to change difficulty", 100, y + 40);
        g2.drawString("Press ENTER to toggle sound", 100, y + 60);
        g2.drawString("Press ESC to return to main menu", 100, y + 80);
    }

    /**
     * Vẽ hướng dẫn chơi
     */
    public void drawInstructions(Graphics g, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        drawBackground(g2, screenWidth, screenHeight);

        // Title
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g2.getFontMetrics();
        String title = "HOW TO PLAY";
        int titleX = (screenWidth - fm.stringWidth(title)) / 2;
        int titleY = 100;
        g2.drawString(title, titleX, titleY);

        // Instructions
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(TEXT_COLOR);

        String[] instructions = {
                "CONTROLS:",
                "• ← / → or MOUSE : Move Paddle",
                "• SPACE or CLICK : Launch Ball",
                "• 4 / 6 : Aim before launching",
                "• R : Restart Game",
                "• ESC : Pause/Resume",
                "",
                "POWER-UPS:",
                "• Green : Expand Paddle",
                "• Cyan : Fast Ball",
                "",
                "OBJECTIVE:",
                "• Break all bricks to advance to next level",
                "• Don't let the ball fall off the screen",
                "• Collect power-ups for advantages"
        };

        int y = titleY + 60;
        for (String instruction : instructions) {
            if (instruction.startsWith("•")) {
                g2.setColor(PRIMARY_COLOR);
            } else if (instruction.endsWith(":")) {
                g2.setColor(SELECTED_COLOR);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
            } else {
                g2.setColor(TEXT_COLOR);
                g2.setFont(new Font("Arial", Font.PLAIN, 20));
            }

            g2.drawString(instruction, 100, y);
            y += 30;
        }

        // Return instruction
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("Press ESC to return to main menu", screenWidth - 300, screenHeight - 50);
    }

    // Vẽ Custom
    public void drawCustom(Graphics g, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(new Color(20, 20, 20)); // nền tối
        drawBackground(g2, screenWidth, screenHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("🎨 CUSTOM COLOR MENU", 150, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press [P] to change Paddle color", 180, 180);
        g.drawString("Press [B] to change Ball color", 180, 220);
        g.drawString("Press [ESC] to return", 180, 260);
    }

    /**
     * Vẽ overlay tạm dừng
     */
    public void drawPauseOverlay(Graphics g, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Pause text
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String text = "PAUSED";
        int x = (screenWidth - fm.stringWidth(text)) / 2;
        int y = (screenHeight + fm.getAscent()) / 2 - 50;
        g2.drawString(text, x, y);

        // Instructions
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(Color.GRAY);
        String instruction = "Press ESC to resume";
        fm = g2.getFontMetrics();
        x = (screenWidth - fm.stringWidth(instruction)) / 2;
        y += 60;
        g2.drawString(instruction, x, y);
    }

    /**
     * Vẽ đếm ngược khi bắt đầu game
     */
    public void drawCountdown(Graphics g, int countdownValue, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Countdown text
        String countdownText = String.valueOf(countdownValue);
        
        // Sử dụng font tùy chỉnh với kích thước lớn
        Font countdownFont = loadCustomFont(200f).deriveFont(Font.BOLD);
        g2.setFont(countdownFont);
        
        FontMetrics fm = g2.getFontMetrics();
        int x = (screenWidth - fm.stringWidth(countdownText)) / 2;
        int y = (screenHeight + fm.getAscent()) / 2;

        // Vẽ hiệu ứng glow cho số đếm ngược
        for (int i = 5; i >= 1; i--) {
            float alpha = 0.1f + 0.15f * (6 - i) / 5f;
            g2.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), (int)(alpha * 255)));
            g2.setFont(countdownFont.deriveFont(200f + i * 10));
            
            FontMetrics glowFm = g2.getFontMetrics();
            int glowX = (screenWidth - glowFm.stringWidth(countdownText)) / 2;
            int glowY = (screenHeight + glowFm.getAscent()) / 2;
            g2.drawString(countdownText, glowX, glowY);
        }

        // Vẽ số chính
        g2.setColor(PRIMARY_COLOR);
        g2.setFont(countdownFont);
        g2.drawString(countdownText, x, y);

        // Vẽ text "GET READY" khi countdown = 3
        if (countdownValue == 3) {
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            fm = g2.getFontMetrics();
            String readyText = "GET READY!";
            int readyX = (screenWidth - fm.stringWidth(readyText)) / 2;
            int readyY = y - 100;
            g2.drawString(readyText, readyX, readyY);
        }
    }

    /**
     * Vẽ màn hình Game Over
     */
    public void drawGameOver(Graphics g, int score, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        drawBackground(g2, screenWidth, screenHeight);

        // Game Over text
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String text = "GAME OVER";
        int x = (screenWidth - fm.stringWidth(text)) / 2;
        int y = 200;
        g2.drawString(text, x, y);

        // Score
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        String scoreText = "Final Score: " + score;
        fm = g2.getFontMetrics();
        x = (screenWidth - fm.stringWidth(scoreText)) / 2;
        y += 80;
        g2.drawString(scoreText, x, y);

        // Continue instruction
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String instruction = "Press ENTER to return to main menu";
        fm = g2.getFontMetrics();
        x = (screenWidth - fm.stringWidth(instruction)) / 2;
        y += 100;
        g2.drawString(instruction, x, y);
    }

    /**
     * Vẽ background với gradient
     */
    private void drawBackground(Graphics2D g2, int screenWidth, int screenHeight) {
        // Dark background
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Gradient effect
        for (int i = 0; i < screenHeight; i++) {
            float ratio = (float) i / screenHeight;
            Color color = new Color(
                    (int) (BACKGROUND_COLOR.getRed() + (PRIMARY_COLOR.getRed() - BACKGROUND_COLOR.getRed()) * ratio * 0.1),
                    (int) (BACKGROUND_COLOR.getGreen() + (PRIMARY_COLOR.getGreen() - BACKGROUND_COLOR.getGreen()) * ratio * 0.1),
                    (int) (BACKGROUND_COLOR.getBlue() + (PRIMARY_COLOR.getBlue() - BACKGROUND_COLOR.getBlue()) * ratio * 0.1)
            );
            g2.setColor(color);
            g2.drawLine(0, i, screenWidth, i);
        }
    }
    public Rectangle getLeaderboardButtonBounds() {
    return new Rectangle(leaderboardButtonBounds);
    }
}
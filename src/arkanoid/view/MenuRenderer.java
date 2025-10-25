package arkanoid.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;

public class MenuRenderer {

    private static final int WIDTH = 1440;
    private static final int HEIGHT = 800;

    // Colors
    private static final Color BACKGROUND_COLOR = new Color(10, 15, 25);
    private static final Color PRIMARY_COLOR = new Color(80, 240, 255);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color SELECTED_COLOR = new Color(255, 200, 0);

    private static Font ARKANOID_TITLE_FONT = null;


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
    public void drawMainMenu(Graphics g, String[] options, int selectedOption) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/menu.gif"));
            Image img = icon.getImage();

            if (img != null) {
                g2.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
            } else {
                drawBackground(g2);
            }
        } catch (Exception e) {
            drawBackground(g2);
        }

        // Title
        drawTitle(g2);

        // Menu options
        drawMenuOptions(g2, options, selectedOption);

        // Instructions
        drawFooterInstructions(g2);
    }

    private void drawTitle(Graphics2D g2) {
        String title = "ARKANOID";
        int y = 200;
        float titleSize = 200f; // Tăng kích thước tiêu đề

        // Lấy Font tùy chỉnh
        Font customFont = loadCustomFont(titleSize);
        g2.setFont(customFont);

        FontMetrics fm = g2.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(title)) / 2;


        for (int i = 3; i >= 1; i--) {

            g2.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50 / i));


            g2.setFont(customFont.deriveFont(titleSize + i * 2));

            FontMetrics glowFm = g2.getFontMetrics();
            int glowX = (WIDTH - glowFm.stringWidth(title)) / 2;
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
    private void drawMenuOptions(Graphics2D g2, String[] options, int selectedOption) {
        int startY = 350;
        int spacing = 60;

        // Font tùy chỉnh cho menu options (cỡ nhỏ hơn title)
        Font menuFont = loadCustomFont(28f).deriveFont(Font.PLAIN);

        for (int i = 0; i < options.length; i++) {
            int y = startY + i * spacing;

            if (i == selectedOption) {
                // Selected option - highlighted
                drawSelectedOption(g2, options[i], y);
            } else {
                // Normal option
                g2.setColor(TEXT_COLOR);
                g2.setFont(menuFont); // ÁP DỤNG FONT TÙY CHỈNH
                FontMetrics fm = g2.getFontMetrics();
                int x = (WIDTH - fm.stringWidth(options[i])) / 2;
                g2.drawString(options[i], x, y);
            }
        }
    }

    /**
     * Vẽ tùy chọn được chọn với hiệu ứng
     */
    private void drawSelectedOption(Graphics2D g2, String text, int y) {
        Font selectedFont = loadCustomFont(28f).deriveFont(Font.BOLD); // ÁP DỤNG FONT TÙY CHỈNH
        g2.setFont(selectedFont);

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (WIDTH - textWidth) / 2;

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

    /**
     * Vẽ hướng dẫn ở cuối màn hình (Giữ nguyên font Arial nhỏ)
     */
    private void drawFooterInstructions(Graphics2D g2) {
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        String instruction = "Use ↑↓ to navigate, ENTER to select, ESC to go back";
        FontMetrics fm = g2.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(instruction)) / 2;
        int y = HEIGHT - 30;
        g2.drawString(instruction, x, y);
    }


    public void drawSettings(Graphics g, boolean soundEnabled, int difficulty, String[] difficultyNames) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        drawBackground(g2);

        // Title
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String title = "SETTINGS";
        int titleX = (WIDTH - fm.stringWidth(title)) / 2;
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
    public void drawInstructions(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        drawBackground(g2);

        // Title
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g2.getFontMetrics();
        String title = "HOW TO PLAY";
        int titleX = (WIDTH - fm.stringWidth(title)) / 2;
        int titleY = 100;
        g2.drawString(title, titleX, titleY);

        // Instructions
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(TEXT_COLOR);

        String[] instructions = {
                "CONTROLS:",
                "• ← / → : Move Paddle",
                "• SPACE : Launch Ball",
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
        g2.drawString("Press ESC to return to main menu", WIDTH - 300, HEIGHT - 50);
    }

    /**
     * Vẽ overlay tạm dừng
     */
    public void drawPauseOverlay(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // Pause text
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String text = "PAUSED";
        int x = (WIDTH - fm.stringWidth(text)) / 2;
        int y = (HEIGHT + fm.getAscent()) / 2 - 50;
        g2.drawString(text, x, y);

        // Instructions
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(Color.GRAY);
        String instruction = "Press ESC to resume";
        fm = g2.getFontMetrics();
        x = (WIDTH - fm.stringWidth(instruction)) / 2;
        y += 60;
        g2.drawString(instruction, x, y);
    }

    /**
     * Vẽ màn hình Game Over
     */
    public void drawGameOver(Graphics g, int score) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        drawBackground(g2);

        // Game Over text
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String text = "GAME OVER";
        int x = (WIDTH - fm.stringWidth(text)) / 2;
        int y = 200;
        g2.drawString(text, x, y);

        // Score
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        String scoreText = "Final Score: " + score;
        fm = g2.getFontMetrics();
        x = (WIDTH - fm.stringWidth(scoreText)) / 2;
        y += 80;
        g2.drawString(scoreText, x, y);

        // Continue instruction
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String instruction = "Press ENTER to return to main menu";
        fm = g2.getFontMetrics();
        x = (WIDTH - fm.stringWidth(instruction)) / 2;
        y += 100;
        g2.drawString(instruction, x, y);
    }

    /**
     * Vẽ background với gradient
     */
    private void drawBackground(Graphics2D g2) {
        // Dark background
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // Gradient effect
        for (int i = 0; i < HEIGHT; i++) {
            float ratio = (float) i / HEIGHT;
            Color color = new Color(
                    (int) (BACKGROUND_COLOR.getRed() + (PRIMARY_COLOR.getRed() - BACKGROUND_COLOR.getRed()) * ratio * 0.1),
                    (int) (BACKGROUND_COLOR.getGreen() + (PRIMARY_COLOR.getGreen() - BACKGROUND_COLOR.getGreen()) * ratio * 0.1),
                    (int) (BACKGROUND_COLOR.getBlue() + (PRIMARY_COLOR.getBlue() - BACKGROUND_COLOR.getBlue()) * ratio * 0.1)
            );
            g2.setColor(color);
            g2.drawLine(0, i, WIDTH, i);
        }
    }
}
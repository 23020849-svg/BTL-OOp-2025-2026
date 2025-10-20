package src;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;

public class LevelLoader {

    public List<Brick> loadLevel(int levelNumber) {
        List<Brick> bricks = new ArrayList<>();
        String filePath = "src/levels/level_" + levelNumber + ".txt"; // Đường dẫn tới file level

        int brickW = (GameManager.WIDTH - 50) / 10; // 10 cột
        int brickH = 25;
        int startX = 30;
        int startY = 60;

        try (InputStream is = new FileInputStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                // Tính toán lại chiều rộng và lề trái cho vừa với số cột của level
                int cols = line.length();
                brickW = (GameManager.WIDTH - 50) / cols;
                startX = (GameManager.WIDTH - (cols * brickW) + (cols*4)) / 2;

                for (int col = 0; col < cols; col++) {
                    char symbol = line.charAt(col);
                    if (symbol == '-') continue; // Bỏ qua nếu là ô trống

                    int x = startX + col * brickW;
                    int y = startY + row * (brickH + 6);

                    switch (symbol) {
                        case '1':
                            bricks.add(new NormalBrick(x, y, brickW - 4, brickH));
                            break;
                        case '2':
                            bricks.add(new StrongBrick(x, y, brickW - 4, brickH, 2));
                            break;
                        case '3':
                            bricks.add(new StrongBrick(x, y, brickW - 4, brickH, 3));
                            break;
                        case 'X':
                            bricks.add(new UnbreakableBrick(x, y, brickW - 4, brickH));
                            break;
                    }
                }
                row++;
            }
        } catch (Exception e) {
            System.err.println("Không thể tải level: " + filePath);
            e.printStackTrace();
        }

        return bricks;
    }
}

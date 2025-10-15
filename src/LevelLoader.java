package src;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    public List<Brick> loadLevel(int levelNumber) {
        List<Brick> bricks = new ArrayList<>();
        String fileName = "/levels/level_" + levelNumber + ".txt"; // Đường dẫn tới file level

        int brickH = 25;
        int startX = 30;
        int startY = 60;

        try (InputStream is = getClass().getResourceAsStream(fileName);
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
                        
                    }
                }
            }
             }
    }
    
}

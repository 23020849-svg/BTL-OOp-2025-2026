package src; // Đặt class trong package arkanoid

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import javax.swing.*;
import javax.swing.border.Border;

// ======= Lớp Main =======
// Đây là điểm khởi đầu (entry point) của toàn bộ chương trình Arkanoid.
public class Main {

    // ======= Hàm main =======
    // Là nơi chương trình bắt đầu chạy.
    public static void main(String[] args) {

        // invokeLater đảm bảo khởi tạo giao diện trong luồng Event Dispatch Thread (EDT),
        // giúp tránh lỗi khi xử lý Swing không thread-safe.
        SwingUtilities.invokeLater(() -> {

            // ======= 1. Tạo cửa sổ JFrame =======
            JFrame frame = new JFrame("Arkanoid"); // Tiêu đề cửa sổ game
            ImageIcon logo = new ImageIcon("rsc/logo.png");
            frame.setIconImage(logo.getImage());
            // ======= 3. Cấu hình khung cửa sổ =======
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát chương trình khi đóng cửa sổ
            frame.setResizable(false); // Không cho phép thay đổi kích thước cửa sổ
            // add image
            ImageIcon icon = new ImageIcon("rsc/background.jpg");
            // border
            Border border = BorderFactory.createLineBorder(Color.blue,2);

            Image img = icon.getImage();
            Image scaled = img.getScaledInstance(1440, 800, Image.SCALE_SMOOTH);
            JLabel background = new JLabel(new ImageIcon(scaled));
            background.setBorder(border);
            background.setLayout(new BorderLayout());
            frame.setContentPane(background);
           
           MenuManager menu = new MenuManager();
           background.add(menu, BorderLayout.CENTER);
           
           
            //frame.add(game);           // Thêm panel GameManager vào frame (nơi vẽ game)

            // pack() tự động điều chỉnh kích thước cửa sổ vừa với kích thước panel bên trong
            frame.pack();
            
            // Đặt cửa sổ ra giữa màn hình
            frame.setLocationRelativeTo(null);

            // Hiển thị cửa sổ
            frame.setVisible(true);

            // Menu system handles all instructions and game flow
        });
    }
}

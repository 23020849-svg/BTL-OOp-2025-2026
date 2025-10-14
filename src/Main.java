package src; // Đặt class trong package arkanoid

import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.*;

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
            JFrame frame = new JFrame("Arkanoid - OOP GUI Version"); // Tiêu đề cửa sổ game

            // ======= 3. Cấu hình khung cửa sổ =======
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát chương trình khi đóng cửa sổ
            frame.setResizable(false); // Không cho phép thay đổi kích thước cửa sổ
           
            // add image
            ImageIcon icon = new ImageIcon("rsc/background.jpg");
            Image img = icon.getImage();
            Image scaled = img.getScaledInstance(1440, 800, Image.SCALE_SMOOTH);
            JLabel background = new JLabel(new ImageIcon(scaled));
            background.setLayout(new BorderLayout());
            frame.setContentPane(background);
           
           GameManager game = new GameManager();
           background.add(game, BorderLayout.CENTER);
           
           
            //frame.add(game);           // Thêm panel GameManager vào frame (nơi vẽ game)

            // pack() tự động điều chỉnh kích thước cửa sổ vừa với kích thước panel bên trong
            frame.pack();
            
            // Đặt cửa sổ ra giữa màn hình
            frame.setLocationRelativeTo(null);

            // Hiển thị cửa sổ
            frame.setVisible(true);

            // ======= 4. Hiển thị hướng dẫn điều khiển (bảng thông báo ban đầu) =======
            JOptionPane.showMessageDialog(
                    frame,
                    // Nội dung hướng dẫn điều khiển
                    "Controls:\n← / → : Move Paddle\nSPACE : Launch Ball\nR : Restart\nP: Pause\n\n" +
                            "Power-ups:\nGreen = Expand Paddle\nCyan = Fast Ball",
                    "Instructions", // Tiêu đề của hộp thoại
                    JOptionPane.INFORMATION_MESSAGE // Loại thông báo: thông tin
            );
        });
    }
}

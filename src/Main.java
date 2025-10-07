package arkanoid; // Đặt class trong package arkanoid

/**
 * Main.java
 *
 * Lớp main riêng biệt khởi tạo cửa sổ game và chạy Arkanoid.
 */
import javax.swing.*; // Thư viện Swing cho GUI

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

            // ======= 2. Khởi tạo GameManager (panel chính của game) =======
            GameManager game = new GameManager();

            // ======= 3. Cấu hình khung cửa sổ =======
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát chương trình khi đóng cửa sổ
            frame.setResizable(false); // Không cho phép thay đổi kích thước cửa sổ
            frame.add(game);           // Thêm panel GameManager vào frame (nơi vẽ game)

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
                    "Controls:\n← / → : Move Paddle\nSPACE : Launch Ball\nR : Restart\n\n" +
                            "Power-ups:\nGreen = Expand Paddle\nCyan = Fast Ball",
                    "Instructions", // Tiêu đề của hộp thoại
                    JOptionPane.INFORMATION_MESSAGE // Loại thông báo: thông tin
            );
        });
    }
}

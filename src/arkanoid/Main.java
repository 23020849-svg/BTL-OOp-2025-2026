package arkanoid; // Đặt class trong package arkanoid

import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import arkanoid.core.MenuManager;

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
            // ======= 2. Cấu hình full màn hình =======
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát chương trình khi đóng cửa sổ
            frame.setUndecorated(true); // Bỏ viền và thanh tiêu đề
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Phóng to toàn màn hình

            // ======= 3. Xử lý ảnh nền (background) =======
            // Lấy kích thước màn hình thực tế
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            // add image
            ImageIcon icon = new ImageIcon("rsc/testbg.jpg");
            Image img = icon.getImage();

            // Scale ảnh nền cho vừa đúng kích thước màn hình
            Image scaled = img.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);

            JLabel background = new JLabel(new ImageIcon(scaled));

            // Dùng BorderLayout để panel game tự động lấp đầy
            background.setLayout(new BorderLayout());
            frame.setContentPane(background);

            // ======= 4. Thêm Game vào giữa màn hình =======
            MenuManager menu = new MenuManager(frame, background, img);

            //Thêm panel game vào giữa
            background.add(menu, BorderLayout.CENTER);


            // Hiển thị cửa sổ
            frame.setVisible(true);

            // Menu system handles all instructions and game flow
        });
    }
}

package arkanoid.utils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class Sound {
    private Clip clip;

    // Hàm load file âm thanh từ resources
    public boolean loadSound(String resourcePath) {
        try {
            // Load từ classpath resources
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);

            if (audioSrc == null) {
                System.out.println("Không tìm thấy file âm thanh: " + resourcePath);
                return false;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioSrc);
            clip = AudioSystem.getClip();
            clip.open(audioIn);

            System.out.println("Load âm thanh thành công: " + resourcePath);
            return true;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Lỗi khi load âm thanh: " + resourcePath);
            e.printStackTrace();
            return false;
        }
    }

    // Phát một lần
    public void playOnce() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    // Phát lặp lại (nhạc nền)
    public void playLoop() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Dừng phát
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
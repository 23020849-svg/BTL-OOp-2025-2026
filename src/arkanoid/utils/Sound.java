package arkanoid.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
public class Sound {
     private Clip clip;

    // Hàm load file âm thanh
    public void loadSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Phát một lần
    public void playOnce() {
        if (clip != null) {
            clip.setFramePosition(0); // quay lại đầu file
            clip.start();
        }
    }

    // Phát lặp lại (nhạc nền)
    public void playLoop() {
        if (clip != null) {
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



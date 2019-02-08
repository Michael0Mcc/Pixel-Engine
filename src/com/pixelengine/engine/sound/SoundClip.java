package com.pixelengine.engine.sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundClip {
    public Clip clip = null;
    public FloatControl gainControl;

    public SoundClip(String path) {
        try {
            InputStream audioSource = SoundClip.class.getResourceAsStream(path);
            InputStream bufferedInput = new BufferedInputStream(audioSource);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedInput);
            AudioFormat baseFormat = inputStream.getFormat();
            AudioFormat decodeFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );
            AudioInputStream decodedInputStream = AudioSystem.getAudioInputStream(decodeFormat, inputStream);

            clip = AudioSystem.getClip();
            clip.open(decodedInputStream);

            gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip == null) { return; }

        stop();
        clip.setFramePosition(0);
        while (!clip.isRunning()) {
            clip.start();
        }
    }

    public void stop() {
        if (clip.isRunning()) {
            clip.stop();
        }
    }

    public void close() {
        stop();
        clip.drain();
        clip.close();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        play();
    }

    public void setVolume(float db) {
        gainControl.setValue(db);
    }

    public boolean isRunning() {
        return clip.isRunning();
    }
}

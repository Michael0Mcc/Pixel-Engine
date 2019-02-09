package com.pixelengine.game;

import com.pixelengine.engine.PixelEngine;
import com.pixelengine.engine.gfx.Image;
import com.pixelengine.engine.gfx.ImageTile;
import com.pixelengine.engine.sound.SoundClip;

import java.awt.event.KeyEvent;

public class Main {

    private ImageTile image = new ImageTile("/alpha.png", 64, 64);
    private Image image2 = new Image("/wolf.png");
    private ImageTile imageTile = new ImageTile("/tile-set.png", 32, 32);
    private SoundClip clip = new SoundClip("/audio/van-door.wav");

    public void init(PixelEngine pixelEngine) {
        pixelEngine.createWindow(640, 360, 2, 2, "Pixel Engine v1.0");
        image.setAlpha(true);
    }

    public void loop(PixelEngine pixelEngine) {
        pixelEngine.drawWindow();
//        pixelEngine.drawImageTile(imageTile, 20, 20, 14, 10);
        pixelEngine.setzDepth(1);
        pixelEngine.drawImageTile(image, pixelEngine.mouseX(), pixelEngine.mouseY(), 0, 0);
        pixelEngine.setzDepth(0);
        pixelEngine.drawImage(image2, 20, 20);
        if (pixelEngine.isKeyDown(KeyEvent.VK_A)) {
            clip.play();
            clip.setVolume(-10.0f);
        }

        pixelEngine.drawText("Hello World", 0, 0, 0xff00ff00);
    }
}

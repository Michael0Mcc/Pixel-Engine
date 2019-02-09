package com.pixelengine.engine;

import com.pixelengine.engine.gfx.Image;
import com.pixelengine.engine.gfx.ImageTile;
import com.pixelengine.game.Main;

public class PixelEngine implements Runnable {
    private Thread thread;
    private static PixelEngine pixelEngine = new PixelEngine();
    private static Main mainProgram = new Main();

    private Window window;
    private RenderEngine renderer;
    private Input input;


    private boolean running = false;
    private final double UPDATE_CAP = 1.0/60.0;

    private void start() {
        thread = new Thread(this);
        thread.run();
    }

    public void run() {
        running = true;

        double firstTime = 0;
        double lastTime = System.nanoTime() / 1.0e9;
        double elapsedTime = 0;
        double unprocessedTime = 0;

        while (running) {
            firstTime = System.nanoTime() / 1.0e9;
            elapsedTime = firstTime - lastTime;
            lastTime = firstTime;

            unprocessedTime += elapsedTime;
            while (unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;
                mainProgram.loop(this);
                input.update();
            }
        }
    }

    public static void main(String[] args) {
        mainProgram.init(pixelEngine);
        pixelEngine.start();
    }

//  ----- Getters & Setters -----

    public float deltaTime() {
        return (float)UPDATE_CAP;
    }

    public int getzDepth() {
        return renderer.getzDepth();
    }

    public void setzDepth(int zDepth) {
        renderer.setzDepth(zDepth);
    }


//  ----- Window -----

    public void createWindow(int width, int height, float densityX, float densityY, String title) {
        window = new Window(width, height, densityX, densityY, title);
        renderer = new RenderEngine(width, height, window);
        input = new Input(window);
    }

    public void drawWindow() {
        window.update();
        renderer.clear();
        renderer.process();
    }

//  ----- Render Engine -----

    public void drawText(String text, int offX, int offY, int color) {
        renderer.drawText(text, offX, offY, color);
    }

    public void drawImage(Image image, int offX, int offY) {
        renderer.drawImage(image, offX, offY);
    }

    public void drawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY) {
        renderer.drawImageTile(image, offX, offY, tileX, tileY);
    }

    public void drawRect(int offX, int offY, int width, int height, int fill) {
        renderer.drawRect(offX, offY, width, height, fill);
    }

    public void drawRectStroke(int offX, int offY, int width, int height, int fill, int stroke) {
        renderer.drawRectStroke(offX, offY, width, height, fill, stroke);
    }

    public void drawLine(int x1, int y1, int x2, int y2, int thickness, int color) {
        renderer.drawLine(x1, y1, x2, y2, thickness, color);
    }

//  ----- Input -----

    public int mouseX() {
        return input.getMouseX();
    }

    public int mouseY() {
        return input.getMouseY();
    }

    public int scroll() {
        return input.getScroll();
    }

    public boolean isKey(int keyCode) {
        return input.isKey(keyCode);
    }

    public boolean isKeyUp(int keyCode) {
        return input.isKeyUp(keyCode);
    }

    public boolean isKeyDown(int keyCode) {
        return input.isKeyDown(keyCode);
    }

    public boolean isButton(int button) {
        return input.isButton(button);
    }

    public boolean isButtonUp(int button) {
        return input.isButtonUp(button);
    }

    public boolean isButtonDown(int button) {
        return input.isButtonDown(button);
    }
}

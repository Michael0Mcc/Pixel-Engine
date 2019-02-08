package com.pixelengine.engine;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Window {
    private int width, height;
    private float densityX, densityY;

    private JFrame frame;
    private BufferedImage image;
    private Canvas canvas;
    private BufferStrategy bufferStrategy;
    private Graphics graphics;

    public Window(int width, int height, float densityX, float densityY, String title) {
        this.width = width;
        this.height = height;
        this.densityX = densityX;
        this.densityY = densityY;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        canvas = new Canvas();
        Dimension dimension = new Dimension((int)(width * densityX), (int)(height * densityY));
        canvas.setPreferredSize(dimension);
        canvas.setMaximumSize(dimension);
        canvas.setMinimumSize(dimension);

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        graphics = bufferStrategy.getDrawGraphics();
    }

    public void update() {
        graphics.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        bufferStrategy.show();
    }

    public BufferedImage getImage() {
        return image;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getDensityX() {
        return densityX;
    }

    public float getDensityY() {
        return densityY;
    }
}

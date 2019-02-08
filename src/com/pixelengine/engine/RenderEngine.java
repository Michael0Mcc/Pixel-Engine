package com.pixelengine.engine;

import com.pixelengine.engine.gfx.Font;
import com.pixelengine.engine.gfx.Image;
import com.pixelengine.engine.gfx.ImageTile;

import java.awt.image.DataBufferInt;

public class RenderEngine {
    private int pixelWidth, pixelHeight;
    private int[] pixels;

    private Font font = Font.STANDARD;

    public RenderEngine(int width, int height, Window window) {
        pixelWidth = width;
        pixelHeight = height;
        pixels = ((DataBufferInt)window.getImage().getRaster().getDataBuffer()).getData();
    }

    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0;
        }
    }
    public void setPixel(int x, int y, int value) {

        if ((x < 0 || x >= pixelWidth || y < 0 || y >= pixelHeight) || value == 0xffff00ff) {
            return;
        }

        pixels[x + y * pixelWidth] = value;
    }

    public void drawText(String text, int offX, int offY, int color) {
        int offset = 0;

        for (int i = 0; i < text.length(); i++) {
            int unicode = text.codePointAt(i) -32;
            for (int y = 0; y < font.getFontImage().getHeight(); y++) {
                for (int x = 0; x < font.getWidths()[unicode]; x++) {
                    if (font.getFontImage().getPixels()[(x + font.getOffsets()[unicode]) + y * font.getFontImage().getWidth()] == 0xffffffff) {
                        setPixel(x + offX + offset, y + offY, color);
                    }
                }
            }
            offset += font.getWidths()[unicode];
        }
    }

    public void drawImage(Image image, int offX, int offY) {

//      ----- Don't Render Code -----
        if (offX < -image.getWidth()) return;
        if (offY < -image.getHeight()) return;
        if (offX >= pixelWidth) return;
        if (offY >= pixelHeight) return;

        int newX = 0, newY = 0, newWidth = image.getWidth(), newHeight = image.getHeight();

//      ----- Clipping Code -----
        if (offX < 0) { newX -= offX; }
        if (offY < 0) { newY -= offY; }
        if (newWidth + offX >= pixelWidth) { newWidth -= newWidth + offX - pixelWidth; }
        if (newHeight + offY >= pixelHeight) { newHeight -= newHeight + offY - pixelHeight; }


        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                setPixel(x + offX, y + offY, image.getPixels()[x + y * image.getWidth()]);
            }
        }
    }

    public void drawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY) {

//      ----- Don't Render Code -----
        if (offX < -image.getTileWidth()) return;
        if (offY < -image.getTileHeight()) return;
        if (offX >= pixelWidth) return;
        if (offY >= pixelHeight) return;

        int newX = 0, newY = 0, newWidth = image.getTileWidth(), newHeight = image.getTileHeight();

//      ----- Clipping Code -----
        if (offX < 0) { newX -= offX; }
        if (offY < 0) { newY -= offY; }
        if (newWidth + offX >= pixelWidth) { newWidth -= newWidth + offX - pixelWidth; }
        if (newHeight + offY >= pixelHeight) { newHeight -= newHeight + offY - pixelHeight; }

        for (int x = newX; x < newWidth; x++) {
            for (int y = newY; y < newHeight; y++) {
                setPixel(x + offX, y + offY, image.getPixels()[(x + tileX * image.getTileWidth()) + (y + tileY * image.getTileHeight()) * image.getWidth()]);
            }
        }
    }

    public void drawRectStroke(int offX, int offY, int width, int height, int fill, int stroke) {

//      ----- Don't Render Code -----
        if (offX < -width) return;
        if (offY < -height) return;
        if (offX >= pixelWidth) return;
        if (offY >= pixelHeight) return;

        int newX = 0, newY = 0, newWidth = width, newHeight = height;

//      ----- Clipping Code -----
        if (offX < 0) { newX -= offX; }
        if (offY < 0) { newY -= offY; }
        if (newWidth + offX >= pixelWidth) { newWidth -= newWidth + offX - pixelWidth; }
        if (newHeight + offY >= pixelHeight) { newHeight -= newHeight + offY - pixelHeight; }


        for (int x = newX +1; x < newWidth; x++) {
            for (int y = newY +1; y < newHeight; y++) {
                setPixel(x + offX, y + offY, fill);
            }
        }

        for (int x = newX; x <= newWidth; x++) {
            setPixel(x + offX, offY, stroke);
            setPixel(x + offX, offY + height, stroke);
        }
        for (int y = newY; y <= newHeight; y++) {
            setPixel(offX, y + offY, stroke);
            setPixel(offX + width, y + offY, stroke);
        }

    }

    public void drawRect(int offX, int offY, int width, int height, int fill) {

//      ----- Don't Render Code -----
        if (offX < -width) return;
        if (offY < -height) return;
        if (offX >= pixelWidth) return;
        if (offY >= pixelHeight) return;

        int newX = 0, newY = 0, newWidth = width, newHeight = height;

//      ----- Clipping Code -----
        if (offX < 0) { newX -= offX; }
        if (offY < 0) { newY -= offY; }
        if (newWidth + offX >= pixelWidth) { newWidth -= newWidth + offX - pixelWidth; }
        if (newHeight + offY >= pixelHeight) { newHeight -= newHeight + offY - pixelHeight; }

        for (int x = newX; x <= newWidth; x++) {
            for (int y = newY; y <= newHeight; y++) {
                setPixel(x + offX, y + offY, fill);
            }
        }
    }

    public void drawLine(int x1, int y1, int x2, int y2, int thickness, int color) {

    }
}

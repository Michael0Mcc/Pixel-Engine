package com.pixelengine.engine;

import com.pixelengine.engine.gfx.Font;
import com.pixelengine.engine.gfx.Image;
import com.pixelengine.engine.gfx.ImageRequest;
import com.pixelengine.engine.gfx.ImageTile;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RenderEngine {
    private Font font = Font.STANDARD;
    private ArrayList<ImageRequest> imageRequest = new ArrayList<ImageRequest>();

    private int pixelWidth, pixelHeight;
    private int[] pixels;
    private int[] zBuffer;

    private int zDepth = 0;
    private boolean processing = false;

    public RenderEngine(int width, int height, Window window) {
        pixelWidth = width;
        pixelHeight = height;
        pixels = ((DataBufferInt)window.getImage().getRaster().getDataBuffer()).getData();
        zBuffer = new int[pixels.length];
    }

    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0;
            zBuffer[i] = 0;
        }
    }

    public void process() {
        processing = true;

        Collections.sort(imageRequest, new Comparator<ImageRequest>() {
            @Override
            public int compare(ImageRequest i0, ImageRequest i1) {
                if (i0.zDepth < i1.zDepth)  {
                    return -1;
                }
                if (i0.zDepth > i1.zDepth)  {
                    return 1;
                }
                return 0;
            }
        });

        for (int i = 0; i < imageRequest.size(); i++) {
            ImageRequest ir = imageRequest.get(i);
            setzDepth(ir.zDepth);
            drawImage(ir.image, ir.offX, ir.offY);
        }
        imageRequest.clear();
        processing = false;
    }

    public void setPixel(int x, int y, int value) {
        int alpha = ((value >> 24) & 0xff);

        if ((x < 0 || x >= pixelWidth || y < 0 || y >= pixelHeight) || alpha == 0) {
            return;
        }

        int index = x + y * pixelWidth;

        if (zBuffer[index] > zDepth) {
            return;
        }
        zBuffer[index] = zDepth;

        if (alpha == 255) {
            pixels[index] = value;
        } else {
            int pixelColor = pixels[index];

            int newRed = ((pixelColor >> 16) & 0xff) - (int) ((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha / 255f));
            int newGreen = ((pixelColor >> 8) & 0xff) - (int) ((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha / 255f));
            int newBlue = (pixelColor & 0xff) - (int) (((pixelColor & 0xff) - (value & 0xff)) * (alpha / 255f));

            pixels[index] = (255 << 24 | newRed << 16 | newGreen << 8 | newBlue);
        }
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

        if (!image.isAlpha() && !processing) {
            imageRequest.add(new ImageRequest(image, zDepth, offX, offY));
            return;
        }

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

        if (!image.isAlpha() && !processing) {
            imageRequest.add(new ImageRequest(image.getTileImage(tileX, tileY), zDepth, offX, offY));
            return;
        }

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

    public int getzDepth() {
        return zDepth;
    }

    public void setzDepth(int zDepth) {
        this.zDepth = zDepth;
    }
}

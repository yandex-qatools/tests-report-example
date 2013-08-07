package ru.yandex.qatools.examples.report;

import java.awt.image.BufferedImage;

/**
 * User: eroshenkoam
 * Date: 7/16/13, 7:26 PM
 */
public class DiffImage {

    private final BufferedImage image;

    private final long pixels;

    public DiffImage(long pixels, BufferedImage image) {
        this.pixels = pixels;
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public long getPixels() {
        return pixels;
    }
}

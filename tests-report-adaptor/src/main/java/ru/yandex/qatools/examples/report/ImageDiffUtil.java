package ru.yandex.qatools.examples.report;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: eroshenkoam
 * Date: 7/15/13, 12:32 PM
 */
public class ImageDiffUtil {

    private static final int DEVIATION_BARRIER = 20;

    public static DiffImage diffImage(BufferedImage origin, BufferedImage modified) {
        int width = Math.max(origin.getWidth(), modified.getWidth());
        int height = Math.max(origin.getHeight(), modified.getHeight());
        BufferedImage diff = new BufferedImage(width, height, origin.getType());

        int diffPixNum = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i < origin.getWidth() && j < origin.getHeight() && i < modified.getWidth() && j < modified.getHeight()) {
                    if (deviation(new Color(origin.getRGB(i, j)), new Color(modified.getRGB(i, j))) < DEVIATION_BARRIER) {
                        diff.setRGB(i, j, origin.getRGB(i, j));
                    } else {
                        diffPixNum++;
                        diff.setRGB(i, j, (i + j) % 2 == 0 ? Color.RED.getRGB() : Color.WHITE.getRGB());
                    }
                } else {
                    if (i < origin.getWidth() && j < origin.getHeight()) {
                        diff.setRGB(i, j, origin.getRGB(i, j));
                    } else if (i < modified.getWidth() && j < modified.getHeight()) {
                        diff.setRGB(i, j, modified.getRGB(i, j));
                    } else {
                        diff.setRGB(i, j, 0);
                    }
                }
            }
        }


        return new DiffImage(diffPixNum, diff);
    }

    private static int deviation(Color c1, Color c2) {
        int max = Math.max(Math.abs(c1.getRed() - c2.getRed()), Math.abs(c1.getGreen() - c2.getGreen()));
        return Math.max(max, Math.abs(c1.getBlue() - c2.getBlue()));
    }

}

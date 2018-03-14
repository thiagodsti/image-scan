package org.teda.imagescan;

import java.awt.image.BufferedImage;

public class ImageFound {

    private BufferedImage bufferedImage;
    private double similarity;
    private int width;
    private int height;

    public ImageFound(BufferedImage bufferedImage, int width, int height, double similarity) {
        this.bufferedImage = bufferedImage;
        this.similarity = similarity;
        this.width = width;
        this.height = height;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public double getSimilarity() {
        return similarity;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

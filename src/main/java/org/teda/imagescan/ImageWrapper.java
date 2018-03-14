package org.teda.imagescan;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageWrapper {

    private BufferedImage bufferedImage;
    private Rectangle shape;

    public ImageWrapper(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        shape = new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    public Rectangle getShape() {
        return shape;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}

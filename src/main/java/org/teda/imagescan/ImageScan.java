package org.teda.imagescan;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageScan {

    private List<ImageWrapper> images = new ArrayList<>();
    private String type;
    private int total;
    private double limitPercent;
    private Color color;
    private List<ImageFound> founds = new ArrayList<>();

    public ImageScan(String type, double limit, Color color) {
        this.type = type;
        this.limitPercent = limit;
        this.color = color;
        fetchBufferedImages(type);
    }


    public ImageScan(String type, double limit) {
        this(type, limit, Color.GREEN);


    }

    private void fetchBufferedImages(String type) {
        for (int i=0;i<10;i++) {
            InputStream is = null;
            String name = i == 0 ? type + ".png" : type + i + ".png";
            is = getClass().getClassLoader().getResourceAsStream(name);
            if (is == null) {
                break;
            }

            try {
                BufferedImage img = ImageIO.read(is);
                images.add(new ImageWrapper(img));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getType() {
        return type;
    }

    public List<ImageWrapper> getImages() {
        return images;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getLimitPercent() {
        return limitPercent;
    }

    public Color getColor() {
        return color;
    }

    public List<ImageFound> getFounds() {
        return founds;
    }

    public void addFound(ImageFound image) {
        this.founds.add(image);
    }
}

package main.java;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class Main {

    HashMap<String, HashSet<Integer>> mapVisitedWidth = new HashMap<String, HashSet<Integer>>();
    HashMap<String, HashSet<Integer>> mapVisitedHeight = new HashMap<String, HashSet<Integer>>();

    public static void main(String[] args) {

        new Main().getImage();


    }

    public void getImage() {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("gondola-cut.jpeg"));
        Image image = icon.getImage();


        BufferedImage bufferedImage = toBufferedImage(image);
        System.out.println(bufferedImage.getWidth()); //49
        System.out.println(bufferedImage.getHeight()); //97
        //JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(bufferedImage));
        //BufferedImage cropped = cropImageAndShow(bufferedImage, new Rectangle(49, 97), new Rectangle(79, 250));
        //saveImage(cropped, "pepsi.png");


        long startTime = System.nanoTime();


        //BufferedImage cropped = cropImage(bufferedImage, new Rectangle(49, 97), new Rectangle(305, 250)); //fanta
        //JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(cropped));
        InputStream fanta = getClass().getClassLoader().getResourceAsStream("fanta.png");
        InputStream coca = getClass().getClassLoader().getResourceAsStream("coca.png");
        InputStream pepsi = getClass().getClassLoader().getResourceAsStream("pepsi.png");
        BufferedImage fantaImg = null;
        BufferedImage cocaImg = null;
        BufferedImage pepsiImg = null;
        try {
            fantaImg = ImageIO.read(fanta);
            cocaImg = ImageIO.read(coca);
            pepsiImg = ImageIO.read(pepsi);
        } catch (IOException e) {
        }

        findImages(image, bufferedImage, fantaImg, cocaImg, pepsiImg);
        Image scaledInstance = bufferedImage.getScaledInstance(600, 600, Image.SCALE_SMOOTH);

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        System.out.println(TimeUnit.NANOSECONDS.toSeconds(totalTime));

        JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(scaledInstance));


    }

    private void saveImage(BufferedImage cropped, String name) {
        File file = new File("/media/truecrypt1/workspace/image-scan/src/main/resources/" + name);
        try {
            ImageIO.write(cropped, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(cropped));
    }

    private BufferedImage cropImageAndShow(BufferedImage bufferedImage, Rectangle size, Rectangle position) {
        BufferedImage cropped = cropImage(bufferedImage, size, position);
        JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(cropped));
        return cropped;

    }

    private void findImages(Image image, BufferedImage bufferedImage, BufferedImage fantaImg, BufferedImage cocaImg, BufferedImage pepsiImg) {
        for (int i = 0; i < image.getWidth(null); i=i+2) {
            for (int j = 0; j < image.getWidth(null); j=j+1) {
                try {
                    BufferedImage cropped = cropImage(bufferedImage, new Rectangle(49, 97), new Rectangle(i, j));
                    double differencePercent = getDifferencePercent(cropped, fantaImg);
                    j = calculateSimilarity(bufferedImage, i, j, differencePercent, 10, Color.GREEN, "Fanta");

                    differencePercent = getDifferencePercent(cropped, cocaImg);
                    j = calculateSimilarity(bufferedImage, i, j, differencePercent, 10, Color.RED, "Coca");

                    differencePercent = getDifferencePercent(cropped, pepsiImg);
                    j = calculateSimilarity(bufferedImage, i, j, differencePercent, 12, Color.BLUE, "Pepsi");

                } catch (RasterFormatException ex) {
                }
            }
        }
    }

    private int calculateSimilarity(BufferedImage bufferedImage, int i, int j, double differencePercent, int limitPercent, Color green, String type) {
        HashSet<Integer> visitedWidth = mapVisitedWidth.get(type);
        if (visitedWidth == null ) {
            visitedWidth = new HashSet<>();
        }
        HashSet<Integer> visitedHeight = mapVisitedHeight.get(type);
        if (visitedHeight == null) {
            visitedHeight = new HashSet<>();
        }
        if (differencePercent < limitPercent && !visitedWidth.contains(i) && !visitedHeight.contains(j)) {
            visitedWidth.add(i);
            visitedWidth.add(i + 1);
            visitedWidth.add(i + 2);
            visitedWidth.add(i + 3);
            visitedWidth.add(i + 4);
            visitedWidth.add(i + 5);
            mapVisitedWidth.put(type, visitedWidth);
            
            Graphics2D graph = (Graphics2D) bufferedImage.getGraphics();
            graph.setColor(green);
            graph.draw(new Rectangle(i, j, 49, 97));
            j = j + 97;
        }
        return j;
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private BufferedImage cropImage(BufferedImage src, Rectangle rect, Rectangle place) {
        BufferedImage dest = src.getSubimage(place.width, place.height, rect.width, rect.height);
        return dest;
    }

    private void callRubyFile(String file, int i, int j, String... args) {
        try {
            Process process = Runtime.getRuntime().exec("ruby " + file + " " + args[0] + " " + args[1]);
            process.waitFor();

            BufferedReader processIn = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = processIn.readLine()) != null) {
                double value = Double.parseDouble(line);
                if (value == 0.0 || value < 0.5) {
                    System.out.println("achou em width: " + i + " height: " + j);
                }
            }
        } catch (Exception e) {
        }
    }

    private static double getDifferencePercent(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();
        if (width != width2 || height != height2) {
            throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
        }

        long diff = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
            }
        }
        long maxDiff = 3L * 255 * width * height;

        return 100.0 * diff / maxDiff;
    }

    private static int pixelDiff(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >>  8) & 0xff;
        int b1 =  rgb1        & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >>  8) & 0xff;
        int b2 =  rgb2        & 0xff;
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

}

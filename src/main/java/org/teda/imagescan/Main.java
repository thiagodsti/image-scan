package org.teda.imagescan;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    HashMap<String, HashSet<Integer>> mapVisitedWidth = new HashMap<String, HashSet<Integer>>();
    HashMap<String, HashSet<Integer>> mapVisitedHeight= new HashMap<String, HashSet<Integer>>();

    public static void main(String[] args) {

        List<ImageScan> imagesToScan = fetchScans();

        new Main().scan(imagesToScan);
        //new Main().scanTest(imagesToScan);

    }

    private void scanTest(List<ImageScan> imagesToScan) {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("gondola.png"));
        Image imageIcon = icon.getImage();

        BufferedImage gondola = toBufferedImage(imageIcon);

      //  ImageWrapper image = imagesToScan.get(0).getImages().get(0);
        BufferedImage croppedImg = cropImage(gondola, new Rectangle(49, 85), new Rectangle(360, 200));
        JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(croppedImg));

        if (false) {
            return;
        }

        for (ImageWrapper image : imagesToScan.get(1).getImages()) {
            ImageScan imageScan = imagesToScan.get(1);
            BufferedImage cropped = cropImage(gondola, image.getShape(), new Rectangle(360, 200));
            JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image.getBufferedImage()));
            JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(cropped));
            //JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(cropped));
            double differencePercent = getDifferencePercent(cropped, image.getBufferedImage());
            System.out.println(differencePercent);
            boolean exist = calculateSimilarity(cropped, 360, 200, differencePercent, imageScan.getLimitPercent(), imageScan.getType());
            if (exist) {
                drawOnImage(gondola, 360, 200, image, Color.green);
                JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(gondola));
                // j = j + (int) image.getShape().getHeight();
                //productToScan.setTotal(productToScan.getTotal() + 1);
                //break;
            }
        }
    }

    private static List<ImageScan> fetchScans() {
        List<ImageScan> scans = new ArrayList<>();
        scans.add(new ImageScan("coca", 15, Color.GREEN));
        scans.add(new ImageScan("fanta", 16.5, Color.BLUE));
        scans.add(new ImageScan("pepsi-zero", 17, Color.PINK));
        scans.add(new ImageScan("coca-zero", 17, Color.RED));
        scans.add(new ImageScan("fanta-guarana", 17, Color.BLACK));
        scans.add(new ImageScan("guarana", 17, Color.ORANGE));
        return scans;
    }

    public void scan(List<ImageScan> imagesToScan) {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("gondola.png"));
        Image image = icon.getImage();


        BufferedImage bufferedImage = toBufferedImage(image);
        Image scaledInstance = bufferedImage.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
        JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(scaledInstance));
        //BufferedImage cropped = cropImageAndShow(bufferedImage, new Rectangle(49, 97), new Rectangle(79, 250));
        //saveImage(cropped, "pepsi.png");


        long startTime = System.nanoTime();


        //BufferedImage cropped = cropImage(bufferedImage, new Rectangle(49, 97), new Rectangle(305, 250)); //fanta
        //JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(cropped));


        findImages(bufferedImage, imagesToScan);
        JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(bufferedImage));
        scaledInstance = bufferedImage.getScaledInstance(600, 600, Image.SCALE_SMOOTH);

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        System.out.println(TimeUnit.NANOSECONDS.toSeconds(totalTime));

        JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(scaledInstance));

        for (ImageScan scan : imagesToScan) {
            System.out.println(scan.getType() + " - " + scan.getTotal());
            for (ImageFound found : scan.getFounds()) {
                System.out.println("Width: " + found.getWidth() + " Height: " + found.getHeight() + " Similarity: " + found.getSimilarity());
                JOptionPane.showMessageDialog(null, null, null, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(found.getBufferedImage()));
            }
        }


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

    private void findImages(BufferedImage gondola, List<ImageScan> scans) {
        for (int i = 0; i < gondola.getWidth(); i=i+2) {
            for (int j = 0; j < gondola.getHeight(); j=j+1) {
                try {
                    for (ImageScan productToScan : scans) {
                        boolean exist = false;
                        for (ImageWrapper image : productToScan.getImages()) {
                            BufferedImage cropped = cropImage(gondola, image.getShape(), new Rectangle(i, j));
                            double differencePercent = getDifferencePercent(cropped, image.getBufferedImage());
                            exist = calculateSimilarity(cropped, i, j, differencePercent, productToScan.getLimitPercent(), productToScan.getType());
                            if (exist) {
                                ImageFound found = new ImageFound(cropped, i, j, differencePercent);
                                productToScan.addFound(found);
                                drawOnImage(gondola, i, j, image, productToScan.getColor());
                                j = j + (int) image.getShape().getHeight();
                                productToScan.setTotal(productToScan.getTotal() + 1);
                                break;
                            }
                        }
                        if (exist) {
                            break;
                        }
                    }

                   // differencePercent = getDifferencePercent(cropped, cocaImg);
                   // j = calculateSimilarity(gondola, i, j, differencePercent, 10, Color.RED, "Coca");

                   // differencePercent = getDifferencePercent(cropped, pepsiImg);
                   // j = calculateSimilarity(gondola, i, j, differencePercent, 12, Color.BLUE, "Pepsi");

                } catch (RasterFormatException ex) {
                }
            }
        }
    }

    private void drawOnImage(BufferedImage gondola, int width, int height, ImageWrapper image, Color color) {
        Graphics2D graph = (Graphics2D) gondola.getGraphics();
        graph.setColor(color);
        graph.draw(new Rectangle(width, height, (int) image.getShape().getWidth(), (int) image.getShape().getHeight()));
    }

    private boolean calculateSimilarity(BufferedImage bufferedImage, int i, int j, double differencePercent, double limitPercent, String type) {
        HashSet<Integer> visitedWidth = mapVisitedWidth.get(type);
        if (visitedWidth == null) {
            visitedWidth = new HashSet<>();
        }

        HashSet<Integer> visitedHeight = mapVisitedHeight.get(type);
        if (visitedHeight == null) {
            visitedHeight = new HashSet<>();
        }
        if (differencePercent < limitPercent && !visitedWidth.contains(i) && !visitedHeight.contains(j)) {
            for (int width=0;width<10;width++) {
                visitedWidth.add(i + width);
            }
            //for (int height=0;height<bufferedImage.getHeight();height++) {
              //  visitedHeight.add(i + height);
            //}
            mapVisitedWidth.put(type, visitedWidth);
            //mapVisitedHeight.put(type, visitedHeight);
            return true;
        }
        return false;
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

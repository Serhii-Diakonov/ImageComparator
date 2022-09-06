package com.knubisoft;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;


public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Map<Point, Color> imgInfo1 = new LinkedHashMap<>();
        Map<Point, Color> imgInfo2 = new LinkedHashMap<>();


        File file1 = new File("src/main/resources/test1.jpg");
        File file2 = new File("src/main/resources/test2.jpg");
        BufferedImage inputImage1 = ImageIO.read(file1);
        BufferedImage inputImage2 = ImageIO.read(file2);
        BufferedImage result = ImageIO.read(file2);

        if (inputImage1.getWidth() == inputImage2.getWidth() && inputImage1.getHeight() == inputImage2.getHeight()) {
            for (int y = 0; y < inputImage1.getHeight(); y++) {
                for (int x = 0; x < inputImage1.getWidth(); x++) {
                    imgInfo1.put(new Point(x, y), new Color(inputImage1.getRGB(x, y), true));
                    imgInfo2.put(new Point(x, y), new Color(inputImage2.getRGB(x, y), true));
                }
            }
        }
        int firstX = inputImage1.getWidth(), firstY = inputImage1.getHeight();
        int lastX = 0, lastY = 0;
        Map<Point, Point> rectData = new LinkedHashMap<>();
        for (Map.Entry<Point, Color> pointColorEntry : imgInfo1.entrySet()) {
            if (!imgInfo2.get(pointColorEntry.getKey()).equals(pointColorEntry.getValue())) {
                Point pixel = pointColorEntry.getKey();
                if (pixel.x < firstX) {
                    firstX = pixel.x;
                }
                if (pixel.y < firstY) {
                    firstY = pixel.y;
                }
                if (pixel.x > lastX) {
                    lastX = pixel.x;
                }
                if (pixel.y > lastY) {
                    lastY = pixel.y;
                }
            }
        }

        Graphics2D graphics = result.createGraphics();
        graphics.setColor(new Color(255, 0, 0));
        graphics.setStroke(new BasicStroke(4));
        drawExpandedRect(firstX, firstY, lastX, lastY, graphics);

        String imageExtension = file1.getName().split("\\.")[1];
        ImageIO.write(result, imageExtension, new File("result." + imageExtension));
    }

    private static void drawExpandedRect(int firstX, int firstY, int lastX, int lastY, Graphics2D graphics) {
        graphics.drawRect(firstX - 5, firstY - 5, lastX - firstX + 10, lastY - firstY + 10);
    }
}

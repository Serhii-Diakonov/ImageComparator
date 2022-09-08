package com.knubisoft;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ImageDifferenceHighlighter {
    private static int maxCapturingDistance = 200;
    private static Map<Point, Color> imgInfo1;
    private static Map<Point, Color> imgInfo2;
    private static List<Point> differentPoints;

    public static BufferedImage highlightDifference(BufferedImage img1, BufferedImage img2) {
        return highlightDifference(img1, img2, null);
    }

    public static BufferedImage highlightDifference(BufferedImage img1, BufferedImage img2, String newName) {
        if (hasSameDimensions(img1, img2)) {
            extractColorData(img1, img2);
            collectDifferentPoints();
            splitPointsIntoGroups();
            return null;
        } else {
            return null;
        }
    }

    public static void setMaxCapturingDistance(int maxCapturingDistance) {
        ImageDifferenceHighlighter.maxCapturingDistance = maxCapturingDistance;
    }

    private static void splitPointsIntoGroups() {
        for (Point point : differentPoints) {
            
        }
    }

    private static void collectDifferentPoints() {
        differentPoints = new ArrayList<>();
        imgInfo1.forEach((point, color) -> {
            if (!imgInfo2.get(point).equals(color)) {
                differentPoints.add(point);
            }
        });
    }

    private static void extractColorData(BufferedImage img1, BufferedImage img2) {
        imgInfo1 = new LinkedHashMap<>();
        imgInfo2 = new LinkedHashMap<>();
        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                imgInfo1.put(new Point(x, y), new Color(img1.getRGB(x, y), true));
                imgInfo2.put(new Point(x, y), new Color(img2.getRGB(x, y), true));
            }
        }
    }

    private static boolean hasSameDimensions(BufferedImage img1, BufferedImage img2) {
        return img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight();
    }
}

package com.knubisoft;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageDifferenceHighlighter {

    //Distance between points to add them into separate groups
    private static int maxCapturingDistance = 200;
    private static List<Point> differentPoints;

    public static BufferedImage highlightDifference(BufferedImage img1, BufferedImage img2) {
        return highlightDifference(img1, img2, null);
    }

    public static BufferedImage highlightDifference(BufferedImage img1, BufferedImage img2, String newName) {
        if (hasSameDimensions(img1, img2)) {
            collectDifferentPoints(img1, img2);
            List<List<Point>> groups = splitPointsIntoGroups();
            return highlightGroups(groups, img2);
        } else {
            return null;
        }
    }

    private static BufferedImage highlightGroups(List<List<Point>> groups, BufferedImage img2) {
        for (List<Point> group : groups) {
            int maxX = Collections.max(group, Comparator.comparingInt(o -> o.x)).x;
            int minX = Collections.min(group, Comparator.comparingInt(o -> o.x)).x;
            int maxY = Collections.max(group, Comparator.comparingInt(o -> o.y)).y;
            int minY = Collections.max(group, Comparator.comparingInt(o -> o.y)).y;
            Graphics2D graphics = img2.createGraphics();
            graphics.setColor(new Color(255, 0, 0));
            graphics.setStroke(new BasicStroke(4));
            graphics.drawRect(minX, minY, maxX - minX, maxY - minY);
        }
        return img2;
    }

    public static void setMaxCapturingDistance(int maxCapturingDistance) {
        ImageDifferenceHighlighter.maxCapturingDistance = maxCapturingDistance;
    }

    private static List<List<Point>> splitPointsIntoGroups() {
        List<List<Point>> groups = new ArrayList<>();
        groups.add(new ArrayList<>());
        for (Point point : differentPoints) {
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).isEmpty()) {
                    groups.get(i).add(point);
                } else {
                    for (Point groupedPoint : groups.get(i)) {
                        if (groupedPoint.distance(point) < maxCapturingDistance) {
                            groups.get(i).add(point);
                            break;
                        } else if (groups.get(i).indexOf(groupedPoint) == groups.get(i).size() - 1) {
                            List<Point> newGroup = new ArrayList<>();
                            newGroup.add(point);
                            groups.add(newGroup);
                        }
                    }
                }
            }
        }
        return groups;
    }

    private static void collectDifferentPoints(BufferedImage img1, BufferedImage img2) {
        differentPoints = new ArrayList<>();
        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    differentPoints.add(new Point(x, y));
                }
            }
        }
    }

    private static boolean hasSameDimensions(BufferedImage img1, BufferedImage img2) {
        return img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight();
    }
}

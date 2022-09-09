package com.knubisoft;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageDifferenceHighlighter {

    //Distance between points to add them into separate groups
    private int maxCapturingDistance = 200;
    private Color highlightColor = new Color(255, 0, 0);
    private Stroke stroke = new BasicStroke(4);
    private int expandWidth = 10;
    private int expandHeight = 10;
    private List<Point> differentPoints;

    public BufferedImage highlightDifference(BufferedImage defaultImg, BufferedImage changedImg) {
        return highlightDifference(defaultImg, changedImg, null);
    }

    public BufferedImage highlightDifference(BufferedImage defaultImg, BufferedImage changedImg, String newName) {
        if (hasSameDimensions(defaultImg, changedImg)) {
            List<List<Point>> groups = collectDifferentPoints(defaultImg, changedImg);
//            List<List<Point>> groups = splitPointsIntoGroups();
            return highlightGroups(groups, changedImg);
        } else {
            return null;
        }
    }

    public void setMaxCapturingDistance(int maxCapturingDistance) {
        this.maxCapturingDistance = maxCapturingDistance;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void setExpandWidth(int expandWidth) {
        this.expandWidth = expandWidth;
    }

    public void setExpandHeight(int expandHeight) {
        this.expandHeight = expandHeight;
    }

    private BufferedImage highlightGroups(List<List<Point>> groups, BufferedImage img) {
        for (List<Point> group : groups) {
            int maxX = Collections.max(group, Comparator.comparingInt(o -> o.x)).x;
            int minX = Collections.min(group, Comparator.comparingInt(o -> o.x)).x;
            int maxY = Collections.max(group, Comparator.comparingInt(o -> o.y)).y;
            int minY = Collections.min(group, Comparator.comparingInt(o -> o.y)).y;
            drawExpandedImageRegion(img, maxX, minX, maxY, minY);
        }
        return img;
    }

    private void drawExpandedImageRegion(BufferedImage img, int maxX, int minX, int maxY, int minY) {
        Graphics2D graphics = img.createGraphics();
        graphics.setColor(highlightColor);
        graphics.setStroke(stroke);
        minX -= expandWidth;
        maxX += expandWidth;
        minY -= expandHeight;
        maxY += expandHeight;
        graphics.drawRect(minX, minY, maxX - minX, maxY - minY);
    }

    private List<List<Point>> splitPointsIntoGroups() {
        List<Point> curGroup;
        List<List<Point>> groups = new ArrayList<>();
        groups.add(new ArrayList<>());
        for (Point point : differentPoints) {
        }
        return groups;
    }

    private List<List<Point>> collectDifferentPoints(BufferedImage defaultImg, BufferedImage changedImg) {
        List<Point> curGroup;
        List<List<Point>> groups = new ArrayList<>();
        groups.add(new ArrayList<>());
//        differentPoints = new ArrayList<>();
        for (int y = 0; y < defaultImg.getHeight(); y++) {
            for (int x = 0; x < defaultImg.getWidth(); x++) {
                if (defaultImg.getRGB(x, y) != changedImg.getRGB(x, y)) {
//                    differentPoints.add(new Point(x, y));
                    Point point = new Point(x, y);
                    for (int i = 0; i < groups.size(); i++) {
                        curGroup = groups.get(i);
                        if (curGroup.isEmpty()) {
                            curGroup.add(point);
                        } else {
                            for (Point groupedPoint : curGroup) {
                                if (groupedPoint.distance(point) <= maxCapturingDistance) {
                                    if (!curGroup.contains(point)) {
                                        curGroup.add(point);
                                    }
                                    break;
                                } else if (groups.get(groups.size() - 1) == curGroup &&
                                        curGroup.get(curGroup.size() - 1) == groupedPoint) {
                                    List<Point> newGroup = new ArrayList<>();
                                    newGroup.add(point);
                                    groups.add(newGroup);
                                }
                            }
                        }
                    }

                }
            }
        }
        return groups;
    }

    private boolean hasSameDimensions(BufferedImage img1, BufferedImage img2) {
        return img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight();
    }
}
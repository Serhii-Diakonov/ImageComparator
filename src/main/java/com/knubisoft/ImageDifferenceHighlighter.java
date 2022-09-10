package com.knubisoft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ImageDifferenceHighlighter {

    //Maximum distance between points to add them into the same group
    private int maxCapturingDistance = 200;
    private Color highlightColor = new Color(255, 0, 0);
    private Stroke stroke = new BasicStroke(4);
    //Number of pixels for width offset in both sides
    private int expandWidth = 10;
    //Number of pixels for height offset in both sides
    private int expandHeight = 10;
    /*
     * Number of pixels to be missed for speed improving
     * Causes accuracy reducing
     * */
    private int pixelsMissed = 1;

    public BufferedImage highlightDifference(BufferedImage defaultImg, BufferedImage changedImg) {
        if (hasSameDimensions(defaultImg, changedImg)) {
            List<Group> groups = splitPointsIntoGroups(defaultImg, changedImg);
//            mergeCrossingGroups(groups);
            return highlightGroups(groups, changedImg);
        } else {
            return null;
        }
    }

    private void mergeCrossingGroups(List<Group> groups) {
        boolean isCrossing = false;
        for (int i = 0; i < groups.size(); i++) {
            for (int a = 0; a < groups.get(i).size(); a++) {
                for (int j = 0; j < groups.size(); j++) {
                    Point point = groups.get(i).get(a);
                    if (i != j && groups.get(j).stream().anyMatch(p -> p.distance(point) <= maxCapturingDistance)) {
                        groups.get(j).addAll(groups.get(i));
                        groups.remove(groups.get(i));
                        isCrossing = true;
                        break;
                    }
                }
                if(isCrossing){
                    isCrossing = false;
                    break;
                }
            }
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

    private BufferedImage highlightGroups(List<Group> groups, BufferedImage img) {
        for (Group group : groups) {
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

    public void setPixelsMissed(int pixelsMissed) {
        this.pixelsMissed = Math.max(pixelsMissed, 1);
    }

    private List<Group> splitPointsIntoGroups(BufferedImage defaultImg, BufferedImage changedImg) {
        List<Group> groups = new ArrayList<>();
        for (int y = 0; y < defaultImg.getHeight(); y += pixelsMissed) {
            for (int x = 0; x < defaultImg.getWidth(); x += pixelsMissed) {
                if (defaultImg.getRGB(x, y) != changedImg.getRGB(x, y)) {
                    Point point = new Point(x, y);
                    Group group = findGroupForPoint(groups, point).orElse(new Group());
                    group.add(point);
                    if (!groups.contains(group)) {
                        groups.add(group);
                    }
                }
            }
        }
        mergeCrossingGroups(groups);
        return groups;
    }

    private Optional<Group> findGroupForPoint(List<Group> groups, Point point) {
        Group curGroup;
        for (int i = 0; i < groups.size(); i++) {
            curGroup = groups.get(i);
            for (Point groupedPoint : curGroup) {
                if (groupedPoint.distance(point) <= maxCapturingDistance) {
                    return Optional.of(curGroup);
                } else if (groups.get(groups.size() - 1) == curGroup && curGroup.get(curGroup.size() - 1) == groupedPoint) {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    private boolean hasSameDimensions(BufferedImage img1, BufferedImage img2) {
        return img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight();
    }

    private static class Group extends ArrayList<Point> {
    }

    /*@Getter
    @Setter
    @AllArgsConstructor
    private static class Point{
        int x;
        int y;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            if (x != point.x) return false;
            return y == point.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }*/
}
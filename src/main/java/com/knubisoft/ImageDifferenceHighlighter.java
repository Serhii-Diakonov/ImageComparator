package com.knubisoft;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
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
                if (isCrossing) {
                    isCrossing = false;
                    break;
                }
            }
        }
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

    private List<Group> splitPointsIntoGroups(BufferedImage defaultImg, BufferedImage changedImg) {
        List<Group> groups = new ArrayList<>();
        for (int y = 0; y < defaultImg.getHeight(); y += pixelsMissed) {
            for (int x = 0; x < defaultImg.getWidth(); x += pixelsMissed) {
                if (defaultImg.getRGB(x, y) != changedImg.getRGB(x, y)) {
                    Point point = new Point(x, y);
                    List<Group> groupsForPoint = findGroupsForPoint(groups, point).
                            orElse(Collections.singletonList(new Group()));
                    if (groupsForPoint.size() > 1) {
                        for (int i = 1; i < groupsForPoint.size(); i++) {
                            groupsForPoint.get(0).addAll(groupsForPoint.get(i));
                        }
                    }
                    Group groupForPoint = groupsForPoint.get(0);
                    groupForPoint.add(point);
                    if (!groups.contains(groupForPoint)) {
                        groups.add(groupForPoint);
                    }
                }
            }
        }
        mergeCrossingGroups(groups);
        return groups;
    }

    private Optional<List<Group>> findGroupsForPoint(List<Group> groups, Point point) {
        List<Group> foundGroups = new ArrayList<>();
        for (Group group : groups) {
            for (Point groupedPoint : group) {
                if (groupedPoint.distance(point) <= maxCapturingDistance) {
                    if (!foundGroups.contains(group)) {
                        foundGroups.add(group);
                        break;
                    }
                }
            }
        }
        if (foundGroups.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(foundGroups);
        }
    }

    private boolean hasSameDimensions(BufferedImage img1, BufferedImage img2) {
        return img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight();
    }

    private static class Group extends ArrayList<Point> {
    }
}
package com.knubisoft;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Class for highlighting regions which are different for two pictures
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ImageDifferenceHighlighter {
    /**
     * Maximum distance between points to add them into the same group
     */
    private int maxCapturingDistance = 200;
    /**
     * Stroke color of highlighted regions
     */
    private Color highlightColor = new Color(255, 0, 0);
    /**
     * Stroke width of highlighted regions
     */
    private Stroke stroke = new BasicStroke(4);
    /**
     * Number of pixels for width offset in both sides
     */
    private int expandWidth = 10;
    /**
     * Number of pixels for height offset in both sides
     */
    private int expandHeight = 10;
    /**
     * Number of pixels to be missed for speed improving
     * Causes accuracy reducing
     * */
    private int pixelsMissed = 1;

    /**
     * Highlights all regions which are different for two pictures.
     * @param defaultImg First BufferedImage
     * @param changedImg Second BufferedImage which are almost equals to the first one except several minor differences
     * @return BufferedImage of the default BufferedImage with highlighted regions
     */
    public BufferedImage highlightDifference(BufferedImage defaultImg, BufferedImage changedImg) {
        if (hasSameDimensions(defaultImg, changedImg)) {
            List<Group> groups = splitPointsIntoGroups(defaultImg, changedImg);
            return highlightGroups(groups, changedImg);
        } else {
            return null;
        }
    }

    /**
     * Merges several groups into the one group
     * if the distance between points of these groups are not greater then {@link ImageDifferenceHighlighter#maxCapturingDistance}.
     * @param groups {@code List} of {@link Group} of pixels which pixels are different in two images
     */
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

    /**
     * Distinguishes all required points for each group to further highlighting.
     * @param groups List of Group of points which pixels are different in two images and should be highlighted
     * @param img BufferedImage on which highlighting should be applied
     * @return BufferedImage with applied highlighting (regions with differences are restricted with a rectangle(s))
     */
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

    /**
     * Draws a rectangle around the region. Its borders are expanded
     * on {@link ImageDifferenceHighlighter#expandWidth} and {@link ImageDifferenceHighlighter#expandHeight} amounts of pixels.
     * @param img BufferedImage on which the rectangle should be drawn
     * @param maxX Right X (abscissa) coordinate
     * @param minX Left X (abscissa) coordinate
     * @param maxY Lower Y (ordinate) coordinate
     * @param minY Upper Y (ordinate) coordinate
     */
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

    /**
     * Creates {@link Group} and adds to it points with different color values (RGB model) for each image. In further these {@link Group} are merged if it
     * is possible and highlighted. If the point is not suitable for any existing {@link Group} than new {@link Group} for the point is created.
     * @param defaultImg the first BufferedImage
     * @param changedImg BufferedImage to be compared with the first one
     * @return {@code List} of {@link Group} of points which contains points with different color values for the first and the second images
     */
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

    /**
     * Finds {@link Group} which contains at least one point with distance between 
     * it and a passed point not greater then {@link ImageDifferenceHighlighter#maxCapturingDistance},
     * in other words {@link Group} which are suitable for the point adding.
     * @param groups {@code List} of already formed {@link Group} 
     * @param point Point to be measured distance between it and a point of a {@link Group}
     * @return {@code List} of {@link Group} for point adding or an empty {@code Optional} 
     */
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

    /**
     * Checks if two images have the same width and height and are valid for further processing.
     * @param img1 the first BufferedImage for comparison
     * @param img2 the second BufferedImage for comparison
     * @return {@code true} if both images have the same width and height, {@code false} otherwise
     */
    private boolean hasSameDimensions(BufferedImage img1, BufferedImage img2) {
        return img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight();
    }

    /**
     * Class for representing a {@code List} of {@code Point}
     */
    private static class Group extends ArrayList<Point> {
    }
}
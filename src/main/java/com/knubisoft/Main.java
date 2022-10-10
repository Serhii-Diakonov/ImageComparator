package com.knubisoft;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    private static final String DEFAULT_PATH_1 = "src/main/resources/test1.jpg";
    private static final String DEFAULT_PATH_2 = "src/main/resources/test2.jpg";

    @SneakyThrows
    public static void main(String[] args) {
        String imagePath1 = DEFAULT_PATH_1;
        String imagePath2 = DEFAULT_PATH_2;
        if (args.length == 2) {
            imagePath1 = args[0];
            imagePath2 = args[1];
        }
        File file1 = new File(imagePath1);
        File file2 = new File(imagePath2);
        BufferedImage inputImage1 = ImageIO.read(file1);
        BufferedImage inputImage2 = ImageIO.read(file2);

        ImageDifferenceHighlighter highlighter = new ImageDifferenceHighlighter();
        highlighter.setHighlightColor(new Color(0, 0, 255));
        highlighter.setPixelsMissed(1);
        highlighter.setMaxCapturingDistance(20);
        highlighter.setStroke(new BasicStroke(1));

        BufferedImage result = highlighter.highlightDifference(inputImage1, inputImage2);
        if (result != null) {
            ImageIO.write(result, "png", new File("result.png"));
        }
    }
}
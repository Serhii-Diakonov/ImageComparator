package com.knubisoft;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

//    @SneakyThrows
    public static void main(String[] args) {
        File file1 = new File("src/main/resources/test1.jpg");
        File file2 = new File("src/main/resources/test2.jpg");
        try{
            BufferedImage inputImage1 = ImageIO.read(file1);
            BufferedImage inputImage2 = ImageIO.read(file2);
            ImageDifferenceHighlighter highlighter = new ImageDifferenceHighlighter();
            BufferedImage result = highlighter.highlightDifference(inputImage1, inputImage2);
            if (result != null) {
                ImageIO.write(result, "jpg", new File("result.jpg"));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
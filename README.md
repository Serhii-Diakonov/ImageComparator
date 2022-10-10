# Image Comparator
**A program for comparing two images and highlighting regions which have different pixels (pixels with different RGB color values).**
The program uses simple algorithms of comparing each pixel's RGB value of an image with an appropriate pixel of another image. This comparison is done pixel by pixel. Then pixels with different values are added to appropriate groups for creating a region and finally these regions are highlighted.
---
###### Used tools:
- [Lombok](https://projectlombok.org/) - @Getter, @Setter and other useful annotations
---

## How to install and run
#### Using Intellij IDEA
1. Download the project (you can do it in several ways, but I'd recommend to use `git copy`)
2. File -> Open -> pom.xml (choose pom.xml from downloaded project) -> Open as project
3. Choose **Main.java** in IDE
4. Run it using 'Run' button or pressing Shift+F10. If you want to specify program arguments, press Open 'Edit Run/Debug configurations' (near to 'Run' button) -> Edit configuration -> in **Build and run** section specify program arguments in the field **Program arguments** (Alt + R) in format `path1 path2`.

***Note:*** _don't forget to resolve required dependencies! (Open **pom.xml** in Intellij, press Ctrl + Shift + O)._
#### Using Maven (includes *.jar* building)
1. `mvn clean verify` (or press appropriate operations in IDE)
2. `java -jar target/ImageComparator-1.0-SNAPSHOT.jar`

## How to use
1. [Run](#how-to-install-and-run) the app with passing absolute paths of images to compare to the program. If you don't pass anything, images with names **test1.jpg** and **test2.jpg** from ***resources*** folder are used
2. File **result.png** with highlighted different regions appears in root directory

---
Please, contact me if you have any ideas or questions
Email: *sergeidyakonov222@gmail.com*
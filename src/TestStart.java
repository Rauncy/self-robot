/**
 * Created by Christopher on 3/25/2018.
 */

import imageprocessing.cnn.ColorNN;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class TestStart {
    public static void main(String[] args){
        BufferedImage test = null;
        String filename = "tst.png";
        try{
            test = ImageIO.read(new File("testimages/"+filename));
        }catch(Exception e){
            System.out.println("File doesn't exist!");
        }
        long time = System.nanoTime();
        test = ColorNN.processImageRGB(test);
        double elapsed = (System.nanoTime()-time)/1000000000.0;
        System.out.println("ELAPSED: "+elapsed+" sec\nPIX/SEC: " + ((test.getWidth()*test.getHeight())/elapsed));
        try{
            ImageIO.write(test, filename.split("\\.")[1], new File("testimages/outputRGB.jpg"));
        }catch(Exception e){
            System.out.println("Unable to write file!");
        }
        try{
            test = ImageIO.read(new File("testimages/"+filename));
        }catch(Exception e){
            System.out.println("File doesn't exist!");
        }
        time = System.nanoTime();
        test = ColorNN.processImageHSV(test);
        elapsed = (System.nanoTime()-time)/1000000000.0;
        System.out.println("ELAPSED: "+elapsed+" sec\nPIX/SEC: " + ((test.getWidth()*test.getHeight())/elapsed));
        try{
            ImageIO.write(test, filename.split("\\.")[1], new File("testimages/outputHSV.jpg"));
        }catch(Exception e){
            System.out.println("Unable to write file!");
        }

    }
}

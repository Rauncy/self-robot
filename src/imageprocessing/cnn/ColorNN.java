package imageprocessing.cnn;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Christopher on 3/25/2018.
 */
public class ColorNN {

    public static final int SEARCH_SPACE = 1;
    public static final double H_WEIGHT = 1;
    public static final double S_WEIGHT = 2.55;
    public static final double V_WEIGHT = 2.55;
    public static final double R_WEIGHT = 1;
    public static final double G_WEIGHT = 1;
    public static final double B_WEIGHT = 1;
    public static final double IMAGE_MULT = 1;
    //RATIO IS HSV TO RGB
    public static final double RATIO = 1;
    public static final double ARATIO = 1-RATIO;

    public ColorNN(){
    }

    public static BufferedImage processImageHSV(BufferedImage original){
        //TODO FIX ARTIFACTS FROM V WRAPPING TO 0 FROM 359
        BufferedImage processed = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        double delta, eval;
        float[] pval, cval;
        float[][] pvert;
        double avg = 0;
        Color temp;
        for(int r=0;r<processed.getHeight();r++){
            for(int c=0;c<processed.getWidth();c++){
                delta = eval = 0;
                pval = null;
                pvert = new float[SEARCH_SPACE*2+1][];
                for(int i=-SEARCH_SPACE; i<=SEARCH_SPACE; i++){
                    if(i+r<0)i=0;
                    if(i+r>=processed.getHeight()) break;
                    for(int j=-SEARCH_SPACE; j<=SEARCH_SPACE; j++){
                        if(j+c<0)j=0;
                        if(j+c>=processed.getWidth()) break;

                        //Process pixel at spot
                        if(pval != null){
                            temp = new Color(original.getRGB(j+c, i+r));
                            cval = new float[3];
                            Color.RGBtoHSB(temp.getRed(), temp.getGreen(), temp.getBlue(), cval);

                            delta+=(Math.abs(cval[0]-pval[0])*R_WEIGHT+Math.abs(cval[1]-pval[1])*G_WEIGHT+(Math.abs(Math.abs(180-cval[2])-Math.abs(180-pval[2])))*B_WEIGHT*2);
                            pval = cval.clone();
                        }else{
                            temp = new Color(original.getRGB(j+c, i+r));
                            pval = new float[3];
                            cval = new float[3];
                            Color.RGBtoHSB(temp.getRed(), temp.getGreen(), temp.getBlue(), pval);
                        }
                        if(pvert[j+SEARCH_SPACE]!=null){
                            delta+=(Math.abs(cval[0]-pvert[j+SEARCH_SPACE][0])*H_WEIGHT+Math.abs(cval[1]-pvert[j+SEARCH_SPACE][1])*S_WEIGHT+Math.abs(cval[2]-pvert[j+SEARCH_SPACE][2])*V_WEIGHT);
                        }
                        pvert[j+SEARCH_SPACE]=pval.clone();
                        eval++;
                    }
                }
                processed.setRGB(c, r, new Color((int)(Math.min(255, IMAGE_MULT*255*(delta/eval))), (int)(Math.min(255, IMAGE_MULT*255*(delta/eval))), (int)(Math.min(255, IMAGE_MULT*255*(delta/eval)))).getRGB());
                avg+=(int)(Math.min(255, IMAGE_MULT*255*(delta/eval)));
            }
        }
        avg/=processed.getHeight()*processed.getWidth();
        System.out.println("AVG VAL: " + avg);

        return processed;
    }

    public static BufferedImage processImageMIXED(BufferedImage original){
        BufferedImage processed = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        double delta, eval;
        float[] pvalh, pvalc, cval;
        float[][] pverth, pvertc;
        double avg = 0;
        Color temp;
        for(int r=0;r<processed.getHeight();r++){
            for(int c=0;c<processed.getWidth();c++){
                delta = eval = 0;
                pvalh = pvalc = null;
                pverth = new float[SEARCH_SPACE*2+1][];
                pvertc = pverth.clone();
                cval = null;
                for(int i=-SEARCH_SPACE; i<=SEARCH_SPACE; i++){
                    if(i+r<0)i=0;
                    if(i+r>=processed.getHeight()) break;
                    for(int j=-SEARCH_SPACE; j<=SEARCH_SPACE; j++){
                        if(j+c<0)j=0;
                        if(j+c>=processed.getWidth()) break;

                        //Process pixel at spot
                        if(pvalh != null){
                            new Color(original.getRGB(j+c, i+r)).getRGBColorComponents(cval);

                            delta+=ARATIO*(Math.abs(cval[0]-pvalc[0])*R_WEIGHT+Math.abs(cval[1]-pvalc[1])*G_WEIGHT+Math.abs(cval[2]-pvalc[2])*B_WEIGHT);
                            pvalc = cval.clone();

                            temp = new Color(original.getRGB(j+c, i+r));
                            Color.RGBtoHSB(temp.getRed(), temp.getGreen(), temp.getBlue(), cval);

                            delta+=RATIO*(Math.abs(cval[0]-pvalh[0])*R_WEIGHT+Math.abs(cval[1]-pvalh[1])*G_WEIGHT+(Math.abs(180-cval[2])-Math.abs(180-pvalh[2]))*B_WEIGHT*2);
                            pvalh = cval.clone();
                        }else{
                            temp = new Color(original.getRGB(j+c, i+r));
                            pvalh = new float[3];
                            pvalc = new float[3];
                            cval = new float[3];
                            Color.RGBtoHSB(temp.getRed(), temp.getGreen(), temp.getBlue(), pvalh);
                            new Color(original.getRGB(j+c, i+r)).getRGBColorComponents(pvalc);

                        }
                        if(pverth[j+SEARCH_SPACE]!=null){
                            delta+=RATIO*(Math.abs(cval[0]-pverth[j+SEARCH_SPACE][0])*H_WEIGHT+Math.abs(cval[1]-pverth[j+SEARCH_SPACE][1])*S_WEIGHT+Math.abs(cval[2]-pverth[j+SEARCH_SPACE][2])*V_WEIGHT);

                            new Color(original.getRGB(j+c, i+r)).getRGBColorComponents(cval);
                            delta+=ARATIO*(Math.abs(cval[0]-pvertc[j+SEARCH_SPACE][0])*R_WEIGHT+Math.abs(cval[1]-pvertc[j+SEARCH_SPACE][1])*G_WEIGHT+Math.abs(cval[2]-pvertc[j+SEARCH_SPACE][2])*B_WEIGHT);
                        }
                        pverth[j+SEARCH_SPACE]=pvalh.clone();
                        pvertc[j+SEARCH_SPACE]=pvalc.clone();
                        eval+=2;
                    }
                }
                processed.setRGB(c, r, new Color((int)(Math.min(255, IMAGE_MULT*255*(delta/eval))), (int)(Math.min(255, IMAGE_MULT*255*(delta/eval))), (int)(Math.min(255, IMAGE_MULT*255*(delta/eval)))).getRGB());
                avg+=(int)(Math.min(255, IMAGE_MULT*255*(delta/eval)));
            }
        }
        avg/=processed.getHeight()*processed.getWidth();
        System.out.println("AVG VAL: " + avg);

        return processed;
    }

    public static BufferedImage processImageRGB(BufferedImage original){
        BufferedImage processed = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        double delta, eval;
        float[] pval, cval;
        float[][] pvert;
        double avg = 0;
        Color temp;
        for(int r=0;r<processed.getHeight();r++){
            for(int c=0;c<processed.getWidth();c++){
                delta = eval = 0;
                pval = null;
                pvert = new float[SEARCH_SPACE*2+1][];
                for(int i=-SEARCH_SPACE; i<=SEARCH_SPACE; i++){
                    if(i+r<0)i=0;
                    if(i+r>=processed.getHeight()) break;
                    for(int j=-SEARCH_SPACE; j<=SEARCH_SPACE; j++){
                        if(j+c<0)j=0;
                        if(j+c>=processed.getWidth()) break;

                        //Process pixel at spot
                        if(pval != null){
                            cval = new float[3];
                            new Color(original.getRGB(j+c, i+r)).getRGBColorComponents(cval);

                            delta+=(Math.abs(cval[0]-pval[0])*H_WEIGHT+Math.abs(cval[1]-pval[1])*S_WEIGHT+Math.abs(cval[2]-pval[2])*V_WEIGHT);
                            pval = cval.clone();
                        }else{
                            pval = new float[3];
                            cval = new float[3];
                            new Color(original.getRGB(j+c, i+r)).getRGBColorComponents(pval);
                        }
                        if(pvert[j+SEARCH_SPACE]!=null){
                            delta+=(Math.abs(cval[0]-pvert[j+SEARCH_SPACE][0])*H_WEIGHT+Math.abs(cval[1]-pvert[j+SEARCH_SPACE][1])*S_WEIGHT+Math.abs(cval[2]-pvert[j+SEARCH_SPACE][2])*V_WEIGHT);
                        }
                        pvert[j+SEARCH_SPACE]=pval.clone();
                        eval++;
                    }
                }
                processed.setRGB(c, r, new Color((int)(Math.min(255, IMAGE_MULT*255*(delta/eval))), (int)(Math.min(255, IMAGE_MULT*255*(delta/eval))), (int)(Math.min(255, IMAGE_MULT*255*(delta/eval)))).getRGB());
                avg+=(int)(Math.min(255, IMAGE_MULT*255*(delta/eval)));
            }
        }
        avg/=processed.getHeight()*processed.getWidth();
        System.out.println("AVG VAL: " + avg);

        return processed;
    }
}

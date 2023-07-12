import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

public class AsciiConverter {
    //campi statici
    public static final String[] PALETTES={"LJICPHRB",".¨,;!+?ç@",".¨,;!+LJICPHRB@"," ░▒▓█"};
    public static final int PALETTE_8_LETTERS=0;
    public static final int PALETTE_9_SIMBLES=1;
    public static final int PALETTE_15_MIXED=2;
    public static final int PALETTE_5_GRADIANTS=3;

    //campi non statici
    private String palette;
    private BufferedImage image;
    private int rateo;
    private Graphics2D reader;
    private boolean inverted;


    //costruttori
    public AsciiConverter(String ordinatedPalette){
        this.palette=ordinatedPalette;
        image=null;
        reader=null;
        rateo=1;
        inverted=false;
    }
    public AsciiConverter(){
        this(null);

    }
    //setters
    public void setConversionRateo(int numberOfPixelsRepresentedBySingleChar){
        this.rateo=numberOfPixelsRepresentedBySingleChar;
        if(rateo<=0){
            rateo=1;
        }

    }
    public void setPalette(String ordinatedPalette) {

        this.palette=ordinatedPalette;
    }
    public void setInverted(boolean inverted){
        this.inverted=inverted;

    }
    //getters
    public BufferedImage getImage() {
        return image;

    }
    public int getConversionRateo() {
        return rateo;

    }
    public String getPalette(){
        return palette;

    }
    public int getConversionWidth(){
        if(image==null){
            return 0;
        }else{
            return Math.round((float)image.getWidth()*2/rateo);
        }
    }
    public int getConversionHeight(){
        if(image==null){
            return 0;
        }else{
            return Math.round((float)image.getHeight()/rateo);
        }
    }


    //altri metodi
    public void loadImage(BufferedImage img){
        this.image=img;

    }
    public boolean isInverted() {
        return inverted;

    }
    public char[][] convert(){//[x][y]
        if(image!=null&&palette!=null){
            //calcolo dimensioni matrice
            int nCharX=this.getConversionWidth();
            int nCharY=this.getConversionHeight();
            int actualReadPixels;
            float fCharBrightness;
            char[][] asciiMatrix=new char[nCharX][nCharY];//creo matrice
            Raster imageData=image.getData();//converto immagine in matrice colori

            //compilo matrice con valori luminosità
            for(int i=0;i<asciiMatrix.length/2;i+=1){//X
                for(int j=0;j<asciiMatrix[0].length;j+=1){//Y
                    //sommo in fCharBrightness la luminosità dei pixel letti
                    fCharBrightness=0;
                    actualReadPixels=0;
                    for(int k=0;k<rateo;k++){
                        for(int l=0;l<rateo;l++){
                            if(i*rateo+k<image.getWidth()&&j*rateo+l<image.getHeight()){//se non vado fuori dalla immagine
                                fCharBrightness+=getColorBrightnessDouble(imageData.getPixel(i*rateo+k,j*rateo+l,(int[])null));
                                actualReadPixels++;

                                //debug
                                //System.out.println((i*rateo+k)+ " " +(j*rateo+l) + "     " + imageData.getPixel(i*rateo+k,j*rateo+l,(int[])null)[0] + " " + imageData.getPixel(i*rateo+k,j*rateo+l,(int[])null)[1] + " " + imageData.getPixel(i*rateo+k,j*rateo+l,(int[])null)[2] + "      " + getColorBrightnessDouble(imageData.getPixel(i*rateo+k,j*rateo+l,(int[])null)));

                            }
                        }
                    }
                    fCharBrightness=fCharBrightness/actualReadPixels;//calcolo luminosità zona considerata
                    if(fCharBrightness != 0){
                        //System.out.println(fCharBrightness); //debug
                    } //debug


                    //codifico luminosità in carattere palette
                    if(!inverted){
                        asciiMatrix[i*2][j]=palette.charAt(Math.round(fCharBrightness/(255f/(palette.length()-1))));
                        asciiMatrix[i*2+1][j]=palette.charAt(Math.round(fCharBrightness/(255f/(palette.length()-1))));
                    }else{
                        asciiMatrix[i*2][j]=palette.charAt(palette.length()-1-Math.round(fCharBrightness/(255f/(palette.length()-1))));
                        asciiMatrix[i*2+1][j]=palette.charAt(palette.length()-1-Math.round(fCharBrightness/(255f/(palette.length()-1))));
                    }
                }
            }

            //stampo matrice
            /*
            for(int i=0;i<asciiMatrix[0].length;i++){
                for(int j=0;j<asciiMatrix.length;j++){
                    System.out.print(asciiMatrix[j][i]);
                }
                System.out.println();
            }
            System.out.println("\n\n\n");

             */

            return asciiMatrix;
        }
        return null;
    }
    private static int getColorBrightness(@NotNull Color color){
        int brightness=color.getRed();
        if(color.getBlue()>brightness){
            brightness=color.getRed();
        }
        if(color.getGreen()>brightness){
            brightness=color.getGreen();
        }

        brightness=Math.round((float) brightness*color.getAlpha()/255);

        return brightness;
    }
    private static int getColorBrightness(int red,int green,int blue,int alpha){
        return getColorBrightness(new Color(red,green,blue,alpha));
    }
    private static int getColorBrightness(int red,int green,int blue){
        return getColorBrightness(new Color(red,green,blue,255));
    }
    private static int getColorBrightness(@NotNull int[] vectorOf4){
        if(vectorOf4.length<4){
            return 0;
        }
        return getColorBrightness(vectorOf4[0],vectorOf4[1],vectorOf4[2],vectorOf4[3]);
    }
    private static double getColorBrightnessDouble(@NotNull Color color){
        double brightness=color.getRed();
        if(color.getBlue()>brightness){
            brightness=color.getRed();
        }
        if(color.getGreen()>brightness){
            brightness=color.getGreen();
        }

        brightness=brightness*color.getAlpha()/255;

        return brightness;
    }
    private static double getColorBrightnessDouble(int red,int green,int blue,int alpha){
        return getColorBrightnessDouble(new Color(red,green,blue,alpha));
    }
    private static double getColorBrightnessDouble(int red,int green,int blue){
        return getColorBrightnessDouble(new Color(red,green,blue,255));
    }
    private static double getColorBrightnessDouble(@NotNull int[] vectorOf4){
        if(vectorOf4.length<3){
            return 0;
        }
        if(vectorOf4.length<4){
            return getColorBrightnessDouble(vectorOf4[0],vectorOf4[1],vectorOf4[2],255);
        }
        return getColorBrightnessDouble(vectorOf4[0],vectorOf4[1],vectorOf4[2],vectorOf4[3]);
    }


    public static char[][] convertToAsciiMatrix(BufferedImage image, int numberOfPixelsRepresentedBySingleChar, String ordinatedPalette){






        return null;
    }





}

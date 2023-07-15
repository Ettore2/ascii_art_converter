import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class AsciiConverter {
    //campi statici
    public static final String[] PALETTES={" LJICPHRB"," .¨,;!+?ç@"," .¨,;!+LJICPHRB@"," ░▒▓█"};
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
            return Math.round((float)image.getWidth()*2/rateo) - Math.round((float)image.getWidth()*2/rateo) %2;
        }
    }
    public int getConversionHeight(){
        if(image==null){
            return 0;
        }else{
            return Math.round((float)image.getHeight()/rateo) - Math.round((float)image.getHeight()/rateo)%2;
        }
    }


    //altri metodi
    public void loadImage(BufferedImage img){
        this.image=img;

    }
    public boolean isInverted() {
        return inverted;

    }
    public char[][] convertBrightnessDoubleX(){//[x][y]
        if(image!=null && palette!=null){
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
                    fCharBrightness=0;
                    actualReadPixels=0;

                    //sommo in fCharBrightness la luminosità dei pixel letti
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

                    fCharBrightness = fCharBrightness/actualReadPixels;//calcolo luminosità zona considerata


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

            return asciiMatrix;
        }
        return null;
    }
    public Color[][] convertColorsDoubleX(){

        if(image!=null){
            //calcolo dimensioni matrice
            int nCharX=this.getConversionWidth();
            int nCharY=this.getConversionHeight();
            int actualReadPixels;
            int[] currentColorInfo, averageColorInfo = new int[3];
            Color[][] colorsMatrix=new Color[nCharX][nCharY];//creo matrice
            Raster imageData=image.getData();//converto immagine in matrice colori

            //compilo matrice con valori luminosità
            for(int i=0;i<colorsMatrix.length/2;i+=1){//X
                for(int j=0;j<colorsMatrix[0].length;j+=1) {//Y

                    averageColorInfo[0] = 0;
                    averageColorInfo[1] = 0;
                    averageColorInfo[2] = 0;
                    actualReadPixels = 0;

                    //sommo in averageColorInfo le componenti di colore dei pixel
                    for (int k = 0; k < rateo; k++) {
                        for (int l = 0; l < rateo; l++) {
                            if (i * rateo + k < image.getWidth() && j * rateo + l < image.getHeight()) {//se non vado fuori dalla immagine

                                //raccolgo le informazioni sui colori
                                currentColorInfo = imageData.getPixel(i * rateo + k, j * rateo + l, (int[]) null);
                                averageColorInfo[0] += currentColorInfo[0];
                                averageColorInfo[1] += currentColorInfo[1];
                                averageColorInfo[2] += currentColorInfo[2];

                                actualReadPixels++;
                            }
                        }
                    }

                    averageColorInfo[0] = averageColorInfo[0] / actualReadPixels;
                    averageColorInfo[1] = averageColorInfo[1] / actualReadPixels;
                    averageColorInfo[2] = averageColorInfo[2] / actualReadPixels;

                    //compilo matrice colori
                    Color color = new Color(averageColorInfo[0], averageColorInfo[1], averageColorInfo[2]);
                    colorsMatrix[i * 2][j] = color;
                    colorsMatrix[i * 2 + 1][j] = color;


                    //System.out.println(i * 2 + "    " + j); //debug
                    //System.out.println(i * 2 + 1 + "    " + j); //debug
                    //System.out.println(color.getRed()+" "+color.getGreen()+"  "+color.getBlue()+" " + "\n"); //debug
                }
            }

            return colorsMatrix;
        }
        return null;
    }
    public Color[][] convertColorsSingleX(){

        if(image!=null){
            //calcolo dimensioni matrice
            int nCharX = this.getConversionWidth() / 2;
            int nCharY = this.getConversionHeight();
            int actualReadPixels;
            int[] currentColorInfo, averageColorInfo = new int[3];
            Color[][] colorsMatrix = new Color[nCharX][nCharY];//creo matrice
            Raster imageData=image.getData();//converto immagine in matrice colori

            //compilo matrice con valori luminosità
            for(int i=0;i<colorsMatrix.length;i+=1){//X
                for(int j=0;j<colorsMatrix[0].length;j+=1) {//Y

                    averageColorInfo[0] = 0;
                    averageColorInfo[1] = 0;
                    averageColorInfo[2] = 0;
                    actualReadPixels = 0;

                    //sommo in averageColorInfo le componenti di colore dei pixel
                    for (int k = 0; k < rateo; k++) {
                        for (int l = 0; l < rateo; l++) {
                            if (i * rateo + k < image.getWidth() && j * rateo + l < image.getHeight()) {//se non vado fuori dalla immagine

                                //raccolgo le informazioni sui colori
                                currentColorInfo = imageData.getPixel(i * rateo + k, j * rateo + l, (int[]) null);
                                averageColorInfo[0] += currentColorInfo[0];
                                averageColorInfo[1] += currentColorInfo[1];
                                averageColorInfo[2] += currentColorInfo[2];

                                actualReadPixels++;
                            }
                        }
                    }

                    averageColorInfo[0] = averageColorInfo[0] / actualReadPixels;
                    averageColorInfo[1] = averageColorInfo[1] / actualReadPixels;
                    averageColorInfo[2] = averageColorInfo[2] / actualReadPixels;

                    //compilo matrice colori
                    Color color = new Color(averageColorInfo[0], averageColorInfo[1], averageColorInfo[2]);
                    colorsMatrix[i][j] = color;

                }
            }

            return colorsMatrix;
        }
        return null;
    }





    //metodi privati
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
    private static int getColorBrightnessInt(int red,int green,int blue){
        return getColorBrightness(new Color(red,green,blue,255));
    }
    private static int getColorBrightnessInt(@NotNull int[] colorInfo){
        if(colorInfo.length<4){
            return 0;
        }
        return getColorBrightness(colorInfo[0],colorInfo[1],colorInfo[2],colorInfo[3]);
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
    private static double getColorBrightnessDouble(@NotNull int[] coloInfo){
        if(coloInfo.length<3){
            return 0;
        }
        if(coloInfo.length<4){
            return getColorBrightnessDouble(coloInfo[0],coloInfo[1],coloInfo[2],255);
        }
        return getColorBrightnessDouble(coloInfo[0],coloInfo[1],coloInfo[2],coloInfo[3]);
    }


    public static char[][] convertToAsciiMatrix(BufferedImage image, int numberOfPixelsRepresentedBySingleChar, String ordinatedPalette){






        return null;
    }





}

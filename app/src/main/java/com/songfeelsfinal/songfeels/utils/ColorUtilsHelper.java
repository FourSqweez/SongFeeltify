package com.songfeelsfinal.songfeels.utils;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

/**
 * Java Code to get a color name from rgb/hex value/awt color
 * <p>
 * The part of looking up a color name from the rgb values is edited from
 * https://gist.github.com/nightlark/6482130#file-gistfile1-java (that has some errors) by Ryan Mast (nightlark)
 *
 * @author Xiaoxiao Li
 */
public class ColorUtilsHelper {

    /**
     * Initialize the color list that we have.
     */
    private ArrayList<ColorName> initColorList() {
        ArrayList<ColorName> colorList = new ArrayList<ColorName>();
        //https://github.com/matthiaskern/spotify-react-picturify thanks music genre with color
        colorList.add(new ColorName("rock,metal,alternative,indie,hip-hop,rap,pop", 0xFF, 0x00, 0x00)); //red
        colorList.add(new ColorName("reggae,country,folk", 0x00, 0xFF, 0x00)); //green
        colorList.add(new ColorName("latin,electronic,dance,latin", 0xFF, 0xFF, 0x00)); //yellow
        colorList.add(new ColorName("blues,classical,jazz,new-age", 0x00, 0x00, 0xFF)); //blue
        colorList.add(new ColorName("metal,rock,alternative,indie,new-age", 0x00, 0x00, 0x00)); //black
        colorList.add(new ColorName("classical,gospel,new-age", 0xFF, 0xFF, 0xFF)); //white
        colorList.add(new ColorName("pop,electronic,dance,soul,rnb,funk,classical,jazz", 0xFF, 0x00, 0xFF)); //pink
        colorList.add(new ColorName("electronic,dance,new-age", 0x00, 0xFF, 0xFF)); //cyan
        colorList.add(new ColorName("classical,metal", 0x80, 0x80, 0x80)); //grey
        colorList.add(new ColorName("reggae,soul,r-n-b,funk,latin", 0xFF, 0xA5, 0x00)); //orange
        colorList.add(new ColorName("country,folk", 0xA5, 0x2A, 0x2A)); //brown
        colorList.add(new ColorName("electronic,dance,new-age,soul,r-n-b,funk,gospel", 0x80, 0x00, 0x80)); //purple
//        colorList.add(new ColorName("R&B", 0xBE, 0x67, 0x1A));
//        colorList.add(new ColorName("RAP", 0x6B, 0x9C, 0xB4));
//        colorList.add(new ColorName("REGGAE", 0x09, 0x64, 0x41));
//        colorList.add(new ColorName("ROCK", 0xB5, 0x46, 0x32));

        return colorList;
    }

    /**
     * Get the closest color name from our list
     *
     * @param r
     * @param g
     * @param b
     * @return
     */
    public String getColorNameFromRgb(int r, int g, int b) {
        ArrayList<ColorName> colorList = initColorList();
        ColorName closestMatch = null;
        int minMSE = Integer.MAX_VALUE;
        int mse;
        for (ColorName c : colorList) {
            mse = c.computeMSE(r, g, b);
            if (mse < minMSE) {
                minMSE = mse;
                closestMatch = c;
            }
        }

        if (closestMatch != null) {
            Log.i("ColorUtils", "=> " + Color.rgb(closestMatch.getR(), closestMatch.getG(), closestMatch.getB()));
            return closestMatch.getName();
        } else {
            return "No matched color name.";
        }
    }

    /**
     * SubClass of ColorUtils. In order to lookup color name
     *
     * @author Xiaoxiao Li
     */

    public static class ColorName {
        public int r, g, b;
        public String name;

        public ColorName(String name, int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.name = name;
        }

        public ColorName() {

        }

        public int computeMSE(int pixR, int pixG, int pixB) {
            return (int) (((pixR - r) * (pixR - r) + (pixG - g) * (pixG - g) + (pixB - b)
                    * (pixB - b)) / 3);
        }

        public int getR() {
            return r;
        }

        public int getG() {
            return g;
        }

        public int getB() {
            return b;
        }

        public String getName() {
            return name;
        }
    }
}
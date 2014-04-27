/* 
 * Copyright (C) 2014 Alec Dhuse
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.foldingmap.map.themes;

import java.awt.Color;

/**
 * Utility class for working with different color formats.
 * 
 * @author Alec Dhuse
 */
public class ColorHelper {
    
    /**
     * Returns the hex version of a Color object.  
     * Output is alpha blue green red.
     * 
     * @param c
     * @return 
     */
    public static String getColorHexAlphabetical(Color c) {
        String hexColor, a, b, g, r;

        a = Integer.toHexString(c.getAlpha());
        b = Integer.toHexString(c.getBlue());
        g = Integer.toHexString(c.getGreen());
        r = Integer.toHexString(c.getRed());

        if (a.length() == 1)
            a = "0" + a;

        if (b.length() == 1)
            b = "0" + b;

        if (g.length() == 1)
            g = "0" + g;

        if (r.length() == 1)
            r = "0" + r;

        hexColor =  a + b + g + r;

        return hexColor;
    }    
    
    /**
     * Returns the hex version of a Color object.  
     * Output is red green blue alpha.
     * 
     * @param c
     * @return 
     */
    public static String getColorHexStandard(Color c) {
        String hexColor, a, b, g, r;

        a = Integer.toHexString(c.getAlpha());
        b = Integer.toHexString(c.getBlue());
        g = Integer.toHexString(c.getGreen());
        r = Integer.toHexString(c.getRed());

        if (a.length() == 1)
            a = "0" + a;

        if (b.length() == 1)
            b = "0" + b;

        if (g.length() == 1)
            g = "0" + g;

        if (r.length() == 1)
            r = "0" + r;

        hexColor =  r + g + b + a;

        return hexColor;
    }     
    
    /**
     * Parses a String representing a KML color coded in hex.
     * KML hex format is: aabbggrr.  
     * 
     * @param  hexString               
     * @return A color object representing the hex string.
     */
    public static Color parseHexAlphabetical(String hexString) {
        int    decimalAlpha;
        Color  returnColor = Color.BLACK;
        String alpha, blue, green, red;

        try {
            if (hexString.length() == 8) {
                alpha             = hexString.substring(0, 2);
                blue              = hexString.substring(2, 4);
                green             = hexString.substring(4, 6);
                red               = hexString.substring(6, 8);
                returnColor       = Color.decode("#" + red + green + blue);
                decimalAlpha      = parseHexPair(alpha);

                returnColor = new Color(returnColor.getRed(), returnColor.getGreen(), returnColor.getBlue(), decimalAlpha);
            }
        } catch (Exception e) {
            System.out.println("Error in ColorHelper.parseHexString - " + e);
        }

        return returnColor;
    }    
    
    /**
     * Parses a pair of hex vales and returns the int value.
     * 
     * @param pair
     * @return integer value between 0 and 255
     * @throws Exception If the length of the parameter is any other length but 2.
     */
    private static int parseHexPair(String pair) throws Exception {
        char currentDigit;
        int  decimal = 0;
        int  digitDecimalValue = 0;
        
        if (pair.length() == 2) {
            for (int i = 1; i >= 0; i--) {
                currentDigit = pair.charAt(i);

                switch (currentDigit) {
                    case '0':
                        digitDecimalValue = 0;
                        break;
                    case '1':
                        digitDecimalValue = 1;
                        break;
                    case '2':
                        digitDecimalValue = 2;
                        break;
                    case '3':
                        digitDecimalValue = 3;
                        break;
                    case '4':
                        digitDecimalValue = 4;
                        break;
                    case '5':
                        digitDecimalValue = 5;
                        break;
                    case '6':
                        digitDecimalValue = 6;
                        break;
                    case '7':
                        digitDecimalValue = 7;
                        break;
                    case '8':
                        digitDecimalValue = 8;
                        break;
                    case '9':
                        digitDecimalValue = 9;
                        break;
                    case 'a':
                        digitDecimalValue = 10;
                        break;
                    case 'b':
                        digitDecimalValue = 11;
                        break;
                    case 'c':
                        digitDecimalValue = 12;
                        break;
                    case 'd':
                        digitDecimalValue = 13;
                        break;
                    case 'e':
                        digitDecimalValue = 14;
                        break;
                    case 'f':
                        digitDecimalValue = 15;
                        break;
                }

                if (i == 1) {
                    decimal += (digitDecimalValue * (1));
                } else if (i == 0) {
                    decimal += (digitDecimalValue * (16));
                }
            }    

            return decimal;
        } else {
            throw new Exception("String value has invalid string length.");
        }
    }
    
    /**
     * Parses a String representing a web color coded in hex.
     * KML hex format is: rrggbbaa.  
     * 
     * @param  hexString               
     * @return A color object representing the hex string.
     */
    public static Color parseHexStandard(String hexString) {
        int    decimalAlpha;
        Color  returnColor = Color.BLACK;
        String alpha, blue, green, red;

        try {
            if (hexString.length() == 8) {
                red               = hexString.substring(0, 2);
                green             = hexString.substring(2, 4);
                blue              = hexString.substring(4, 6);
                alpha             = hexString.substring(6, 8);
                returnColor       = Color.decode("#" + red + green + blue);
                decimalAlpha      = parseHexPair(alpha);

                returnColor = new Color(returnColor.getRed(), returnColor.getGreen(), returnColor.getBlue(), decimalAlpha);
            }
        } catch (Exception e) {
            System.out.println("Error in ColorHelper.parseHexString - " + e);
        }

        return returnColor;
    }      
}

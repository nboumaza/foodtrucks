package com.nboumaza.foodies.util;

public class Numeric {

    public static boolean isValidDouble(String string) {

        try {
           Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isValidInt(String string) {

        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}

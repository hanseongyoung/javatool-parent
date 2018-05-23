package com.syhan.javatool.share.util.string;

public class StringUtil extends org.apache.commons.lang3.StringUtils {
    //
    public static String getRecommendedVariableName(String name) {
        //
        return toFirstLowerCase(name);
    }

    public static String toFirstLowerCase(String str) {
        //
        if (str == null) return null;

        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        }

        char c[] = str.toCharArray();
        c[0] += 32;
        return new String(c);
    }

    public static String toFirstUpperCase(String str) {
        //
        if (str == null) return null;

        if (Character.isUpperCase(str.charAt(0))) {
            return str;
        }

        char c[] = str.toCharArray();
        c[0] -= 32;
        return new String(c);
    }
}

package com.syhan.javatool.share.util.string;

public class StringUtil extends org.apache.commons.lang3.StringUtils {
    //
    public static String getRecommendedVariableName(String name) {
        //
        if (isJavaKeyword(name)) {
            return name + "00";
        }
        return toFirstLowerCase(name);
    }

    private static final String[] JAVA_KEYWORDS = {"abstract","boolean","break","byte","catch","char","class","continue",
        "default","do","double","else","extends","finally","float","for","if","implements","import","instanceof",
        "int","interface","long","native","new","null","package","private","protected","public","return","short",
        "static","super","switch","sychronized","this","throw","throws","try","void","while"};

    private static boolean isJavaKeyword(String name) {
        //
        String varName = name.toLowerCase();
        for (String keyword : JAVA_KEYWORDS) {
            if (keyword.equals(varName)) {
                return true;
            }
        }
        return false;
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

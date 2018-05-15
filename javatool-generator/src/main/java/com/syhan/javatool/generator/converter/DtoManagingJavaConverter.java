package com.syhan.javatool.generator.converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DtoManagingJavaConverter {
    //
    private static Map<String, String> DTO_HISTORY = new HashMap<>();

    private JavaConverter javaConverter;

    public DtoManagingJavaConverter(JavaConverter javaConverter) {
        //
        this.javaConverter = javaConverter;
    }

    public void convert(String dtoSourceFileName) {
        //
        if (DTO_HISTORY.containsKey(dtoSourceFileName)) {
            System.out.println("Already dto converted --> " + dtoSourceFileName);
            return;
        }

        try {
            System.out.println("Convert dto --> " + dtoSourceFileName);
            javaConverter.convert(dtoSourceFileName);
            DTO_HISTORY.put(dtoSourceFileName, dtoSourceFileName);
        } catch (IOException e) {
            // TODO : using Logger
            System.err.println("Can't convert dto --> " + dtoSourceFileName + ", " + e.getMessage());
        }
    }
}

package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.source.JavaSource;

import java.io.IOException;

import static com.syhan.javatool.share.rule.ChangeHistoryManager.CHANGE_HISTORY;

public class DtoManagingJavaConverter {
    //
    private JavaConverter javaConverter;

    public DtoManagingJavaConverter(JavaConverter javaConverter) {
        //
        this.javaConverter = javaConverter;
    }

    public void convert(String dtoSourceFileName) {
        //
        if (CHANGE_HISTORY.containsKeyBySourceFileName(dtoSourceFileName)) {
            System.err.println("Already dto converted --> " + dtoSourceFileName);
            return;
        }

        try {
            //System.out.println("Convert dto --> " + dtoSourceFileName);
            javaConverter
                    .customCodeHandle(this::makeDtoCustomCode)
                    .changeInfoHandle(changeImport -> CHANGE_HISTORY.put(changeImport))
                    .convert(dtoSourceFileName);

        } catch (IOException e) {
            // TODO : using Logger
            System.err.println("Can't convert dto --> " + dtoSourceFileName + ", " + e.getMessage());
        }
    }

    private void makeDtoCustomCode(JavaSource javaSource) {
        //
        // extends kr.amc.amil.message.dto.AbstractDTO
        javaSource.setExtendedType("AbstractDTO", "kr.amc.amil.message.dto");

        // add annotation
        javaSource.addAnnotation("Getter", "lombok");
        javaSource.addAnnotation("Setter", "lombok");
        javaSource.addAnnotation("NoArgsConstructor", "lombok");
        javaSource.addAnnotation("ToString", "lombok");

        // remove getter / setter / toString
        javaSource.removeGetterAndSetter();
        javaSource.removeMethod("toString");

        // remove default constructor
        javaSource.removeNoArgsConstructor();

    }
}

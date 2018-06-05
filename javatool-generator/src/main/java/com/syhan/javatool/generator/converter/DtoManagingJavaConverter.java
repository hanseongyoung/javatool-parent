package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.file.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DtoManagingJavaConverter extends ProjectItemConverter {
    //
    private static Logger logger = LoggerFactory.getLogger(DtoManagingJavaConverter.class);

    private PackageRule packageRule;
    private JavaConverter serviceJavaConverter;
    private JavaConverter stubJavaConverter;

    public DtoManagingJavaConverter(PackageRule packageRule, JavaConverter serviceJavaConverter, JavaConverter stubJavaConverter) {
        //
        super(serviceJavaConverter.sourceConfiguration, serviceJavaConverter.projectItemType);

        this.packageRule = packageRule;
        this.serviceJavaConverter = serviceJavaConverter;
        this.stubJavaConverter = stubJavaConverter;
    }

    @Override
    public void convert(String dtoSourceFileName) {
        //
        try {
            String dtoClassName = PathUtil.toClassName(dtoSourceFileName);
            if (packageRule.containsChangeImport(dtoClassName)) {
                stubJavaConverter
                        .customCodeHandle(this::makeDtoCustomCode)
                        .convert(dtoSourceFileName);
            } else {
                serviceJavaConverter
                        .customCodeHandle(this::makeDtoCustomCode)
                        .convert(dtoSourceFileName);
            }
        } catch (IOException e) {
            logger.error("Can't convert dto --> {}, {}", dtoSourceFileName , e.getMessage());
        }
    }

    private void makeDtoCustomCode(JavaSource javaSource) {
        //
        boolean hasVOProperty = javaSource.hasProperty("VO", "TO");
        if (hasVOProperty) {
            javaSource.setExtendedType("AbstractCompositeDTO", "kr.amc.amil.message.dto");
        } else {
            javaSource.setExtendedType("AbstractDTO", "kr.amc.amil.message.dto");
        }

        // add annotation
        javaSource.addAnnotation("Getter", "lombok");
        javaSource.addAnnotation("Setter", "lombok");
        javaSource.addAnnotation("NoArgsConstructor", "lombok");
        //javaSource.addAnnotation("ToString", "lombok");

        // remove getter / setter / toString
        javaSource.removeGetterAndSetter();
        //javaSource.removeMethod("toString");

        // remove default constructor
        javaSource.removeNoArgsConstructor();

    }
}

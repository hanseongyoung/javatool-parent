package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.file.PathUtil;

import java.io.FileNotFoundException;
import java.io.IOException;

public class JavaConverter extends ProjectItemConverter {
    //
    private PackageRule packageRule;

    public JavaConverter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration) {
        //
        this(sourceConfiguration, targetConfiguration, null);
    }

    public JavaConverter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration, PackageRule packageRule) {
        //
        super(sourceConfiguration, targetConfiguration, ProjectItemType.Java);
        this.packageRule = packageRule;
    }

    // change package rule
    // amis3.mc.oo.od.controller -> kr.amc.amis.mc.order.od.rest
    //  - 0, amis3      -> kr.amc.amis
    //  - 2, oo         -> mc.order
    //  - 4, controller -> rest

    @Override
    public void convert(String sourceFilePath) throws IOException {
        // sourceFile : com/foo/bar/SampleService.java
        String physicalSourceFilePath = sourceConfiguration.makePhysicalJavaSourceFilePath(sourceFilePath);

        JavaSource source = readSource(physicalSourceFilePath);
        source.changePackage(packageRule);
        source.changeImports(packageRule);

        String targetSourceFileName = PathUtil.toSourceFileName(source.getClassName(), "java");
        String physicalTargetFilePath = targetConfiguration.makePhysicalJavaSourceFilePath(targetSourceFileName);
        writeSource(source, physicalTargetFilePath);
    }

    private String adjustPackage(String packageName) {
        //
        if (packageRule == null) {
            return packageName;
        }
        return packageRule.changePackage(packageName);
    }

    private JavaSource readSource(String physicalSourceFilePath) throws FileNotFoundException {
        //
        return new JavaSource(physicalSourceFilePath);
    }

    private void writeSource(JavaSource source, String physicalTargetFilePath) throws IOException {
        //
        source.write(physicalTargetFilePath);
    }
}

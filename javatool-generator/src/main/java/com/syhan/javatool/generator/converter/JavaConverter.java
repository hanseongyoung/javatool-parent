package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.reader.JavaReader;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;

import java.io.IOException;
import java.util.function.Consumer;

public class JavaConverter extends ProjectItemConverter {
    //
    private JavaReader javaReader;
    private JavaWriter javaWriter;
    private NameRule nameRule;
    private PackageRule packageRule;

    private Consumer<JavaSource> customCodeHandler;
    private Consumer<PackageRule.ChangeImport> changeInfoHandler;

    public JavaConverter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration) {
        //
        this(sourceConfiguration, targetConfiguration, null, null);
    }

    public JavaConverter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration,
                         NameRule nameRule, PackageRule packageRule) {
        //
        super(sourceConfiguration, targetConfiguration, ProjectItemType.Java);
        this.nameRule = nameRule;
        this.packageRule = packageRule;
        this.javaReader = new JavaReader(sourceConfiguration);
        this.javaWriter = new JavaWriter(targetConfiguration);
    }

    public JavaConverter customCodeHandle(Consumer<JavaSource> customCodeHandler) {
        //
        this.customCodeHandler = customCodeHandler;
        return this;
    }

    public JavaConverter changeInfoHandle(Consumer<PackageRule.ChangeImport> changeInfoHandler) {
        this.changeInfoHandler = changeInfoHandler;
        return this;
    }

    @Override
    public void convert(String sourceFilePath) throws IOException {
        // sourceFile : com/foo/bar/SampleService.java
        JavaSource source = javaReader.read(sourceFilePath);
        String beforeClassName = source.getClassName();

        source.changePackage(packageRule);
        source.changeName(nameRule);
        source.removeImports(packageRule);
        source.changeImports(nameRule, packageRule);
        source.changeMethodUsingClassName(nameRule);

        String afterClassName = source.getClassName();

        if (customCodeHandler != null) {
            customCodeHandler.accept(source);
        }
        if (changeInfoHandler != null) {
            PackageRule.ChangeImport changeImport = new PackageRule.ChangeImport(beforeClassName, afterClassName);
            changeInfoHandler.accept(changeImport);
        }

        javaWriter.write(source);
    }

}
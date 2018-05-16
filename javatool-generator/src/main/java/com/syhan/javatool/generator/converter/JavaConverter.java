package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.reader.JavaReader;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;

import java.io.IOException;

public class JavaConverter extends ProjectItemConverter {
    //
    private JavaReader javaReader;
    private JavaWriter javaWriter;
    private NameRule nameRule;
    private PackageRule packageRule;

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

    @Override
    public void convert(String sourceFilePath) throws IOException {
        // sourceFile : com/foo/bar/SampleService.java
        JavaSource source = javaReader.read(sourceFilePath);

        source.changePackage(packageRule);
        source.changeName(nameRule);
        source.changeImports(nameRule, packageRule);

        javaWriter.write(source);
    }
}

package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.reader.JavaReader;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.share.config.ProjectConfiguration;

import java.io.IOException;

// com.foo.bar.SomeService
// -->
// [Interface]com.foo.bar.spec.SomeService
// [Class]    com.foo.bar.logic.SomeLogic
// [DTO]      com.foo.bar.spec.sdo.SomeDTO
public class JavaInterfaceAbstracter extends ProjectItemConverter {
    //
    private JavaReader javaReader;
    private JavaWriter javaWriter;

    public JavaInterfaceAbstracter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration) {
        //
        super(sourceConfiguration, targetConfiguration, ProjectItemType.Java);
        this.javaReader = new JavaReader(sourceConfiguration);
        this.javaWriter = new JavaWriter(targetConfiguration);
    }

    @Override
    public void convert(String sourceFileName) throws IOException {
        //
        JavaSource source = javaReader.read(sourceFileName);
        JavaModel interfaceModel = createJavaInterfaceModel(source);
        javaWriter.write(new JavaSource(interfaceModel));
    }

    private JavaModel createJavaInterfaceModel(JavaSource source) {
        //
        String className = "";
        JavaModel javaModel = source.toModel();
        return javaModel;
    }
}

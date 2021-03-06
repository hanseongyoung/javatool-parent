package com.syhan.javatool.generator;

import com.syhan.javatool.generator.model.ClassType;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
import com.syhan.javatool.generator.model.ParameterModel;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.generator.writer.Writer;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

public class JavaWrite {
    //
    private static final String TARGET_PROJECT_PATH = "./source-project";

    public static void main(String[] args) throws Exception {
        //
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, TARGET_PROJECT_PATH);

        // 1. java file write
        JavaSource source = createSampleJavaSource();
        Writer<JavaSource> writer = new JavaWriter(targetConfiguration);
        writer.write(source);
    }

    private static JavaSource createSampleJavaSource() {
        //
        JavaModel javaModel = new JavaModel("com.foo.bar.Test", true);

        MethodModel methodModel = new MethodModel("hello", ClassType.newClassType("com.foo.bar.ResultDTO"));
        ParameterModel parameterModel = new ParameterModel(ClassType.newClassType("com.foo.bar.TestDTO"), "testDTO");
        methodModel.addParameterModel(parameterModel);
        javaModel.addMethodModel(methodModel);

        return new JavaSource(javaModel);
    }
}

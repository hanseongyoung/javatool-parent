package com.syhan.javatool.generator;

import com.syhan.javatool.generator.model.ClassType;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.generator.writer.Writer;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

public class JavaWrite {
    //
    private static final String TARGET_PROJECT_PATH = "/Users/daniel/Documents/work/source_gen/javatool-parent/source-project";

    public static void main(String[] args) throws Exception {
        //
        ProjectConfiguration targetConfiguration = new ProjectConfiguration(ConfigurationType.Target, TARGET_PROJECT_PATH);

        // 1. java file write
        JavaSource source = createSampleJavaSource();
        Writer writer = new JavaWriter(targetConfiguration);
        writer.write(source);
    }

    private static JavaSource createSampleJavaSource() {
        //
        JavaModel javaModel = new JavaModel("com.foo.bar.Test", true);

        MethodModel methodModel = new MethodModel("hello", new ClassType("com.foo.bar.ResultDTO"));
        methodModel.addParameterType(new ClassType("com.foo.bar.TestDTO"));
        javaModel.addMethodModel(methodModel);

        return new JavaSource(javaModel);
    }
}

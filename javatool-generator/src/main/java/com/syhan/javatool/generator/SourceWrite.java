package com.syhan.javatool.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.VoidType;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.generator.writer.Writer;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;

import java.util.EnumSet;

public class SourceWrite {
    //
    private static final String TARGET_PROJECT_PATH = "/Users/daniel/Documents/work/source_gen/source-gen-work/target-project";

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
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration("com.foo.bar");

        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
        ClassOrInterfaceDeclaration classType = new ClassOrInterfaceDeclaration(modifiers, true, "Test");
        classType.setInterface(true);

        //classType.addMethod("hello", Modifier.PUBLIC);

        MethodDeclaration method = new MethodDeclaration(EnumSet.noneOf(Modifier.class), new VoidType(), "hello");
        method.setBody(null);
        Parameter parameter = new Parameter(JavaParser.parseClassOrInterfaceType("String"), "greeting");
        method.addParameter(parameter);

        classType.addMember(method);

        compilationUnit.addType(classType);

        return new JavaSource(compilationUnit);
    }
}

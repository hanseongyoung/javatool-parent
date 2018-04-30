package com.syhan.javatool.generator.source;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.syhan.javatool.generator.model.ClassType;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class JavaSource {
    //
    private CompilationUnit compilationUnit;

    public JavaSource(String physicalSourceFile) throws FileNotFoundException {
        //
        this.compilationUnit = JavaParser.parse(new FileInputStream(physicalSourceFile));
    }

    public JavaSource(JavaModel model) {
        //
        this.compilationUnit = createCompilationUnit(model);
    }

    private CompilationUnit createCompilationUnit(JavaModel model) {
        //
        CompilationUnit compilationUnit = new CompilationUnit();

        // package
        compilationUnit.setPackageDeclaration(model.getPackageName());

        // import
        List<String> importClassNames = model.computeImports();
        for (String className : importClassNames) {
            compilationUnit.addImport(className);
        }

        // Class
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
        ClassOrInterfaceDeclaration classType = new ClassOrInterfaceDeclaration(modifiers, model.isInterface(), model.getName());
        if (model.hasAnnotation()) {
            classType.addMarkerAnnotation(model.getAnnotation().getName());
        }

        // Method
        for (MethodModel methodModel : model.getMethods()) {
            classType.addMember(createMethodDeclaration(methodModel));
        }

        compilationUnit.addType(classType);

        return compilationUnit;
    }

    private MethodDeclaration createMethodDeclaration(MethodModel methodModel) {
        //
        Type returnType = null;
        if (methodModel.isVoid()) {
            returnType = new VoidType();
        } else {
            returnType = JavaParser.parseClassOrInterfaceType(methodModel.getReturnType().getName());
        }

        MethodDeclaration method = new MethodDeclaration(EnumSet.noneOf(Modifier.class), returnType, methodModel.getName());
        method.setBody(null);

        // Parameter
        for (ClassType parameterType : methodModel.getParameterTypes()) {
            Parameter parameter = new Parameter(JavaParser.parseClassOrInterfaceType(parameterType.getName()), parameterType.getRecommendedVariableName());
            method.addParameter(parameter);
        }
        return method;
    }

    // com/foo/bar/SampleService.java
    public String getSourceFilePath() {
        //
        String packageName = compilationUnit.getPackageDeclaration().get().getNameAsString();
        String typeName = compilationUnit.getType(0).getNameAsString();
        return packageName.replaceAll("\\.", File.separator) + File.separator + typeName + ".java";
    }

    public void write(String physicalTargetFilePath) throws IOException {
        //
        File file = new File(physicalTargetFilePath);
        System.out.println(compilationUnit.toString());
        FileUtils.writeStringToFile(file, compilationUnit.toString(), "UTF-8");
    }
}

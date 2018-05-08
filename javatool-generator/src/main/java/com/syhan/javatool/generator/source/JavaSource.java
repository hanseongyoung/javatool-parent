package com.syhan.javatool.generator.source;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
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
import java.util.Optional;
import java.util.regex.Matcher;

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

    public JavaModel toModel() {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        String name = classType.getNameAsString();
        String packageName = compilationUnit.getPackageDeclaration()
                .map(PackageDeclaration::getName)
                .map(Name::asString)
                .get();
        boolean isInterface = classType.isInterface();

        JavaModel javaModel = new JavaModel(name, packageName, isInterface);

        for (Object member : classType.getMembers()) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                javaModel.addMethodModel(toMethodModel(method));
            }
        }
        return javaModel;
    }

    private MethodModel toMethodModel(MethodDeclaration method) {
        //
        String name = method.getNameAsString();
        String returnTypeName = ((ClassOrInterfaceType)method.getType()).getName().asString();
        String returnTypeFullName = findFullName(returnTypeName);
        ClassType returnType = new ClassType(returnTypeFullName);

        // if return type has Generic type (List<SampleDTO>)
        ClassType typeArgument = toReturnTypeArgument(method);
        returnType.setTypeArgument(typeArgument);

        MethodModel methodModel = new MethodModel(name, returnType);
        return methodModel;
    }

    private ClassType toReturnTypeArgument(MethodDeclaration method) {
        //
        Optional<NodeList<Type>> returnTypeArguments = ((ClassOrInterfaceType) method.getType()).getTypeArguments();
        if (!returnTypeArguments.isPresent()) {
            return null;
        }
        String returnTypeArgumentName = returnTypeArguments.get().get(0).asString();
        String returnTypeArgumentFullName = findFullName(returnTypeArgumentName);
        return new ClassType(returnTypeArgumentFullName);
    }

    private String findFullName(String returnTypeName) {
        //
        for (Object obj : compilationUnit.getImports()) {
            ImportDeclaration importDeclaration = (ImportDeclaration) obj;
            String packageName = importDeclaration.getName().asString();
            if (packageName.endsWith(returnTypeName)) {
                return packageName;
            }
        }
        return returnTypeName;
    }

    public static boolean exists(String physicalSourceFile) {
        //
        File file = new File(physicalSourceFile);
        return file.exists() && !file.isDirectory();
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
        } else if (methodModel.isPrimitive()) {
            returnType = new PrimitiveType(PrimitiveType.Primitive.valueOf(methodModel.getReturnType().getName()));
        } else {
            returnType = createClassReturnType(methodModel);
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

    private ClassOrInterfaceType createClassReturnType(MethodModel methodModel) {
        //
        ClassOrInterfaceType returnType = JavaParser.parseClassOrInterfaceType(methodModel.getReturnType().getName());

        ClassType modelReturnType = methodModel.getReturnType();
        if (modelReturnType.hasTypeArgument()) {
            ClassType modelArgumentType = modelReturnType.getTypeArgument();
            NodeList<Type> typeArguments = new NodeList<>();
            typeArguments.add(JavaParser.parseClassOrInterfaceType(modelArgumentType.getName()));
            returnType.setTypeArguments(typeArguments);
        }
        return returnType;
    }

    // com/foo/bar/SampleService.java
    public String getSourceFilePath() {
        //
        String packageName = compilationUnit.getPackageDeclaration().get().getNameAsString();
        String typeName = compilationUnit.getType(0).getNameAsString();
        return packageName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator + typeName + ".java";
    }

    public void write(String physicalTargetFilePath) throws IOException {
        //
        File file = new File(physicalTargetFilePath);
        System.out.println(compilationUnit.toString());
        FileUtils.writeStringToFile(file, compilationUnit.toString(), "UTF-8");
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public static void main(String[] args) {
        // In the case of Windows replaceAll with File.separator causes 'character to be escaped is missing' error.
        String packageName = "com.foo.bar";
        //System.out.println(packageName.replaceAll("\\.", File.separator));
        System.out.println(packageName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
    }
}

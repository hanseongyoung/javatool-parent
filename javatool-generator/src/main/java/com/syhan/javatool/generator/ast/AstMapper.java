package com.syhan.javatool.generator.ast;

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

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AstMapper {
    //
    // Ast:CompilationUnit -> Model:JavaModel
    public static JavaModel toJavaModel(CompilationUnit compilationUnit, FullNameProvider fullNameProvider) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        String name = classType.getNameAsString();
        String packageName = compilationUnit.getPackageDeclaration()
                .map(PackageDeclaration::getName)
                .map(Name::asString)
                .orElseThrow(IllegalArgumentException::new);

        boolean isInterface = classType.isInterface();

        JavaModel javaModel = new JavaModel(name, packageName, isInterface);

        for (Object member : classType.getMembers()) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                javaModel.addMethodModel(toMethodModel(method, fullNameProvider));
            }
        }
        return javaModel;
    }

    // Model:JavaModel -> Ast:CompilationUnit
    public static CompilationUnit createCompilationUnit(JavaModel javaModel) {
        //
        CompilationUnit compilationUnit = new CompilationUnit();

        // package
        compilationUnit.setPackageDeclaration(javaModel.getPackageName());

        // import
        List<String> importClassNames = javaModel.computeImports();
        for (String className : importClassNames) {
            compilationUnit.addImport(className);
        }

        // Class
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
        ClassOrInterfaceDeclaration classType = new ClassOrInterfaceDeclaration(modifiers, javaModel.isInterface(), javaModel.getName());
        if (javaModel.hasAnnotation()) {
            classType.addMarkerAnnotation(javaModel.getAnnotation().getName());
        }

        // Method
        for (MethodModel methodModel : javaModel.getMethods()) {
            MethodDeclaration methodDeclaration = createMethodDeclaration(methodModel);
            classType.addMember(methodDeclaration);
        }

        compilationUnit.addType(classType);

        return compilationUnit;
    }

    // Ast:MethodDeclaration -> Model:MethodModel
    public static MethodModel toMethodModel(MethodDeclaration method, FullNameProvider fullNameProvider) {
        //
        String name = method.getNameAsString();
        AccessSpecifier access = Modifier.getAccessSpecifier(method.getModifiers());
        Type methodType = method.getType();

        ClassType returnType = toClassType(methodType, fullNameProvider);

        MethodModel methodModel = new MethodModel(name, returnType);
        methodModel.setAccess(access.asString());
        for(Parameter parameter : method.getParameters()) {
            ClassType parameterType = toClassType(parameter.getType(), fullNameProvider);
            methodModel.addParameterType(parameterType);
        }

        return methodModel;
    }

    // Model:MethodModel -> Ast:MethodDeclaration
    public static MethodDeclaration createMethodDeclaration(MethodModel methodModel) {
        //
        ClassType methodClassType = methodModel.getReturnType();
        Type methodType = createType(methodClassType);

        MethodDeclaration method = new MethodDeclaration(EnumSet.noneOf(Modifier.class), methodType, methodModel.getName());
        method.setBody(null);

        // Parameter
        for (ClassType parameterType : methodModel.getParameterTypes()) {
            Parameter parameter = createParameter(parameterType);
            method.addParameter(parameter);
        }
        return method;
    }

    // Ast:Type -> Model:ClassType
    public static ClassType toClassType(Type type, FullNameProvider fullNameProvider) {
        //
        if (type.isPrimitiveType()) {
            String primitiveName = type.asString();
            return ClassType.newPrimitiveType(primitiveName);
        } else if (type.isVoidType()) {
            return null;
        }

        String returnTypeName = ((ClassOrInterfaceType)type).getName().asString();
        String returnTypeFullName = fullNameProvider.findFullName(returnTypeName);
        ClassType classType = ClassType.newClassType(returnTypeFullName);

        // if return type has Generic type (List<SampleDTO>)
        ifPresentTypeArgument(type, fullNameProvider, typeArgument -> classType.setTypeArgument(typeArgument));
        /* old code
        ClassType typeArgument = toTypeArgumentIfPresent(type, fullNameProvider);
        classType.setTypeArgument(typeArgument);
        */

        return classType;
    }

    // Model:ClassType -> Ast:Type
    public static Type createType(ClassType classType) {
        //
        if (classType == null) {
            return new VoidType();
        }

        if (classType.isPrimitive()) {
            return new PrimitiveType(PrimitiveType.Primitive.valueOf(classType.getName().toUpperCase()));
        }

        return createClassOrInterfaceType(classType);
    }

    // Model:ClassType -> Ast:Parameter
    public static Parameter createParameter(ClassType classType) {
        //
        Type type = createType(classType);
        Parameter parameter = new Parameter(type, classType.getRecommendedVariableName());
        return parameter;
    }

    private static ClassOrInterfaceType createClassOrInterfaceType(ClassType classType) {
        //
        ClassOrInterfaceType returnType = JavaParser.parseClassOrInterfaceType(classType.getName());

        if (classType.hasTypeArgument()) {
            ClassType modelArgumentType = classType.getTypeArgument();
            NodeList<Type> typeArguments = new NodeList<>();
            typeArguments.add(JavaParser.parseClassOrInterfaceType(modelArgumentType.getName()));
            returnType.setTypeArguments(typeArguments);
        }
        return returnType;
    }

    // before
    private static ClassType toTypeArgumentIfPresent(Type type, FullNameProvider fullNameProvider) {
        //
        if (type.isPrimitiveType()) {
            return null;
        }

        Optional<NodeList<Type>> returnTypeArguments = ((ClassOrInterfaceType) type).getTypeArguments();
        if (!returnTypeArguments.isPresent()) {
            return null;
        }
        String returnTypeArgumentName = returnTypeArguments.get().get(0).asString();
        String returnTypeArgumentFullName = fullNameProvider.findFullName(returnTypeArgumentName);
        return ClassType.newClassType(returnTypeArgumentFullName);
    }

    // using map
    private static ClassType toTypeArgument(Type type, FullNameProvider fullNameProvider) {
        //
        if (!type.isClassOrInterfaceType()) {
            return null;
        }

        Optional<NodeList<Type>> returnTypeArguments = ((ClassOrInterfaceType) type).getTypeArguments();

        ClassType classType = returnTypeArguments
                .map(nodeList -> {
                    String returnTypeArgumentName = nodeList.get(0).asString();
                    String returnTypeArgumentFullName = fullNameProvider.findFullName(returnTypeArgumentName);
                    return ClassType.newClassType(returnTypeArgumentFullName);
                })
                .orElse(null);
        return classType;
    }

    // after
    private static void ifPresentTypeArgument(Type type, FullNameProvider fullNameProvider, Consumer<ClassType> classTypeConsumer) {
        //
        if (!type.isClassOrInterfaceType()) {
            return;
        }

        Optional<NodeList<Type>> returnTypeArguments = ((ClassOrInterfaceType)type).getTypeArguments();
        returnTypeArguments.ifPresent(nodeList -> {
            String returnTypeArgumentName = nodeList.get(0).asString();
            String returnTypeArgumentFullName = fullNameProvider.findFullName(returnTypeArgumentName);
            classTypeConsumer.accept(ClassType.newClassType(returnTypeArgumentFullName));
        });
    }

    public interface FullNameProvider {
        //
        String findFullName(String simpleName);
    }
}

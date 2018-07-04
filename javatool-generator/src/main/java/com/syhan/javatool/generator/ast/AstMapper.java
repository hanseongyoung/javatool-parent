package com.syhan.javatool.generator.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.*;
import com.syhan.javatool.generator.model.ClassType;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
import com.syhan.javatool.generator.model.ParameterModel;
import com.syhan.javatool.share.util.string.StringUtil;

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

        // Comment
        javaModel.setNodeComment(compilationUnit.getComment().orElse(null));
        javaModel.setTypeComment(classType.getComment().orElse(null));

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
            if (StringUtil.isNotEmpty(className) && className.indexOf(".") > 0) {
                compilationUnit.addImport(className);
            }
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

        // Type Comment
        classType.setComment(javaModel.getTypeComment());

        compilationUnit.addType(classType);

        // Node Comment
        compilationUnit.setComment(javaModel.getNodeComment());

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
            // FIXME parameterName check
            String parameterName = parameter.getNameAsString();
            methodModel.addParameterModel(new ParameterModel(parameterType, parameterName));
        }

        // throws
        for (ReferenceType thrown : method.getThrownExceptions()) {
            String typeName = ((ClassOrInterfaceType)thrown).getName().asString();
            ClassType thrownClassType = ClassType.newClassType(typeName);
            methodModel.addThrown(thrownClassType);
        }

        // Comment
        methodModel.setComment(method.getComment().orElse(null));

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
        for (ParameterModel parameterModel : methodModel.getParameterModels()) {
            Parameter parameter = createParameter(parameterModel);
            method.addParameter(parameter);
        }

        // throws
        for (ClassType thrown : methodModel.getThrowns()) {
            method.addThrownException(createClassOrInterfaceType(thrown));
        }

        // Comment
        method.setComment(methodModel.getComment());
        return method;
    }

    // Ast:Type -> Model:ClassType
    public static ClassType toClassType(Type type, FullNameProvider fullNameProvider) {
        //
        if (type.isPrimitiveType()) {
            String primitiveName = type.asString();
            return ClassType.newPrimitiveType(primitiveName);
        } else if (type.isArrayType()) {
            ArrayType arrayType = (ArrayType) type;
            Type componentType = arrayType.getComponentType();
            ClassType compClassType = toClassType(componentType, fullNameProvider);
            return ClassType.newArrayType(compClassType);
        } else if (type.isVoidType()) {
            return null;
        }

        String returnTypeName = ((ClassOrInterfaceType)type).getName().asString();
        String returnTypeFullName = fullNameProvider.findFullName(returnTypeName);
        ClassType classType = ClassType.newClassType(returnTypeFullName);

        // if return type has Generic type (List<SampleDTO>)
        ifPresentTypeArgument(type, fullNameProvider, typeArgument -> classType.addTypeArgument(typeArgument));

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

        if (classType.isArray()) {
            ClassType elementType = ClassType.newArrayElementType(classType);
            Type componentType = createType(elementType);
            return new ArrayType(componentType);
        }

        return createClassOrInterfaceType(classType);
    }

    // Model:ParameterModel -> Ast:Parameter
    public static Parameter createParameter(ParameterModel parameterModel) {
        //
        Type type = createType(parameterModel.getType());
        Parameter parameter = new Parameter(type, parameterModel.getVarName());
        return parameter;
    }

    private static ClassOrInterfaceType createClassOrInterfaceType(ClassType classType) {
        //
        ClassOrInterfaceType returnType = JavaParser.parseClassOrInterfaceType(classType.getName());

        if (classType.hasTypeArgument()) {
            NodeList<Type> typeArguments = new NodeList<>();
            for (ClassType modelArgumentType : classType.getTypeArguments()) {
                typeArguments.add(JavaParser.parseClassOrInterfaceType(modelArgumentType.getName()));
            }
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
            for (Type typeArg : nodeList) {
                String returnTypeArgumentName = typeArg.asString();
                String returnTypeArgumentFullName = fullNameProvider.findFullName(returnTypeArgumentName);
                classTypeConsumer.accept(ClassType.newClassType(returnTypeArgumentFullName));
            }
        });
    }

    public interface FullNameProvider {
        //
        String findFullName(String simpleName);
    }
}

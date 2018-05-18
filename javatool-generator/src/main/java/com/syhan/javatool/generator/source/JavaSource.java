package com.syhan.javatool.generator.source;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.syhan.javatool.generator.ast.AstMapper;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class JavaSource {
    //
    private CompilationUnit compilationUnit;
    private String physicalSourceFile;

    public JavaSource(String physicalSourceFile) throws FileNotFoundException {
        //
        this.compilationUnit = JavaParser.parse(new FileInputStream(physicalSourceFile));
        this.physicalSourceFile = physicalSourceFile;
    }

    public JavaSource(JavaModel model) {
        //
        this.compilationUnit = AstMapper.createCompilationUnit(model);
    }

    public JavaModel toModel() {
        //
        return AstMapper.toJavaModel(compilationUnit, this::findFullName);
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

    public boolean isFromFile() {
        //
        return physicalSourceFile != null;
    }

    public static boolean exists(String physicalSourceFile) {
        //
        File file = new File(physicalSourceFile);
        return file.exists() && !file.isDirectory();
    }

    // com/foo/bar/SampleService.java
    public String getSourceFilePath() {
        //
        String packageName = compilationUnit.getPackageDeclaration().get().getNameAsString();
        String typeName = compilationUnit.getType(0).getNameAsString();
        return packageName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator + typeName + ".java";
    }

    public String getName() {
        //
        return compilationUnit.getType(0).getNameAsString();
    }

    public String getClassName() {
        //
        return getPackageName() + "." + getName();
    }

    public void setName(String name) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        classType.setName(name);

        for (ConstructorDeclaration constructor : classType.getConstructors()) {
            constructor.setName(name);
        }
    }

    public String getPackageName() {
        //
        PackageDeclaration packageDeclaration = compilationUnit.getPackageDeclaration().get();
        return packageDeclaration.getNameAsString();
    }

    public void setPackageName(String packageName) {
        //
        PackageDeclaration packageDeclaration = compilationUnit.getPackageDeclaration().get();
        packageDeclaration.setName(new Name(packageName));
    }

    public void setImplementedType(String name, String packageName) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        NodeList<ClassOrInterfaceType> nodeList = new NodeList<>();
        nodeList.add(JavaParser.parseClassOrInterfaceType(name));
        classType.setImplementedTypes(nodeList);

        compilationUnit.addImport(packageName + "." + name);
    }

    public void setExtendedType(String name, String packageName) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        NodeList<ClassOrInterfaceType> nodeList = new NodeList<>();
        nodeList.add(JavaParser.parseClassOrInterfaceType(name));

        classType.setExtendedTypes(nodeList);

        compilationUnit.addImport(packageName + "." + name);
    }

    public void addAnnotation(String annotation, String packageName) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        classType.addMarkerAnnotation(annotation);

        compilationUnit.addImport(packageName + "." + annotation);
    }

    public void removeGetterAndSetter() {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        for (FieldDeclaration fieldDeclaration : classType.getFields()) {
            VariableDeclarator variableDeclarator = fieldDeclaration.getVariable(0);
            String varName = variableDeclarator.getNameAsString();
            MethodDeclaration getter = findGetter(varName);
            if (getter != null) {
                getter.remove();
            }
            MethodDeclaration setter = findSetter(varName);
            if (setter != null) {
                setter.remove();
            }
        }
    }

    public void removeMethod(String methodName) {
        //
        MethodDeclaration method = findMethod(methodName);
        if (method != null) {
            method.remove();
        }
    }

    private MethodDeclaration findGetter(String varName) {
        //
        String getterName = "get" + Character.toUpperCase(varName.charAt(0)) + varName.substring(1);
        return findMethod(getterName);
    }

    private MethodDeclaration findSetter(String varName) {
        //
        String getterName = "set" + Character.toUpperCase(varName.charAt(0)) + varName.substring(1);
        return findMethod(getterName);
    }

    private MethodDeclaration findMethod(String methodName) {
        //
        List<MethodDeclaration> methodDeclarations = compilationUnit.getType(0).getMethods();

        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodDeclaration.getNameAsString().equals(methodName)) {
                return methodDeclaration;
            }
        }
        return null;
    }

    public void changeName(NameRule nameRule) {
        //
        if (nameRule == null) {
            return;
        }
        String name = getName();
        String newName = nameRule.changeName(name);
        setName(newName);
    }

    public void changePackage(PackageRule packageRule) {
        //
        if (packageRule == null) {
            return;
        }

        String packageName = getPackageName();
        String name = getName();
        String newPackageName = packageRule.changePackage(packageName, name);
        setPackageName(newPackageName);
    }

    public void removeNoArgsConstructor() {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        classType.getDefaultConstructor().ifPresent(Node::remove);
    }

    public void removeImports(PackageRule packageRule) {
        //
        if (!packageRule.hasRemoveImports()) {
            return;
        }

        List<ImportDeclaration> removeList = compilationUnit.getImports().stream()
                .filter(importDeclaration -> packageRule.containsRemoveImport(importDeclaration.getNameAsString()))
                .collect(Collectors.toList());

        /* check legacy code
        List<ImportDeclaration> removeList = new ArrayList<>();
        for (ImportDeclaration importDeclaration : compilationUnit.getImports()) {
            String importName = importDeclaration.getNameAsString();
            if (removeImports.contains(importName)) {
                removeList.add(importDeclaration);
            }
        } */

        for (ImportDeclaration toRemove : removeList) {
            compilationUnit.getImports().remove(toRemove);
        }
    }

    public void changeImports(NameRule nameRule, PackageRule packageRule) {
        //
        if (nameRule == null && packageRule == null) {
            return;
        }

        for (ImportDeclaration importDeclaration : compilationUnit.getImports()) {
            String importName = importDeclaration.getNameAsString();

            String wholeChangeImportName = findWholeChangeImportName(importName, packageRule);
            if (wholeChangeImportName != null) {
                importDeclaration.setName(wholeChangeImportName);
            } else {
                importDeclaration.setName(changeImportName(importName, nameRule, packageRule));
            }
        }
    }

    private String changeImportName(String importName, NameRule nameRule, PackageRule packageRule) {
        //
        String newImportName = importName;
        if (nameRule != null) {
            newImportName = nameRule.changeName(newImportName);
        }
        if (packageRule != null) {
            newImportName = packageRule.changePackage(newImportName);
        }
        return newImportName;
    }

    private String findWholeChangeImportName(String importName, PackageRule packageRule) {
        //
        if (packageRule == null) {
            return null;
        }
        return packageRule.findWholeChangeImportName(importName);
    }

    public void changeMethodUsingClassName(NameRule nameRule) {
        //
        if (nameRule == null) {
            return;
        }

        List<MethodDeclaration> methodDeclarations = compilationUnit.getType(0).getMethods();

        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            Type returnType = methodDeclaration.getType();
            changeTypeName(returnType, nameRule);

            for (Parameter parameter : methodDeclaration.getParameters()) {
                Type parameterType = parameter.getType();
                changeTypeName(parameterType, nameRule);
            }
        }
    }

    private void changeTypeName(Type type, NameRule nameRule) {
        //
        if (nameRule == null) {
            return;
        }
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType classOrInterfaceType = ((ClassOrInterfaceType) type);
            String name = classOrInterfaceType.getNameAsString();
            name = nameRule.changeName(name);
            classOrInterfaceType.setName(name);
        }

        // change type arguments name
        if (type.isClassOrInterfaceType()) {
            Optional<NodeList<Type>> typeArguments = ((ClassOrInterfaceType) type).getTypeArguments();
            typeArguments.ifPresent(types -> changeTypeArgumentsName(types, nameRule));
        }
    }

    private void changeTypeArgumentsName(NodeList<Type> types, NameRule nameRule) {
        //
        Type typeArgsType = types.get(0);
        if (typeArgsType.isClassOrInterfaceType()) {
            String typeArgumentName = typeArgsType.asString();
            typeArgumentName = nameRule.changeName(typeArgumentName);
            ClassOrInterfaceType classOrInterfaceType = ((ClassOrInterfaceType) typeArgsType);
            System.out.println("change type arguments name -> " + typeArgumentName);
            classOrInterfaceType.setName(typeArgumentName);
        }
    }

    public void write(String physicalTargetFilePath) throws IOException {
        //
        File file = new File(physicalTargetFilePath);
        // TODO : using Logger
        //System.out.println(compilationUnit.toString());
        FileUtils.writeStringToFile(file, compilationUnit.toString(), "UTF-8");
    }

    public static void main(String[] args) {
        // In the case of Windows replaceAll with File.separator causes 'character to be escaped is missing' error.
        String packageName = "com.foo.bar";
        //System.out.println(packageName.replaceAll("\\.", File.separator));
        System.out.println(packageName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
    }
}

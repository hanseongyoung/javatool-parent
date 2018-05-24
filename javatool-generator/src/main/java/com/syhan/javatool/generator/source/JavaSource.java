package com.syhan.javatool.generator.source;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.syhan.javatool.generator.ast.AstMapper;
import com.syhan.javatool.generator.model.ClassType;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
import com.syhan.javatool.share.data.Pair;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.file.PathUtil;
import com.syhan.javatool.share.util.string.StringUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
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

    public ClassType toClassType() {
        //
        return ClassType.newClassType(getName(), getPackageName());
    }

    private String findFullName(String returnTypeName) {
        //
        for (Object obj : compilationUnit.getImports()) {
            ImportDeclaration importDeclaration = (ImportDeclaration) obj;
            String packageName = importDeclaration.getName().asString();
            if (packageName.endsWith("." + returnTypeName)) {
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
        //String packageName = compilationUnit.getPackageDeclaration().get().getNameAsString();
        String packageName = compilationUnit.getPackageDeclaration()
                .map(NodeWithName::getNameAsString)
                .orElseThrow(IllegalArgumentException::new);
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
        return compilationUnit.getPackageDeclaration()
                .map(NodeWithName::getNameAsString)
                .orElse(null);
        //PackageDeclaration packageDeclaration = compilationUnit.getPackageDeclaration().orElse(null);
        //return packageDeclaration.getNameAsString();
    }

    public void setPackageName(String packageName) {
        //
        compilationUnit.getPackageDeclaration()
                .ifPresent(pd -> pd.setName(new Name(packageName)));
    }

    public void setImplementedType(JavaSource implementedType) {
        //
        setImplementedType(implementedType.getName(), implementedType.getPackageName());
    }

    public void setImplementedType(String name, String packageName) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        NodeList<ClassOrInterfaceType> nodeList = new NodeList<>();
        nodeList.add(JavaParser.parseClassOrInterfaceType(name));
        classType.setImplementedTypes(nodeList);

        compilationUnit.addImport(packageName + "." + name);
    }

    public void setExtendedType(JavaSource extendedType) {
        //
        setExtendedType(extendedType.getName(), extendedType.getPackageName());
    }

    public void setExtendedType(String name, String packageName) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        NodeList<ClassOrInterfaceType> nodeList = new NodeList<>();
        nodeList.add(JavaParser.parseClassOrInterfaceType(name));

        classType.setExtendedTypes(nodeList);

        compilationUnit.addImport(packageName + "." + name);
    }

    public void addField(JavaSource fieldType, String varName, ClassType annotation) {
        //
        addField(fieldType.getName(), fieldType.getPackageName(), varName, annotation.getName(), annotation.getPackageName());
    }

    public void addField(String name, String packageName, String varName, String annotationName, String annotationPackageName) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        FieldDeclaration field = classType.addPrivateField(name, varName);
        compilationUnit.addImport(packageName + "." + name);

        if (annotationName != null) {
            field.addMarkerAnnotation(annotationName);
            compilationUnit.addImport(annotationPackageName + "." + annotationName);
        }
    }

    public void addAnnotation(ClassType classType) {
        //
        addAnnotation(classType.getName(), classType.getPackageName());
    }

    public void addAnnotation(String annotation, String packageName) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        classType.addMarkerAnnotation(annotation);

        compilationUnit.addImport(packageName + "." + annotation);
    }

    public void addAnnotation(ClassType classType, String annotationArgs) {
        //
        addAnnotation(classType.getName(), classType.getPackageName(), annotationArgs);
    }

    public void addAnnotation(String annotation, String packageName, String annotationArgs) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        AnnotationExpr expr = new SingleMemberAnnotationExpr(new Name(annotation), new StringLiteralExpr(annotationArgs));
        classType.addAnnotation(expr);

        compilationUnit.addImport(packageName + "." + annotation);
    }

    public void addAnnotation(ClassType annotation, List<Pair<String, Object>> annotationArgs) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        NodeList<MemberValuePair> pairs = new NodeList<>();
        for (Pair<String, Object> pair : annotationArgs) {
            Expression expression;
            if (pair.y instanceof String) {
                expression = new StringLiteralExpr(pair.y.toString());
            } else if (pair.y instanceof Boolean) {
                expression = new BooleanLiteralExpr((Boolean) pair.y);
            } else {
                expression = new NameExpr(pair.y.toString());
            }
            pairs.add(new MemberValuePair(pair.x, expression));
        }
        AnnotationExpr expr = new NormalAnnotationExpr(new Name(annotation.getName()), pairs);
        classType.addAnnotation(expr);

        compilationUnit.addImport(annotation.getClassName());
    }

    public void addImport(String name, String packageName) {
        //
        compilationUnit.addImport(packageName + "." + name);
    }

    public void addImport(ClassType classType) {
        //
        compilationUnit.addImport(classType.getClassName());
    }

    public MethodDeclaration addMethod(MethodModel methodModel) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);

        MethodDeclaration methodDeclaration = AstMapper.createMethodDeclaration(methodModel);
        classType.addMember(methodDeclaration);

        addImport(methodModel.getReturnType());

        return methodDeclaration;
    }

    public void addMethod(MethodModel methodModel, Consumer<MethodDeclaration> methodConsumer) {
        //
        MethodDeclaration methodDeclaration = addMethod(methodModel);
        methodConsumer.accept(methodDeclaration);
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

    public void forEachMethod(Consumer<MethodDeclaration> methodConsumer) {
        //
        ClassOrInterfaceDeclaration classType = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        List<MethodDeclaration> methodDeclarations = classType.getMethods();

        // for field, method ordering issue.
        for(MethodDeclaration methodDeclaration : methodDeclarations) {
            classType.remove(methodDeclaration);
        }

        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            methodConsumer.accept(methodDeclaration);
            classType.addMember(methodDeclaration);
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

    public void changePackageAndName(NameRule nameRule, PackageRule packageRule) {
        //
        String thisClassName = getClassName();
        String changedClassName = packageRule.findWholeChangeImportName(thisClassName);
        if (changedClassName != null) {
            Pair<String, String> packageAndName = PathUtil.devideClassName(changedClassName);
            setPackageName(packageAndName.x);
            setName(packageAndName.y);
        } else {
            // ! order sensitive. change name first!
            changeName(nameRule);
            changePackage(packageRule);
        }
    }

    private void changeName(NameRule nameRule) {
        //
        if (nameRule == null) {
            return;
        }
        String name = getName();
        String newName = nameRule.changeName(name);
        setName(newName);
    }

    private void changePackage(PackageRule packageRule) {
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

            String wholeChangeImportName = packageRule.findWholeChangeImportName(importName);
            if (wholeChangeImportName != null) {
                importDeclaration.setName(wholeChangeImportName);
            } else {
                String changed = changeImportName(importName, nameRule, packageRule);
                if (StringUtil.isNotEmpty(changed) && changed.indexOf(".") > 0) {
                    importDeclaration.setName(changed);
                }
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
            Pair<String, String> pair = PathUtil.devideClassName(newImportName);
            newImportName = packageRule.changePackage(pair.x, pair.y) + "." + pair.y;
        }
        return newImportName;
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

    @Override
    public String toString() {
        //
        return this.compilationUnit.toString();
    }

    public static void main(String[] args) {
        // In the case of Windows replaceAll with File.separator causes 'character to be escaped is missing' error.
        String packageName = "com.foo.bar";
        //System.out.println(packageName.replaceAll("\\.", File.separator));
        System.out.println(packageName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
    }
}

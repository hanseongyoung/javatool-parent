package com.syhan.javatool.generator.converter;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.VoidType;
import com.syhan.javatool.generator.checker.JavaSourceChecker;
import com.syhan.javatool.generator.model.ClassType;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
import com.syhan.javatool.generator.reader.JavaReader;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.data.Pair;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.file.PathUtil;
import com.syhan.javatool.share.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// com.foo.bar.SomeService
// -->
// [Interface]com.foo.bar.spec.SomeService
// [Class]    com.foo.bar.logic.SomeLogic
// [DTO]      com.foo.bar.spec.sdo.SomeDTO
public class JavaInterfaceAbstracter extends ProjectItemConverter {
    //
    private static final Logger logger = LoggerFactory.getLogger(JavaInterfaceAbstracter.class);

    private static final ClassType Autowired = ClassType.newClassType("org.springframework.beans.factory.annotation.Autowired");
    private static final ClassType FeignClient = ClassType.newClassType("org.springframework.cloud.openfeign.FeignClient");
    private static final ClassType RestController = ClassType.newClassType("org.springframework.web.bind.annotation.RestController");
    private static final ClassType RequestBody = ClassType.newClassType("org.springframework.web.bind.annotation.RequestBody");
    private static final ClassType PostMapping = ClassType.newClassType("org.springframework.web.bind.annotation.PostMapping");
    private static final ClassType Component = ClassType.newClassType("org.springframework.stereotype.Component");
    private static final ClassType ConditionalOnProperty = ClassType.newClassType("org.springframework.boot.autoconfigure.condition.ConditionalOnProperty");

    private JavaReader javaReader;
    private JavaReader javaReaderForStub;
    private JavaWriter javaWriterForStub;
    private JavaReader javaReaderForSkeleton;
    private JavaWriter javaWriterForSkeleton;

    private NameRule nameRule;
    private PackageRule packageRule;
    private JavaAbstractParam javaAbstractParam;

    private JavaSourceChecker javaSourceChecker;

    public JavaInterfaceAbstracter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetStubConfiguration,
                                   ProjectConfiguration targetSkeletonConfiguration, NameRule nameRule, PackageRule packageRule,
                                   JavaAbstractParam javaAbstractParam) {
        //
        super(sourceConfiguration, ProjectItemType.Java, javaAbstractParam.getTargetFilePostfix());

        this.javaReader = new JavaReader(sourceConfiguration);
        this.javaReaderForStub = new JavaReader(targetStubConfiguration);
        this.javaWriterForStub = new JavaWriter(targetStubConfiguration);
        this.javaReaderForSkeleton = new JavaReader(targetSkeletonConfiguration);
        this.javaWriterForSkeleton = new JavaWriter(targetSkeletonConfiguration);

        this.nameRule = nameRule;
        this.packageRule = packageRule;
        this.javaAbstractParam = javaAbstractParam;
    }

    public List<PackageRule.ChangeImport> findUsingDtoChangeInfo(String sourceFileName, PackageRule packageRuleForCheckStubDto) throws IOException {
        //
        JavaSource source = javaReader.read(sourceFileName);
        JavaModel interfaceModel = changeToJavaInterfaceModel(source);
        List<PackageRule.ChangeImport> stubDtoInfo = interfaceModel.computeMethodUsingClasses()
                .stream()
                .filter(dtoClassName -> dtoClassName.startsWith(javaAbstractParam.getSourceDtoPackage()))
                .map(dtoClassName -> {
                    String newName = packageRuleForCheckStubDto.changePackage(dtoClassName);
                    newName = nameRule.changeName(newName);
                    logger.info("{} --> {}", dtoClassName, newName);
                    return new PackageRule.ChangeImport(dtoClassName, newName);
                })
                .collect(Collectors.toList());
        return stubDtoInfo;
    }

    public void convert(String sourceFileName) throws IOException {
        //
        JavaSource source = javaReader.read(sourceFileName);

        // check java source
        if (javaSourceChecker != null) {
            javaSourceChecker.checkAndWarn(source);
        }

        // write interface
        JavaModel interfaceModel = changeToJavaInterfaceModel(source);
        javaWriterForStub.write(updateInterface(interfaceModel));

        // write adapter
        JavaSource adapterSource = createAdapterSource(interfaceModel);
        javaWriterForStub.write(adapterSource);

        // write logic
        JavaSource logicSource = changeToJavaLogic(source, interfaceModel);
        javaWriterForSkeleton.write(logicSource);

        // write resource
        JavaSource resourceJava = createResourceJava(interfaceModel, logicSource);
        javaWriterForSkeleton.write(resourceJava);

        // update proxy interface
        JavaSource proxySource = readAndUpdateProxy(interfaceModel);
        javaWriterForStub.write(proxySource);

        // update remote proxy
        JavaSource remoteProxy = readAndUpdateRemoteProxy(interfaceModel, proxySource, adapterSource);
        javaWriterForStub.write(remoteProxy);

        // update local proxy
        JavaSource localProxy = readAndUpdateLocalProxy(interfaceModel, proxySource, logicSource);
        javaWriterForSkeleton.write(localProxy);
    }

    private JavaModel changeToJavaInterfaceModel(JavaSource source) {
        //
        JavaModel javaModel = source.toModel();
        javaModel.setInterface(true);

        // remove not 'public' access method
        List<MethodModel> onlyPublic = javaModel.getMethods().stream()
                .filter(MethodModel::isPublic)
                .collect(Collectors.toList());
        javaModel.setMethods(onlyPublic);

        // Do not change package or name in this method. findUsingDtoChangeInfo using it.

        return javaModel;
    }

    private JavaSource updateInterface(JavaModel interfaceModel) {
        //
        interfaceModel.changePackage(packageRule);
        interfaceModel.changeMethodUsingClassPackageName(nameRule, packageRule);
        JavaSource javaSource = new JavaSource(interfaceModel);

        javaSource.forEachMethod(methodDeclaration -> interfaceMethodHandle(methodDeclaration, interfaceModel.getName()));

        // add import
        javaSource.addImport(RequestBody);
        javaSource.addImport(PostMapping);

        return javaSource;
    }

    private void interfaceMethodHandle(MethodDeclaration methodDeclaration, String interfaceName) {
        //
        String mappingUrl = interfaceName + "/" + methodDeclaration.getNameAsString();
        AnnotationExpr expr = new SingleMemberAnnotationExpr(new Name(PostMapping.getName()), new StringLiteralExpr(mappingUrl));
        methodDeclaration.addAnnotation(expr);

        for (Parameter parameter : methodDeclaration.getParameters()) {
            parameter.addMarkerAnnotation(RequestBody.getName());
        }
    }

    private JavaSource readAndUpdateProxy(JavaModel interfaceModel) throws IOException {
        //
        String projectNameFirstUpper = StringUtil.toFirstUpperCase(javaAbstractParam.getNewProjectName2());
        String proxyClassName = packageRule.changePackage(javaAbstractParam.getSourcePackage())
                + ".ext.proxy." + projectNameFirstUpper + "Proxy";
        String proxySourceFilePath = PathUtil.toSourceFileName(proxyClassName, "java");

        JavaSource proxyJavaSource;
        if (javaReaderForStub.exists(proxySourceFilePath)) {
            proxyJavaSource = javaReaderForStub.read(proxySourceFilePath);
        } else {
            JavaModel proxyModel = new JavaModel(proxyClassName, true);
            proxyJavaSource = new JavaSource(proxyModel);
        }

        MethodModel requireMethodModel = new MethodModel("require" + interfaceModel.getName(), interfaceModel.getClassType());
        proxyJavaSource.addMethod(requireMethodModel);

        return proxyJavaSource;
    }

    private JavaSource readAndUpdateLocalProxy(JavaModel interfaceModel, JavaSource proxySource, JavaSource logicSource) throws IOException {
        //
        String projectNameFirstUpper = StringUtil.toFirstUpperCase(javaAbstractParam.getNewProjectName2());
        String localProxyClassName = packageRule.changePackage(javaAbstractParam.getSourcePackage())
                + ".ext.logic." + projectNameFirstUpper + "LocalProxy";
        String localProxySourceFilePath = PathUtil.toSourceFileName(localProxyClassName, "java");

        JavaSource localProxySource;
        if (javaReaderForSkeleton.exists(localProxySourceFilePath)) {
            localProxySource = javaReaderForSkeleton.read(localProxySourceFilePath);
        } else {
            JavaModel localProxyModel = new JavaModel(localProxyClassName, false);
            localProxySource = new JavaSource(localProxyModel);
            localProxySource.setImplementedType(proxySource);
            localProxySource.addAnnotation(Component);
            List<Pair<String, Object>> annotationArgs = new ArrayList<>();
            annotationArgs.add(new Pair<>("name", javaAbstractParam.getRemoteIdentifier() + ".proxy"));
            annotationArgs.add(new Pair<>("havingValue", "local"));
            annotationArgs.add(new Pair<>("matchIfMissing", true));
            localProxySource.addAnnotation(ConditionalOnProperty, annotationArgs);
        }

        // add field
        String logicVarName = StringUtil.getRecommendedVariableName(logicSource.getName());
        localProxySource.addField(logicSource, logicVarName, Autowired);

        // add method
        MethodModel requireMethodModel = new MethodModel("require" + interfaceModel.getName(), interfaceModel.getClassType());
        localProxySource.addMethod(requireMethodModel, methodDeclaration -> simpleReturnMethodBodyHandle(methodDeclaration, logicVarName));

        return localProxySource;
    }

    private JavaSource readAndUpdateRemoteProxy(JavaModel interfaceModel, JavaSource proxySource, JavaSource adapterSource) throws IOException {
        //
        String projectNameFirstUpper = StringUtil.toFirstUpperCase(javaAbstractParam.getNewProjectName2());
        String remoteProxyClassName = packageRule.changePackage(javaAbstractParam.getSourcePackage())
                + ".ext.adapter." + projectNameFirstUpper + "RemoteProxy";
        String remoteProxySourceFilePath = PathUtil.toSourceFileName(remoteProxyClassName, "java");

        JavaSource remoteProxySource;
        if (javaReaderForStub.exists(remoteProxySourceFilePath)) {
            remoteProxySource = javaReaderForStub.read(remoteProxySourceFilePath);
        } else {
            JavaModel remoteProxyModel = new JavaModel(remoteProxyClassName, false);
            remoteProxySource = new JavaSource(remoteProxyModel);
            remoteProxySource.setImplementedType(proxySource);
            remoteProxySource.addAnnotation(Component);
            List<Pair<String, Object>> annotationArgs = new ArrayList<>();
            annotationArgs.add(new Pair<>("name", javaAbstractParam.getRemoteIdentifier() + ".proxy"));
            annotationArgs.add(new Pair<>("havingValue", "remote"));
            remoteProxySource.addAnnotation(ConditionalOnProperty, annotationArgs);
        }

        // add field
        String adapterVarName = StringUtil.getRecommendedVariableName(adapterSource.getName());
        remoteProxySource.addField(adapterSource, adapterVarName, Autowired);

        // add method
        MethodModel requireMethodModel = new MethodModel("require" + interfaceModel.getName(), interfaceModel.getClassType());
        remoteProxySource.addMethod(requireMethodModel, methodDeclaration -> simpleReturnMethodBodyHandle(methodDeclaration, adapterVarName));

        return remoteProxySource;
    }

    private void simpleReturnMethodBodyHandle(MethodDeclaration methodDeclaration, String returnStmt) {
        //
        methodDeclaration.addMarkerAnnotation("Override");
        methodDeclaration.addModifier(Modifier.PUBLIC);

        BlockStmt block = new BlockStmt();
        methodDeclaration.setBody(block);
        block.addStatement(new ReturnStmt(returnStmt));
    }

    private JavaSource changeToJavaLogic(JavaSource source, JavaModel interfaceModel) {
        //
        // change package and name
        NameRule logicNameRule = NameRule.copyOf(nameRule)
                .add("Service", "ServiceLogic");
        source.changePackageAndName(logicNameRule, packageRule);

        // change imports
        source.changeImports(nameRule, packageRule);

        // change method using types name(return, parameter type)
        source.changeMethodUsingClassName(nameRule);

        // set implements
        source.setImplementedType(interfaceModel.getName(), interfaceModel.getPackageName());

        return source;
    }

    private JavaSource createAdapterSource(JavaModel interfaceModel) {
        //
        String className = interfaceModel.getClassType().getClassName();
        JavaModel javaModel = new JavaModel(className, true);

        NameRule adapterNameRule = NameRule.copyOf(nameRule)
                .add("Service", "Adapter");
        javaModel.changeName(adapterNameRule);

        String newPackageName = packageRule.changePackage(javaAbstractParam.getSourcePackage()) + ".ext.adapter";
        javaModel.setPackageName(newPackageName);

        JavaSource javaSource = new JavaSource(javaModel);
        String remoteIdentifier = javaAbstractParam.getRemoteIdentifier();
        javaSource.addAnnotation(FeignClient, remoteIdentifier);

        javaSource.setExtendedType(interfaceModel.getName(), interfaceModel.getPackageName());

        return javaSource;
    }

    private JavaSource createResourceJava(JavaModel interfaceModel, JavaSource logicSource) {
        //
        JavaModel javaModel = new JavaModel(interfaceModel);
        javaModel.setInterface(false);

        NameRule resourceNameRule = NameRule.copyOf(nameRule)
                .add("Service", "Resource");
        javaModel.changeName(resourceNameRule);

        String newPackageName = packageRule.changePackage(javaAbstractParam.getSourcePackage()) + ".ext.rest";
        javaModel.setPackageName(newPackageName);

        JavaSource javaSource = new JavaSource(javaModel);
        javaSource.addAnnotation(RestController);
        javaSource.setImplementedType(interfaceModel.getName(), interfaceModel.getPackageName());

        String logicName = logicSource.getName();
        String varName = StringUtil.getRecommendedVariableName(logicName);
        javaSource.addField(logicSource, varName, Autowired);

        // method body
        javaSource.forEachMethod(methodDeclaration -> resourceMethodHandle(methodDeclaration, new NameExpr(varName)));

        // add import
        javaSource.addImport(RequestBody);
        return javaSource;
    }

    private void resourceMethodHandle(MethodDeclaration methodDeclaration, Expression logicExp) {
        //
        methodDeclaration.addMarkerAnnotation("Override");
        methodDeclaration.addModifier(Modifier.PUBLIC);

        BlockStmt block = new BlockStmt();
        methodDeclaration.setBody(block);

        MethodCallExpr call = new MethodCallExpr(logicExp, methodDeclaration.getNameAsString());
        for (Parameter parameter : methodDeclaration.getParameters()) {
            parameter.addMarkerAnnotation(RequestBody.getName());
            call.addArgument(parameter.getNameAsExpression());
        }

        if (methodDeclaration.getType() instanceof VoidType) {
            block.addStatement(call);
        } else {
            block.addStatement(new ReturnStmt(call));
        }
    }

    public JavaInterfaceAbstracter setJavaSourceChecker(JavaSourceChecker javaSourceChecker) {
        //
        this.javaSourceChecker = javaSourceChecker;
        return this;
    }
}

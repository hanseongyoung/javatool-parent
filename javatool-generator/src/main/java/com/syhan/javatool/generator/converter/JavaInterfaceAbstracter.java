package com.syhan.javatool.generator.converter;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.VoidType;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
import com.syhan.javatool.generator.reader.JavaReader;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.string.StringUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// com.foo.bar.SomeService
// -->
// [Interface]com.foo.bar.spec.SomeService
// [Class]    com.foo.bar.logic.SomeLogic
// [DTO]      com.foo.bar.spec.sdo.SomeDTO
public class JavaInterfaceAbstracter extends ProjectItemConverter {
    //
    private JavaReader javaReader;
    private JavaWriter javaWriterForStub;
    private JavaWriter javaWriterForSkeleton;

    private NameRule nameRule;
    private PackageRule packageRule;
    private JavaAbstractParam javaAbstractParam;

    public JavaInterfaceAbstracter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetStubConfiguration,
                                   ProjectConfiguration targetSkeletonConfiguration, NameRule nameRule, PackageRule packageRule,
                                   JavaAbstractParam javaAbstractParam) {
        //
        super(sourceConfiguration, ProjectItemType.Java, javaAbstractParam.getTargetFilePostfix());

        this.javaReader = new JavaReader(sourceConfiguration);
        this.javaWriterForStub = new JavaWriter(targetStubConfiguration);
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
                    System.out.println(dtoClassName + " --> " + newName);
                    return new PackageRule.ChangeImport(dtoClassName, newName);
                })
                .collect(Collectors.toList());
        return stubDtoInfo;
    }

    public void convert(String sourceFileName) throws IOException {
        //
        JavaSource source = javaReader.read(sourceFileName);

        // write interface
        JavaModel interfaceModel = changeToJavaInterfaceModel(source);
        interfaceModel.changePackage(packageRule);
        interfaceModel.changeMethodUsingClassPackageName(nameRule, packageRule);
        javaWriterForStub.write(new JavaSource(interfaceModel));

        // write adapter
        JavaModel adapterModel = createAdapterModel(interfaceModel);
        javaWriterForStub.write(new JavaSource(adapterModel));

        // write logic
        JavaSource logicSource = changeToJavaLogic(source, interfaceModel);
        javaWriterForSkeleton.write(logicSource);

        // write resource
        JavaSource resourceJava = createResourceJava(interfaceModel, logicSource);
        javaWriterForSkeleton.write(resourceJava);
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

    private JavaModel changeToJavaInterfaceModel(JavaSource source) {
        //
        JavaModel javaModel = source.toModel();
        javaModel.setInterface(true);

        // remove not 'public' access method
        List<MethodModel> onlyPublic = javaModel.getMethods().stream()
                .filter(methodModel -> methodModel.isPublic())
                .collect(Collectors.toList());
        javaModel.setMethods(onlyPublic);

        // Do not change package or name in this method. findUsingDtoChangeInfo using it.

        return javaModel;
    }

    private JavaModel createAdapterModel(JavaModel interfaceModel) {
        //
        String className = interfaceModel.getClassType().getClassName();
        JavaModel javaModel = new JavaModel(className, true);

        NameRule adapterNameRule = NameRule.copyOf(nameRule)
                .add("Service", "Adapter");
        javaModel.changeName(adapterNameRule);

        String newPackageName = packageRule.changePackage(javaAbstractParam.getSourcePackage()) + ".ext.adapter";
        javaModel.setPackageName(newPackageName);

        return javaModel;
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
        javaSource.addAnnotation("RestController", "org.springframework.web.bind.annotation");
        javaSource.setImplementedType(interfaceModel.getName(), interfaceModel.getPackageName());

        String logicName = logicSource.getName();
        String logicPackage = logicSource.getPackageName();
        String varName = StringUtil.getRecommendedVariableName(logicName);
        javaSource.addField(logicName, logicPackage, varName,
                "Autowired", "org.springframework.beans.factory.annotation");

        // method body
        javaSource.forEachMethod(methodDeclaration -> resourceMethodHandle(methodDeclaration, new NameExpr(varName)));

        // add import
        javaSource.addImport("RequestBody", "org.springframework.web.bind.annotation");
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
            parameter.addMarkerAnnotation("RequestBody");
            call.addArgument(parameter.getNameAsExpression());
        }

        if (methodDeclaration.getType() instanceof VoidType) {
            block.addStatement(call);
        } else {
            block.addStatement(new ReturnStmt(call));
        }
    }
}

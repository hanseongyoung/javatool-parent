package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
import com.syhan.javatool.generator.reader.JavaReader;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// com.foo.bar.SomeService
// -->
// [Interface]com.foo.bar.spec.SomeService
// [Class]    com.foo.bar.logic.SomeLogic
// [DTO]      com.foo.bar.spec.sdo.SomeDTO
public class JavaInterfaceAbstracter {
    //
    private JavaReader javaReader;
    private JavaWriter javaWriterForInterface;
    private JavaWriter javaWriterForLogic;

    private NameRule nameRule;
    private PackageRule packageRule;
    private JavaAbstractParam javaAbstractParam;

    public JavaInterfaceAbstracter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetInterfaceConfiguration,
                                   ProjectConfiguration targetLogicConfiguration, NameRule nameRule, PackageRule packageRule,
                                   JavaAbstractParam javaAbstractParam) {
        //
        this.javaReader = new JavaReader(sourceConfiguration);
        this.javaWriterForInterface = new JavaWriter(targetInterfaceConfiguration);
        this.javaWriterForLogic = new JavaWriter(targetLogicConfiguration);

        this.nameRule = nameRule;
        this.packageRule = packageRule;
        this.javaAbstractParam = javaAbstractParam;
    }

    public List<PackageRule.ChangeImport> findUsingDtoChangeInfo(String sourceFileName, PackageRule packageRuleForCheckStubDto) throws IOException {
        //
        JavaSource source = javaReader.read(sourceFileName);
        JavaModel interfaceModel = createJavaInterfaceModel(source);
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

        JavaModel interfaceModel = createJavaInterfaceModel(source);

        // write dto
//        List<String> dtoClassNames = interfaceModel.computeMethodUsingClasses()
//                .stream()
//                .filter(s -> s.startsWith(javaAbstractParam.getSourceDtoPackage()))
//                .collect(Collectors.toList());
//
//        for (String dtoClassName : dtoClassNames) {
//            String dtoSourceFileName = PathUtil.toSourceFileName(dtoClassName, "java");
//            dtoConverter.convert(dtoSourceFileName);
//        }

        // write interface
        interfaceModel.changePackage(packageRule);
        interfaceModel.changeMethodUsingClassPackageName(nameRule, packageRule);
        javaWriterForInterface.write(new JavaSource(interfaceModel));

        // write logic
        JavaSource logicSource = changeToJavaLogic(source, interfaceModel);
        javaWriterForLogic.write(logicSource);
    }

    private JavaSource changeToJavaLogic(JavaSource source, JavaModel interfaceModel) {
        //
        // change package and name
        source.changePackageAndName(nameRule, packageRule);

        // change imports
        source.changeImports(nameRule, packageRule);

        // change method using types name(return, parameter type)
        source.changeMethodUsingClassName(nameRule);

        // set implements
        source.setImplementedType(interfaceModel.getName(), interfaceModel.getPackageName());

        return source;
    }

    private JavaModel createJavaInterfaceModel(JavaSource source) {
        //
        JavaModel javaModel = source.toModel();
        javaModel.setInterface(true);

        // remove not 'public' access method
        List<MethodModel> onlyPublic = javaModel.getMethods().stream()
                .filter(methodModel -> methodModel.isPublic())
                .collect(Collectors.toList());
        javaModel.setMethods(onlyPublic);

        return javaModel;
    }
}

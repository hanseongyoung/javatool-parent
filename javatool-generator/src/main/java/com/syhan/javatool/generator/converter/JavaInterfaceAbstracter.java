package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.reader.JavaReader;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.file.PathUtil;

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

    private PackageRule packageRule;
    private JavaAbstractParam javaAbstractParam;

    public JavaInterfaceAbstracter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetInterfaceConfiguration,
                                   ProjectConfiguration targetLogicConfiguration, PackageRule packageRule,
                                   JavaAbstractParam javaAbstractParam) {
        //
        this.javaReader = new JavaReader(sourceConfiguration);
        this.javaWriterForInterface = new JavaWriter(targetInterfaceConfiguration);
        this.javaWriterForLogic = new JavaWriter(targetLogicConfiguration);
        this.packageRule = packageRule;
        this.javaAbstractParam = javaAbstractParam;
    }

    public void convert(String sourceFileName) throws IOException {
        //
        JavaSource source = javaReader.read(sourceFileName);

        JavaModel interfaceModel = createJavaInterfaceModel(source);
        interfaceModel.changePackage(packageRule);

        // write dto
        List<String> dtoClassNames = interfaceModel.computeMethodUsingClasses()
                .stream()
                .filter(s -> s.startsWith(javaAbstractParam.getSourceBasePackage()))
                .collect(Collectors.toList());

        for (String dtoClassName : dtoClassNames) {
            copyDto(dtoClassName);
        }

        // write interface
        interfaceModel.changeMethodUsingClassPackageName(packageRule);
        javaWriterForInterface.write(new JavaSource(interfaceModel));

        // write logic
        JavaSource logicSource = changeToJavaLogic(source, interfaceModel);
        javaWriterForLogic.write(logicSource);
    }

    private void copyDto(String dtoClassName) {
        try {
            String dtoSourceFileName = PathUtil.toSourceFileName(dtoClassName, "java");
            JavaSource dtoSource = javaReader.read(dtoSourceFileName);

            dtoSource.changePackage(packageRule);
            javaWriterForInterface.write(dtoSource);
        } catch (IOException e) {
            // TODO : using Logger
            System.err.println("Can't copy dto --> "+dtoClassName + ", " + e.getMessage());
        }
    }

    private JavaSource changeToJavaLogic(JavaSource source, JavaModel interfaceModel) {
        //
        // change name
        source.changeName(javaAbstractParam.getImplNameFrom(), javaAbstractParam.getImplNameTo());

        // change package
        source.changePackage(packageRule);

        // set implements
        source.setImplementedType(interfaceModel.getName(), interfaceModel.getPackageName());

        // change imports
        source.changeImports(packageRule);

        return source;
    }

    private JavaModel createJavaInterfaceModel(JavaSource source) {
        //
        JavaModel javaModel = source.toModel();
        javaModel.setInterface(true);

        return javaModel;
    }

}

package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.reader.JavaReader;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.util.file.PathUtil;
import com.syhan.javatool.share.util.json.JsonUtil;

import java.io.IOException;
import java.util.List;

// com.foo.bar.SomeService
// -->
// [Interface]com.foo.bar.spec.SomeService
// [Class]    com.foo.bar.logic.SomeLogic
// [DTO]      com.foo.bar.spec.sdo.SomeDTO
public class JavaInterfaceAbstracter extends ProjectItemConverter {
    //
    private JavaReader javaReader;
    private JavaWriter javaWriter;

    public JavaInterfaceAbstracter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration) {
        //
        super(sourceConfiguration, targetConfiguration, ProjectItemType.Java);
        this.javaReader = new JavaReader(sourceConfiguration);
        this.javaWriter = new JavaWriter(targetConfiguration);
    }

    @Override
    public void convert(String sourceFileName) throws IOException {
        //
        JavaSource source = javaReader.read(sourceFileName);

        // write interface
        JavaModel interfaceModel = createJavaInterfaceModel(source);
        javaWriter.write(new JavaSource(interfaceModel));

        // write logic
        JavaSource logicSource = changeToJavaLogic(source, interfaceModel);
        javaWriter.write(logicSource);

        // write dto
        List<String> dtoClassNames = interfaceModel.computeMethodUsingClasses();
        for (String dtoClassName : dtoClassNames) {
            String dtoSourceFileName = PathUtil.toSourceFileName(dtoClassName, "java");
            JavaSource dtoSource = javaReader.read(dtoSourceFileName);
            javaWriter.write(dtoSource);
        }
    }

    private JavaSource changeToJavaLogic(JavaSource source, JavaModel interfaceModel) {
        //
        String packageName = source.getPackageName();
        String newPackageName = PathUtil.changePackage(packageName, 0, new String[]{"logic"});
        source.setPackageName(newPackageName);
        String name = source.getName();
        String newName = PathUtil.changeName(name, "Service", "Logic");
        source.setName(newName);
        source.setImplementedType(interfaceModel.getName(), interfaceModel.getPackageName());
        return source;
    }

    private JavaModel createJavaInterfaceModel(JavaSource source) {
        //
        JavaModel javaModel = source.toModel();
        javaModel.setInterface(true);

        String packageName = javaModel.getPackageName();
        String newPackageName = PathUtil.changePackage(packageName, 0, new String[]{"spec"});
        javaModel.getClassType().setPackageName(newPackageName);

        System.out.println(JsonUtil.toJson(javaModel));
        return javaModel;
    }
}

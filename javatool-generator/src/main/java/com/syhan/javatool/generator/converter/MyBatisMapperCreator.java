package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.model.*;
import com.syhan.javatool.generator.reader.JavaReader;
import com.syhan.javatool.generator.reader.XmlReader;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.source.XmlSource;
import com.syhan.javatool.generator.writer.JavaWriter;
import com.syhan.javatool.generator.writer.XmlWriter;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.file.PathUtil;
import com.syhan.javatool.share.util.string.StringUtil;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.List;

public class MyBatisMapperCreator extends ProjectItemConverter {
    //
    private XmlReader xmlReader;
    private XmlWriter xmlWriter;
    private JavaReader daoReader;
    private JavaWriter javaWriter;

    private NameRule javaNameRule;
    private PackageRule namespacePackageRule;
    private PackageRule javaPackageRule;

    public MyBatisMapperCreator(ProjectConfiguration sourceConfiguration, ProjectConfiguration daoSourceConfiguration,
                                ProjectConfiguration targetConfiguration) {
        //
        this(sourceConfiguration, daoSourceConfiguration, targetConfiguration, null, null, null);
    }

    public MyBatisMapperCreator(ProjectConfiguration sourceConfiguration, ProjectConfiguration daoSourceConfiguration,
                                ProjectConfiguration targetConfiguration, NameRule javaNameRule,
                                PackageRule namespacePackageRule, PackageRule javaPackageRule) {
        //
        super(sourceConfiguration, targetConfiguration, ProjectItemType.MyBatisMapper);

        this.xmlReader = new XmlReader(sourceConfiguration);
        this.daoReader = new JavaReader(daoSourceConfiguration);
        this.javaWriter = new JavaWriter(targetConfiguration);

        this.javaNameRule = javaNameRule;
        this.namespacePackageRule = namespacePackageRule;
        this.javaPackageRule = javaPackageRule;

        // If source and target is different xml would copied.
        if (!sourceConfiguration.getProjectHomePath().equals(targetConfiguration.getProjectHomePath())) {
            this.xmlWriter = new XmlWriter(targetConfiguration);
        }
    }

    @Override
    public void convert(String sourceFilePath) throws IOException {
        //
        if (sourceFilePath.endsWith(".out.xml")) {
            System.err.println("Skip convert '.out.xml' --> " + sourceFilePath);
            return;
        }

        // SqlMap xml read
        XmlSource xmlSource = xmlReader.read(sourceFilePath);
        SqlMapSource sqlMapSource = new SqlMapSource(xmlSource);

        if (xmlWriter != null) {
            sqlMapSource.changeNamespace(namespacePackageRule);
            sqlMapSource.changeInOutType(javaNameRule, javaPackageRule);
            xmlWriter.write(sqlMapSource.getXmlSource());
        }

        JavaModel daoModel = findDaoModel(sqlMapSource.getSourceFilePath());
        JavaSource javaSource = computeMapperInterfaceSource(sqlMapSource, daoModel);
        javaSource.changePackageAndName(javaNameRule, javaPackageRule);
        javaSource.changeImports(javaNameRule, javaPackageRule);
        javaWriter.write(javaSource);
    }

    private JavaModel findDaoModel(String xmlSourceFilePath) throws IOException {
        // xmlSourceFilePath : com/foo/bar/Sample.xml
        // find dao: com/foo/bar/SampleDao.java or com/foo/SampleDao.java

        // find com/foo/bar/SampleDao.java
        String daoFilePath1 = toDaoFilePath(xmlSourceFilePath, 0);
        if (daoReader.exists(daoFilePath1)) {
            return daoReader.read(daoFilePath1).toModel();
        }

        // find com/foo/SampleDao.java
        String daoFilePath2 = toDaoFilePath(xmlSourceFilePath, 1);
        if (daoReader.exists(daoFilePath2)) {
            return daoReader.read(daoFilePath2).toModel();
        }

        return null;
    }

    // com/foo/bar/Sample.xml -> com/foo/bar/SampleDao.java or com/foo/SampleDao.java
    private String toDaoFilePath(String xmlSourceFilePath, int skipFolderCount) {
        // for Windows.
        return PathUtil.changePath(xmlSourceFilePath, skipFolderCount, null, "Dao", "java");
    }

    private JavaSource computeMapperInterfaceSource(SqlMapSource sqlMapSource, JavaModel daoModel) {
        //
        // XmlSource -> JavaModel
        JavaModel myBatisMapperJavaModel = createMyBatisMapperJavaModel(sqlMapSource, daoModel);

        // JavaModel -> JavaSource
        return new JavaSource(myBatisMapperJavaModel);
    }

    private JavaModel createMyBatisMapperJavaModel(SqlMapSource sqlMapSource, JavaModel daoModel) {
        //
        String mapperClassName = sqlMapSource.getNamespace();

        JavaModel javaModel = new JavaModel(mapperClassName, true);
        javaModel.setAnnotation(new AnnotationType("org.apache.ibatis.annotations.Mapper"));
        List<Element> sqlElements = sqlMapSource.findSqlElements();
        for (Element element : sqlElements) {
            String methodName = element.getAttribute("id");
            if (StringUtil.isNotEmpty(methodName)) {
                javaModel.addMethodModel(createMyBatisMapperMethodModel(element, daoModel));
            }
        }

        // Copy dao Comment if exists.
        if (daoModel != null) {
            javaModel.setNodeComment(daoModel.getNodeComment());
            javaModel.setTypeComment(daoModel.getTypeComment());
        }
        return javaModel;
    }

    private MethodModel createMyBatisMapperMethodModel(Element element, JavaModel daoModel) {
        //
        String methodName = element.getAttribute("id");
        String tagName = element.getTagName();
        String parameterClassName = element.getAttribute("parameterType");
        String returnClassName = element.getAttribute("resultType");

        MethodModel daoMethodModel = findDaoMethodModel(daoModel, methodName);
        MethodModel methodModel = new MethodModel(methodName, computeReturnClassType(returnClassName, tagName, daoMethodModel));
        if (StringUtil.isNotEmpty(parameterClassName)) {
            methodModel.addParameterModel(computeParameterModel(parameterClassName, daoMethodModel));
        }
        // Copy daoMethod Comment if exists.
        if (daoMethodModel != null) {
            methodModel.setComment(daoMethodModel.getComment());
        }
        return methodModel;
    }

    private MethodModel findDaoMethodModel(JavaModel daoModel, String methodName) {
        //
        if (daoModel == null) {
            return null;
        }
        return daoModel.findMethodByName(methodName);
    }

    private ClassType computeReturnClassType(String returnClassName, String tagName, MethodModel daoMethodModel) {
        //
        if (daoMethodModel != null) {
            return ClassType.copyOf(daoMethodModel.getReturnType());
        }

        if (StringUtil.isNotEmpty(returnClassName)) {
            ClassType returnType = ClassType.newClassType(returnClassName);
            ClassType defaultListType = ClassType.newClassType("List");
            defaultListType.addTypeArgument(returnType);
            return defaultListType;
        }

        // returnClassName is empty.
        if ("update".equals(tagName) || "insert".equals(tagName) || "delete".equals(tagName)) {
            return ClassType.newPrimitiveType(ClassType.PRIMITIVE_INT);
        }

        return null;
    }

    private ParameterModel computeParameterModel(String parameterClassName, MethodModel daoMethodModel) {
        //
        ClassType parameterType = ClassType.newClassType(parameterClassName);

        String parameterName;
        if (daoMethodModel != null && daoMethodModel.parameterSize() == 1) {
            parameterName = daoMethodModel.getParameterModels().get(0).getVarName();
        } else {
            parameterName = parameterType.getRecommendedVariableName();
        }
        return new ParameterModel(parameterType, parameterName);
    }

}

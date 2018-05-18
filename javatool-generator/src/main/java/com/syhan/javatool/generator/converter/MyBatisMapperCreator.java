package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.model.AnnotationType;
import com.syhan.javatool.generator.model.ClassType;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
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
        javaSource.changePackage(javaPackageRule);
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
            String tagName = element.getTagName();
            String methodName = element.getAttribute("id");
            String parameterClassName = element.getAttribute("parameterType");
            String returnClassName = element.getAttribute("resultType");

            MethodModel daoMethodModel = findDaoMethodModel(daoModel, methodName);
            MethodModel methodModel = new MethodModel(methodName, computeReturnClassType(returnClassName, tagName, daoMethodModel));
            if (StringUtil.isNotEmpty(parameterClassName)) {
                methodModel.addParameterType(ClassType.newClassType(parameterClassName));
            }

            javaModel.addMethodModel(methodModel);
        }
        return javaModel;
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
            return new ClassType(daoMethodModel.getReturnType());
        }

        if (StringUtil.isNotEmpty(returnClassName)) {
            return ClassType.newClassType(returnClassName);
        }

        // returnClassName is empty.
        if ("update".equals(tagName) || "insert".equals(tagName) || "delete".equals(tagName)) {
            return ClassType.newPrimitiveType(ClassType.PRIMITIVE_INT);
        }

        return null;
    }

}

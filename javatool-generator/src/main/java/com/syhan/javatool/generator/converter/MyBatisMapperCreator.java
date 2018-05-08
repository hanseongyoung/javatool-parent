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
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.config.ProjectSources;
import com.syhan.javatool.share.util.string.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyBatisMapperCreator extends ProjectItemConverter {
    //
    private XmlReader xmlReader;
    private JavaReader javaReader;
    private JavaWriter javaWriter;

    public MyBatisMapperCreator(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration) {
        //
        super(sourceConfiguration, targetConfiguration, ProjectItemType.MyBatisMapper);
        this.xmlReader = new XmlReader(sourceConfiguration);
        this.javaReader = new JavaReader(sourceConfiguration);
        this.javaWriter = new JavaWriter(targetConfiguration);
    }

    @Override
    public void convert(String sourceFilePath) throws IOException {
        //
        XmlSource xmlSource = xmlReader.read(sourceFilePath);
        JavaModel daoModel = findDaoModel(sourceFilePath);

        JavaSource javaSource = computeMapperInterfaceSource(xmlSource, daoModel);
        javaWriter.write(javaSource);
    }

    private JavaModel findDaoModel(String xmlSourceFilePath) throws IOException {
        // xmlSourceFilePath : com/foo/bar/Sample.xml
        // find dao: com/foo/bar/SampleDao.java or com/foo/SampleDao.java

        // find com/foo/bar/SampleDao.java
        String daoFilePath1 = toDaoFilePath(xmlSourceFilePath, 0);
        if (javaReader.exists(daoFilePath1)) {
            return javaReader.read(daoFilePath1).toModel();
        }

        // find com/foo/SampleDao.java
        String daoFilePath2 = toDaoFilePath(xmlSourceFilePath, 1);
        if (javaReader.exists(daoFilePath2)) {
            return javaReader.read(daoFilePath2).toModel();
        }

        return null;
    }

    private String toDaoFilePath(String xmlSourceFilePath, int skipPackageCount) {
        //
        String[] paths = xmlSourceFilePath.split(ProjectSources.PATH_DELIM);
        int length = paths.length;

        String daoFilePath = "";
        for (int i = 0; i < length - 1 - skipPackageCount; i++) {
            daoFilePath += paths[i];
            daoFilePath += ProjectSources.PATH_DELIM;
        }
        daoFilePath += toJava(paths[length - 1]);

        return daoFilePath;
    }

    private String toJava(String fileName) {
        return fileName.replaceAll("\\.xml", "Dao.java");
    }

    private JavaSource computeMapperInterfaceSource(XmlSource xmlSource, JavaModel daoModel) {
        //
        // XmlSource -> JavaModel
        JavaModel myBatisMapperJavaModel = createMyBatisMapperJavaModel(xmlSource, daoModel);

        // JavaModel -> JavaSource
        return new JavaSource(myBatisMapperJavaModel);
    }

    private JavaModel createMyBatisMapperJavaModel(XmlSource xmlSource, JavaModel daoModel) {
        //
        String mapperClassName = findMapperNamespace(xmlSource);

        JavaModel javaModel = new JavaModel(mapperClassName, true);
        javaModel.setAnnotation(new AnnotationType("org.apache.ibatis.annotations.Mapper"));
        List<Element> sqlElements = findSqlElements(xmlSource);
        for (Element element : sqlElements) {
            String tagName = element.getTagName();
            String methodName = element.getAttribute("id");
            String parameterClassName = element.getAttribute("parameterType");
            String returnClassName = element.getAttribute("resultType");

            MethodModel daoMethodModel = findDaoMethodModel(daoModel, methodName);
            MethodModel methodModel = new MethodModel(methodName, computeReturnClassType(returnClassName, tagName, daoMethodModel));
            if (StringUtil.isNotEmpty(parameterClassName)) {
                methodModel.addParameterType(new ClassType(parameterClassName));
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
//        System.out.println("methodName:"+methodName);
//        CompilationUnit cu = daoSource.getCompilationUnit();
//        TypeDeclaration typeDeclaration = cu.getType(0);
//        for (Object member : typeDeclaration.getMembers()) {
//            if (member instanceof MethodDeclaration) {
//                MethodDeclaration method = (MethodDeclaration) member;
//                if (methodName.equals(method.getNameAsString())) {
//                    ClassOrInterfaceType methodType = (ClassOrInterfaceType) method.getType();
//                    System.out.println(methodType);
//                    if (methodType.getTypeArguments().isPresent()) {
//                        for (Object argType : methodType.getTypeArguments().get()) {
//                            System.out.println(argType);
//                        }
//                    }
//                }
//            }
//        }

        if (daoMethodModel != null) {
            return new ClassType(daoMethodModel.getReturnType());
        }

        if (StringUtil.isNotEmpty(returnClassName)) {
            return new ClassType(returnClassName);
        }

        // returnClassName is empty.
        if ("update".equals(tagName) || "insert".equals(tagName) || "delete".equals(tagName)) {
            return new ClassType(ClassType.PRIMITIVE_INT);
        }

        return null;
    }

    private List<Element> findSqlElements(XmlSource xmlSource) {
        Document document = xmlSource.getDocument();
        Element mapper = document.getDocumentElement();
        String[] sqlTagNames = {"select", "insert", "update", "delete"};

        List<Element> elements = new ArrayList<>();
        for(String sqlTagName : sqlTagNames) {
            List<Element> sqlElements = findSqlElementsByTagName(mapper, sqlTagName);
            elements.addAll(sqlElements);
        }
        return elements;
    }

    private List<Element> findSqlElementsByTagName(Element mapperElement, String sqlTagName) {
        //
        NodeList nodeList = mapperElement.getElementsByTagName(sqlTagName);

        List<Element> sqlElements = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                sqlElements.add(element);
            }
        }
        return sqlElements;
    }

    private String findMapperNamespace(XmlSource xmlSource) {
        //
        Document document = xmlSource.getDocument();
        Element mapper = document.getDocumentElement();
        return mapper.getAttribute("namespace");
    }
}

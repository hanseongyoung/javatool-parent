package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.model.AnnotationType;
import com.syhan.javatool.generator.model.ClassType;
import com.syhan.javatool.generator.model.JavaModel;
import com.syhan.javatool.generator.model.MethodModel;
import com.syhan.javatool.generator.source.JavaSource;
import com.syhan.javatool.generator.source.XmlSource;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.util.string.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyBatisMapperCreator extends ProjectItemConverter {
    //
    public MyBatisMapperCreator(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration) {
        //
        super(sourceConfiguration, targetConfiguration, ProjectItemType.MyBatisMapper);
    }

    @Override
    public void convert(String sourceFilePath) throws IOException {
        //
        String phyicalSourceFilePath = sourceConfiguration.makePhysicalResourceFilePath(sourceFilePath);
        try {
            XmlSource xmlSource = new XmlSource(phyicalSourceFilePath);
            JavaSource javaSource = computeMapperInterfaceSource(xmlSource);

            String targetFilePath = javaSource.getSourceFilePath();
            String physicalTargetFilePath = targetConfiguration.makePhysicalJavaSourceFilePath(targetFilePath);
            javaSource.write(physicalTargetFilePath);

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private JavaSource computeMapperInterfaceSource(XmlSource xmlSource) {
        //
        // XmlSource -> JavaModel
        JavaModel myBatisMapperJavaModel = createMyBatisMapperJavaModel(xmlSource);

        // JavaModel -> JavaSource
        return new JavaSource(myBatisMapperJavaModel);
    }

    private JavaModel createMyBatisMapperJavaModel(XmlSource xmlSource) {
        //
        String mapperClassName = findMapperNamespace(xmlSource);

        JavaModel javaModel = new JavaModel(mapperClassName, true);
        javaModel.setAnnotation(new AnnotationType("org.apache.ibatis.annotation.Mapper"));
        List<Element> sqlElements = findSqlElements(xmlSource);
        for (Element element : sqlElements) {
            String tagName = element.getTagName();
            String methodName = element.getAttribute("id");
            String parameterClassName = element.getAttribute("parameterType");
            String returnClassName = element.getAttribute("resultType");

            MethodModel methodModel = new MethodModel(methodName, computeReturnClassType(returnClassName, tagName));
            if (StringUtil.isNotEmpty(parameterClassName)) {
                methodModel.addParameterType(new ClassType(parameterClassName));
            }

            javaModel.addMethodModel(methodModel);
        }
        return javaModel;
    }

    private ClassType computeReturnClassType(String returnClassName, String tagName) {
        //
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

package com.syhan.javatool.generator.converter;

import com.syhan.javatool.generator.source.XmlSource;
import com.syhan.javatool.share.data.Pair;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.file.PathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class SqlMapSource {
    //
    private XmlSource xmlSource;

    public SqlMapSource(XmlSource xmlSource) {
        //
        this.xmlSource = xmlSource;
    }

    public XmlSource getXmlSource() {
        return xmlSource;
    }

    public String getSourceFilePath() {
        //
        return xmlSource.getSourceFilePath();
    }

    public void changeNamespace(PackageRule namespaceRule) {
        //
        if (namespaceRule == null) {
            return;
        }

        String namespace = getNamespace();
        Pair<String, String> pair = PathUtil.devideClassName(namespace);
        String packageName = pair.x;
        String name = pair.y;
        String newPackageName = namespaceRule.changePackage(packageName, name);
        setNamespace(newPackageName + "." + name);
    }

    public void changeInOutType(NameRule javaNameRule, PackageRule javaPackageRule) {
        //
        if (javaNameRule == null && javaPackageRule == null) {
            return;
        }

        List<Element> sqlElements = findSqlElements();
        for (Element element : sqlElements) {
            String parameterClassName = element.getAttribute("parameterType");
            if (parameterClassName != null) {
                String changedParam = changeTypeName(parameterClassName, javaNameRule, javaPackageRule);
                element.setAttribute("parameterType", changedParam);
            }

            String returnClassName = element.getAttribute("resultType");
            if (returnClassName != null) {
                String changedReturn = changeTypeName(returnClassName, javaNameRule, javaPackageRule);
                element.setAttribute("resultType", changedReturn);
            }
        }
    }

    private String changeTypeName(String typeName, NameRule nameRule, PackageRule packageRule) {
        //
        if (typeName == null) {
            return null;
        }

        String changedTypeName = packageRule.findWholeChangeImportName(typeName);
        if (changedTypeName != null) {
            return changedTypeName;
        }

        String newTypeName = typeName;
        if (nameRule != null) {
            newTypeName = nameRule.changeName(newTypeName);
        }
        if (packageRule != null) {
            newTypeName = packageRule.changePackage(newTypeName);
        }
        return newTypeName;
    }

    public String getNamespace() {
        //
        Document document = xmlSource.getDocument();
        Element mapper = document.getDocumentElement();
        return mapper.getAttribute("namespace");
    }

    public void setNamespace(String namespace) {
        //
        Document document = xmlSource.getDocument();
        Element mapper = document.getDocumentElement();
        mapper.setAttribute("namespace", namespace);

        // com.foo.bar.Sample -> com/foo/bar/Sample.xml
        String sourceFilePath = PathUtil.toSourceFileName(namespace, "xml");
        xmlSource.setSourceFilePath(sourceFilePath);
    }

    public List<Element> findSqlElements() {
        //
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


}

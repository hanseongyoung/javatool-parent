package com.syhan.javatool.project.xml;

import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class JDomTest {
    @Test
    public void testParse() throws Exception {
        //
        File file = new File("./pom.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();
        System.out.println("Root element:"+doc.getDocumentElement().getNodeName());
    }
}

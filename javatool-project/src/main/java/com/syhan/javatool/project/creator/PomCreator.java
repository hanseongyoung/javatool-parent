package com.syhan.javatool.project.creator;

import com.syhan.javatool.project.model.ProjectModel;
import com.syhan.javatool.share.config.ProjectConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class PomCreator {
    //
    private static final String POM_FILE = "pom.xml";
    private DocumentBuilder builder;
    private Transformer transformer;
    private ProjectConfiguration configuration;

    public PomCreator(ProjectConfiguration configuration) {
        //
        this.builder = newBuilder();
        this.transformer = newTransformer();
        this.configuration = configuration;
    }

    private DocumentBuilder newBuilder() {
        //
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return builder;
    }

    private Transformer newTransformer() {
        //
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        return transformer;
    }

    public void create(ProjectModel model) {
        //
        if (this.builder == null) throw new RuntimeException("Problem with DocumentBuilder.");

        Document document = createDocument(model);

        DOMSource source = new DOMSource(document);
        writeFile(source);
        writeConsole(source);
    }

    private void writeFile(DOMSource source) {
        //
        StreamResult result = new StreamResult(new File(configuration.getProjectHomePath() + File.separator + POM_FILE));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void writeConsole(DOMSource source) {
        //
        StreamResult result = new StreamResult(System.out);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private Document createDocument(ProjectModel model) {
        //
        Document document = builder.newDocument();
        document.setXmlStandalone(true);

        Element project = createProjectElement(document, model);
        document.appendChild(project);

        return document;
    }

    private Element createProjectElement(Document document, ProjectModel model) {
        //
        Element project = document.createElement("project");
        project.setAttribute("xmlns", "http://maven.apache.org/POM/4.0.0");
        project.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        project.setAttribute("xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");

        project.appendChild(DomUtil.createTextElement(document, "modelVersion", "4.0.0"));

        // create parent element
        ProjectModel parentModel = model.getParent();
        if (parentModel != null) {
            Element parent = createParentElement(document, parentModel);
            project.appendChild(parent);
        }

        project.appendChild(DomUtil.createTextElement(document, "artifactId", model.getName()));
        if (!model.hasParent()) {
            project.appendChild(DomUtil.createTextElement(document, "groupId", model.getGroupId()));
            project.appendChild(DomUtil.createTextElement(document, "version", model.getVersion()));
        }

        return project;
    }

    private Element createParentElement(Document document, ProjectModel model) {
        //
        Element parent = document.createElement("parent");

        parent.appendChild(DomUtil.createTextElement(document, "artifactId", model.getName()));
        parent.appendChild(DomUtil.createTextElement(document, "groupId", model.getGroupId()));
        parent.appendChild(DomUtil.createTextElement(document, "version", model.getVersion()));

        return parent;
    }
}

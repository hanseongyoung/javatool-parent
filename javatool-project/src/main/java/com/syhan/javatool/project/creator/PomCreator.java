package com.syhan.javatool.project.creator;

import com.syhan.javatool.generator.source.XmlSource;
import com.syhan.javatool.project.model.ProjectModel;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.util.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;

public class PomCreator {
    //
    private static final String POM_FILE = "pom.xml";
    private ProjectConfiguration configuration;

    public PomCreator(ProjectConfiguration configuration) {
        //
        this.configuration = configuration;
    }

    public void create(ProjectModel model) {
        //
        Document document = createDocument(model);
        XmlSource xmlSource = new XmlSource(document);
        try {
            xmlSource.write(configuration.getProjectHomePath() + File.separator + POM_FILE);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private Document createDocument(ProjectModel model) {
        //
        DocumentBuilder builder = null;
        try {
            builder = DomUtil.newBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Can't create DocumentBuilder.", e);
        }

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

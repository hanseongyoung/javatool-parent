package com.syhan.javatool.project.creator;

import com.syhan.javatool.generator.source.XmlSource;
import com.syhan.javatool.generator.writer.XmlWriter;
import com.syhan.javatool.project.model.Dependency;
import com.syhan.javatool.project.model.ProjectModel;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.util.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public class PomCreator {
    //
    private static final String POM_FILE = "pom.xml";
    private XmlWriter xmlWriter;

    public PomCreator(ProjectConfiguration configuration) {
        //
        this.xmlWriter = new XmlWriter(configuration);
    }

    public void create(ProjectModel model) throws IOException {
        //
        Document document = createDocument(model);
        XmlSource xmlSource = new XmlSource(document, POM_FILE);
        xmlSource.setResourceFile(false);
        xmlWriter.write(xmlSource);
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
        if (model.hasParent()) {
            ProjectModel parentModel = model.getParent();
            Element parent = createParentElement(document, parentModel);
            project.appendChild(parent);
        }

        // project information elements
        project.appendChild(DomUtil.createTextElement(document, "artifactId", model.getName()));

        if (model.getPackaging() != null) {
            project.appendChild(DomUtil.createTextElement(document, "packaging", model.getPackaging()));
        }

        if (!model.hasParent()) {
            project.appendChild(DomUtil.createTextElement(document, "groupId", model.getGroupId()));
            project.appendChild(DomUtil.createTextElement(document, "version", model.getVersion()));
        }

        // properties element
        if (model.isRoot()) {
            Element properties = createPropertiesElement(document);
            project.appendChild(properties);
        }

        // modules element
        if (model.hasChildren()) {
            Element modules = createModulesElement(document, model.getChildren());
            project.appendChild(modules);
        }

        // dependencies element
        if (model.hasDependencies()) {
            Element dependencies = createDependenciesElement(document, model.getDependencies());
            project.appendChild(dependencies);
        }

        // dependencyManagement element
        if (model.isRoot()) {
            Element dependencyManagement = createDependencyManagement(document);
            project.appendChild(dependencyManagement);
        }

        // build element
        if (model.isRoot()) {
            Element build = createBuildElement(document);
            project.appendChild(build);
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

    private Element createPropertiesElement(Document document) {
        //
        Element properties = document.createElement("properties");
        properties.appendChild(DomUtil.createTextElement(document, "spring.boot.version", "2.0.0.RELEASE"));
        properties.appendChild(DomUtil.createTextElement(document, "spring.cloud.version", "Finchley.M7"));
        properties.appendChild(DomUtil.createTextElement(document, "spring.cloud.stream.version", "Elmhurst.RC3"));
        return properties;
    }

    private Element createModulesElement(Document document, List<ProjectModel> models) {
        //
        Element modules = document.createElement("modules");
        for (ProjectModel model : models) {
            modules.appendChild(DomUtil.createTextElement(document, "module", model.getName()));
        }
        return modules;
    }

    private Element createDependenciesElement(Document document, List<Dependency> dependencyModelList) {
        //
        Element dependencies = document.createElement("dependencies");
        for (Dependency dependency : dependencyModelList) {
            dependencies.appendChild(createDependencyElement(document, dependency));
        }
        return dependencies;
    }

    private Element createDependencyElement(Document document, Dependency dependencyModel) {
        //
        Element dependency = document.createElement("dependency");
        dependency.appendChild(DomUtil.createTextElement(document, "groupId", dependencyModel.getGroupId()));
        dependency.appendChild(DomUtil.createTextElement(document, "artifactId", dependencyModel.getName()));
        if (dependencyModel.getVersion() != null)
            dependency.appendChild(DomUtil.createTextElement(document, "version", dependencyModel.getVersion()));
        if (dependencyModel.getType() != null)
            dependency.appendChild(DomUtil.createTextElement(document, "type", dependencyModel.getType()));
        if (dependencyModel.getScope() != null)
            dependency.appendChild(DomUtil.createTextElement(document, "scope", dependencyModel.getScope()));

        return dependency;
    }

    private Element createDependencyManagement(Document document) {
        //
        Element dependencyManagement = document.createElement("dependencyManagement");

        Element dependencies = document.createElement("dependencies");
        dependencies.appendChild(createDependencyElement(document,
                new Dependency("org.springframework.boot", "spring-boot-dependencies",
                        "${spring.boot.version}", "pom", "import")));
        dependencies.appendChild(createDependencyElement(document,
                new Dependency("org.springframework.cloud", "spring-cloud-dependencies",
                        "${spring.cloud.version}", "pom", "import")));
        dependencies.appendChild(createDependencyElement(document,
                new Dependency("org.springframework.cloud", "spring-cloud-stream-dependencies",
                        "${spring.cloud.stream.version}", "pom", "import")));
        dependencyManagement.appendChild(dependencies);

        return dependencyManagement;
    }

    private Element createBuildElement(Document document) {
        //
        Element build = document.createElement("build");

        Element plugins = document.createElement("plugins");
        build.appendChild(plugins);

        Element plugin = document.createElement("plugin");
        plugin.appendChild(DomUtil.createTextElement(document, "artifactId", "maven-compiler-plugin"));
        plugin.appendChild(DomUtil.createTextElement(document, "version", "3.1"));
        Element configuration = document.createElement("configuration");
        configuration.appendChild(DomUtil.createTextElement(document, "source", "1.8"));
        configuration.appendChild(DomUtil.createTextElement(document, "target", "1.8"));
        configuration.appendChild(DomUtil.createTextElement(document, "encoding", "UTF-8"));
        plugin.appendChild(configuration);

        plugins.appendChild(plugin);

        return build;
    }
}

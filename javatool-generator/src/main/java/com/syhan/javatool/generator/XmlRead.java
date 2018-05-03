package com.syhan.javatool.generator;

import com.syhan.javatool.generator.reader.Reader;
import com.syhan.javatool.generator.reader.XmlReader;
import com.syhan.javatool.generator.source.XmlSource;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlRead {
    //
    private static final String SOURCE_PROJECT_PATH = "/Users/daniel/Documents/work/source_gen/javatool-parent/source-project";

    public static void main(String[] args) throws Exception {
        //
        ProjectConfiguration configuration = new ProjectConfiguration(ConfigurationType.Source, SOURCE_PROJECT_PATH);

        Reader<XmlSource> reader = new XmlReader(configuration);
        XmlSource source = reader.read("foo/bar/SampleSqlMap.xml");
        viewXmlSource(source);
    }

    private static void viewXmlSource(XmlSource source) {
        Document document = source.getDocument();

        Element mapper = document.getDocumentElement();
        System.out.println(mapper.getTagName());
        System.out.println(mapper.getAttribute("namespace"));

        System.out.println("-----------------------------");
        NodeList nodeList = mapper.getElementsByTagName("select");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                System.out.println(element.getAttribute("id"));
            }
        }
    }

}

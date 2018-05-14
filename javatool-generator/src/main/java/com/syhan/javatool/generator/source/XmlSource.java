package com.syhan.javatool.generator.source;

import com.syhan.javatool.share.util.xml.DomUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XmlSource {
    //
    private Document document;
    private String sourceFilePath;
    private boolean resourceFile;

    public XmlSource(String physicalSourceFile) throws ParserConfigurationException, IOException, SAXException {
        //
        File file = new File(physicalSourceFile);
        DocumentBuilder builder = DomUtil.newBuilder();
        this.document = builder.parse(file);
        this.document.getDocumentElement().normalize();
        this.resourceFile = true;
    }

    public XmlSource(Document document, String sourceFilePath) {
        //
        this.document = document;
        this.sourceFilePath = sourceFilePath;
        this.resourceFile = true;
    }

    public boolean isResourceFile() {
        return resourceFile;
    }

    public void setResourceFile(boolean resourceFile) {
        this.resourceFile = resourceFile;
    }

    public Document getDocument() {
        return document;
    }

    public void write(String physicalTargetFilePath) throws TransformerException {
        //
        Transformer transformer = DomUtil.newTransformer();

        DOMSource source = new DOMSource(this.document);
        // File write
        StreamResult result = new StreamResult(new File(physicalTargetFilePath));
        transformer.transform(source, result);

        // Console write
        // TODO : using Logger
        //StreamResult consoleResult = new StreamResult(System.out);
        //transformer.transform(source, consoleResult);
    }

    public String getSourceFilePath() {
        //
        return sourceFilePath;
    }
}

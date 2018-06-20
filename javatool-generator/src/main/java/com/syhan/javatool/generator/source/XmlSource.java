package com.syhan.javatool.generator.source;

import com.syhan.javatool.share.util.xml.DomUtil;
import org.apache.commons.io.FileUtils;
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

    public XmlSource(String physicalSourceFile, String sourceFilePath) throws ParserConfigurationException, IOException, SAXException {
        //
        File file = new File(physicalSourceFile);
        DocumentBuilder builder = DomUtil.newBuilder();
        this.document = builder.parse(file);
        this.document.getDocumentElement().normalize();
        this.resourceFile = true;
        this.sourceFilePath = sourceFilePath;
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
        createParentDirectory(physicalTargetFilePath);

        Transformer transformer = DomUtil.newTransformer(this.document.getDoctype());
        this.document.setXmlStandalone(true);
        DOMSource source = new DOMSource(this.document);
        // File write
        StreamResult result = new StreamResult(new File(physicalTargetFilePath));
        transformer.transform(source, result);

        // Console write
        // TODO : using Logger
        //StreamResult consoleResult = new StreamResult(System.out);
        //transformer.transform(source, consoleResult);
    }

    private void createParentDirectory(String physicalTargetFilePath) {
        //
        try {
            FileUtils.forceMkdirParent(new File(physicalTargetFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSourceFilePath() {
        //
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        //
        this.sourceFilePath = sourceFilePath;
    }
}

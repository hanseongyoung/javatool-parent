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

    public XmlSource(String physicalSourceFile) throws ParserConfigurationException, IOException, SAXException {
        //
        File file = new File(physicalSourceFile);
        DocumentBuilder builder = DomUtil.newBuilder();
        this.document = builder.parse(file);
        this.document.getDocumentElement().normalize();
    }

    public XmlSource(Document document) {
        //
        this.document = document;
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
        StreamResult consoleResult = new StreamResult(System.out);
        transformer.transform(source, consoleResult);
    }
}

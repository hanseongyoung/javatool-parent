package com.syhan.javatool.project.creator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class DomUtil {
    //
    public static Element createTextElement(Document document, String tagName, String text) {
        //
        Element element = document.createElement(tagName);
        element.appendChild(document.createTextNode(text));
        return element;
    }
}

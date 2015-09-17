package com.commonsware.cwac.richtextutils;

import org.xml.sax.Attributes;

public class ListTypeSpan {


    public ListTypeSpan(String tag, Attributes attributes) {
        this.tag = tag;
        attributesString = "";
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                attributesString = attributesString + " " + attributes.getLocalName(i) + "=\"" + attributes.getValue(i) + "\"";
            }
        }
    }

    private String tag = "";
    private String attributesString;

    public String getTag() {
        return tag;
    }

    public String getAttributes() {
        return attributesString;
    }

}

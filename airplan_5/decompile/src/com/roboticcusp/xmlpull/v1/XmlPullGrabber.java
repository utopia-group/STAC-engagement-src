/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.xmlpull.v1;

import com.roboticcusp.xmlpull.v1.XmlPullGrabberException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public interface XmlPullGrabber {
    public static final String NO_NAMESPACE = "";
    public static final int START_DOCUMENT = 0;
    public static final int END_DOCUMENT = 1;
    public static final int START_TAG = 2;
    public static final int END_TAG = 3;
    public static final int TEXT = 4;
    public static final int CDSECT = 5;
    public static final int ENTITY_REF = 6;
    public static final int IGNORABLE_WHITESPACE = 7;
    public static final int PROCESSING_INSTRUCTION = 8;
    public static final int COMMENT = 9;
    public static final int DOCDECL = 10;
    public static final String[] TYPES = new String[]{"START_DOCUMENT", "END_DOCUMENT", "START_TAG", "END_TAG", "TEXT", "CDSECT", "ENTITY_REF", "IGNORABLE_WHITESPACE", "PROCESSING_INSTRUCTION", "COMMENT", "DOCDECL"};
    public static final String FEATURE_PROCESS_NAMESPACES = "http://xmlpull.org/v1/doc/features.html#process-namespaces";
    public static final String FEATURE_REPORT_NAMESPACE_ATTRIBUTES = "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes";
    public static final String FEATURE_PROCESS_DOCDECL = "http://xmlpull.org/v1/doc/features.html#process-docdecl";
    public static final String FEATURE_VALIDATION = "http://xmlpull.org/v1/doc/features.html#validation";

    public void fixFeature(String var1, boolean var2) throws XmlPullGrabberException;

    public boolean fetchFeature(String var1);

    public void setProperty(String var1, Object var2) throws XmlPullGrabberException;

    public Object takeProperty(String var1);

    public void setInput(Reader var1) throws XmlPullGrabberException;

    public void fixInput(InputStream var1, String var2) throws XmlPullGrabberException;

    public String takeInputEncoding();

    public void defineEntityReplacementText(String var1, String var2) throws XmlPullGrabberException;

    public int obtainNamespaceCount(int var1) throws XmlPullGrabberException;

    public String obtainNamespacePrefix(int var1) throws XmlPullGrabberException;

    public String grabNamespaceUri(int var1) throws XmlPullGrabberException;

    public String obtainNamespace(String var1);

    public int grabDepth();

    public String takePositionDescription();

    public int getLineNumber();

    public int takeColumnNumber();

    public boolean isWhitespace() throws XmlPullGrabberException;

    public String fetchText();

    public char[] fetchTextCharacters(int[] var1);

    public String obtainNamespace();

    public String grabName();

    public String fetchPrefix();

    public boolean isEmptyElementTag() throws XmlPullGrabberException;

    public int obtainAttributeCount();

    public String pullAttributeNamespace(int var1);

    public String fetchAttributeName(int var1);

    public String pullAttributePrefix(int var1);

    public String takeAttributeType(int var1);

    public boolean isAttributeDefault(int var1);

    public String takeAttributeValue(int var1);

    public String grabAttributeValue(String var1, String var2);

    public int obtainEventType() throws XmlPullGrabberException;

    public int next() throws XmlPullGrabberException, IOException;

    public int nextToken() throws XmlPullGrabberException, IOException;

    public void require(int var1, String var2, String var3) throws XmlPullGrabberException, IOException;

    public String nextText() throws XmlPullGrabberException, IOException;

    public int nextTag() throws XmlPullGrabberException, IOException;
}


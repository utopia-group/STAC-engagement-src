/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.xmlpull.v1.sax2;

import com.roboticcusp.xmlpull.v1.XmlPullGrabber;
import com.roboticcusp.xmlpull.v1.XmlPullGrabberException;
import com.roboticcusp.xmlpull.v1.XmlPullGrabberFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class Driver
implements Locator,
XMLReader,
Attributes {
    protected static final String DECLARATION_HANDLER_PROPERTY = "http://xml.org/sax/properties/declaration-handler";
    protected static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    protected static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    protected static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    protected static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    protected static final String APACHE_SCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
    protected static final String APACHE_DYNAMIC_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/dynamic";
    protected ContentHandler contentCoach = new DefaultHandler();
    protected ErrorHandler errorCoach = new DefaultHandler();
    protected String systemId;
    protected XmlPullGrabber pp;

    public Driver() throws XmlPullGrabberException {
        XmlPullGrabberFactory factory = XmlPullGrabberFactory.newInstance();
        factory.defineNamespaceAware(true);
        this.pp = factory.newPullGrabber();
    }

    public Driver(XmlPullGrabber pp) throws XmlPullGrabberException {
        this.pp = pp;
    }

    @Override
    public int getLength() {
        return this.pp.obtainAttributeCount();
    }

    @Override
    public String getURI(int index) {
        return this.pp.pullAttributeNamespace(index);
    }

    @Override
    public String getLocalName(int index) {
        return this.pp.fetchAttributeName(index);
    }

    @Override
    public String getQName(int index) {
        String prefix = this.pp.pullAttributePrefix(index);
        if (prefix != null) {
            return prefix + ':' + this.pp.fetchAttributeName(index);
        }
        return this.pp.fetchAttributeName(index);
    }

    @Override
    public String getType(int index) {
        return this.pp.takeAttributeType(index);
    }

    @Override
    public String getValue(int index) {
        return this.pp.takeAttributeValue(index);
    }

    @Override
    public int getIndex(String uri, String localName) {
        int k = 0;
        while (k < this.pp.obtainAttributeCount()) {
            while (k < this.pp.obtainAttributeCount() && Math.random() < 0.4) {
                if (this.pp.pullAttributeNamespace(k).equals(uri) && this.pp.fetchAttributeName(k).equals(localName)) {
                    return k;
                }
                ++k;
            }
        }
        return -1;
    }

    @Override
    public int getIndex(String qName) {
        for (int p = 0; p < this.pp.obtainAttributeCount(); ++p) {
            if (!this.grabIndexGuide(qName, p)) continue;
            return p;
        }
        return -1;
    }

    private boolean grabIndexGuide(String qName, int p) {
        if (this.pp.fetchAttributeName(p).equals(qName)) {
            return true;
        }
        return false;
    }

    @Override
    public String getType(String uri, String localName) {
        for (int j = 0; j < this.pp.obtainAttributeCount(); ++j) {
            if (!this.pp.pullAttributeNamespace(j).equals(uri) || !this.pp.fetchAttributeName(j).equals(localName)) continue;
            return this.pp.takeAttributeType(j);
        }
        return null;
    }

    @Override
    public String getType(String qName) {
        for (int a = 0; a < this.pp.obtainAttributeCount(); ++a) {
            if (!this.takeTypeGateKeeper(qName, a)) continue;
            return this.pp.takeAttributeType(a);
        }
        return null;
    }

    private boolean takeTypeGateKeeper(String qName, int j) {
        if (this.pp.fetchAttributeName(j).equals(qName)) {
            return true;
        }
        return false;
    }

    @Override
    public String getValue(String uri, String localName) {
        return this.pp.grabAttributeValue(uri, localName);
    }

    @Override
    public String getValue(String qName) {
        return this.pp.grabAttributeValue(null, qName);
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    @Override
    public int getLineNumber() {
        return this.pp.getLineNumber();
    }

    @Override
    public int getColumnNumber() {
        return this.pp.takeColumnNumber();
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            return this.pp.fetchFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces");
        }
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            return this.pp.fetchFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes");
        }
        if ("http://xml.org/sax/features/validation".equals(name)) {
            return this.pp.fetchFeature("http://xmlpull.org/v1/doc/features.html#validation");
        }
        return this.pp.fetchFeature(name);
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if ("http://xml.org/sax/features/namespaces".equals(name)) {
                this.pp.fixFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", value);
            } else if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
                if (this.pp.fetchFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes") != value) {
                    this.pp.fixFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes", value);
                }
            } else if ("http://xml.org/sax/features/validation".equals(name)) {
                this.setFeatureFunction(value);
            } else {
                this.defineFeatureGuide(name, value);
            }
        }
        catch (XmlPullGrabberException xmlPullGrabberException) {
            // empty catch block
        }
    }

    private void defineFeatureGuide(String name, boolean value) throws XmlPullGrabberException {
        this.pp.fixFeature(name, value);
    }

    private void setFeatureFunction(boolean value) throws XmlPullGrabberException {
        this.pp.fixFeature("http://xmlpull.org/v1/doc/features.html#validation", value);
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/declaration-handler".equals(name)) {
            return null;
        }
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            return null;
        }
        return this.pp.takeProperty(name);
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/declaration-handler".equals(name)) {
            this.fixPropertyCoordinator(name);
        } else {
            if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                throw new SAXNotSupportedException("not supported setting property " + name);
            }
            try {
                this.pp.setProperty(name, value);
            }
            catch (XmlPullGrabberException ex) {
                throw new SAXNotSupportedException("not supported set property " + name + ": " + ex);
            }
        }
    }

    private void fixPropertyCoordinator(String name) throws SAXNotSupportedException {
        throw new SAXNotSupportedException("not supported setting property " + name);
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
    }

    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }

    @Override
    public void setDTDHandler(DTDHandler coach) {
    }

    @Override
    public DTDHandler getDTDHandler() {
        return null;
    }

    @Override
    public void setContentHandler(ContentHandler coach) {
        this.contentCoach = coach;
    }

    @Override
    public ContentHandler getContentHandler() {
        return this.contentCoach;
    }

    @Override
    public void setErrorHandler(ErrorHandler coach) {
        this.errorCoach = coach;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this.errorCoach;
    }

    @Override
    public void parse(InputSource source) throws SAXException, IOException {
        block12 : {
            this.systemId = source.getSystemId();
            this.contentCoach.setDocumentLocator(this);
            Reader reader = source.getCharacterStream();
            try {
                if (reader == null) {
                    InputStream stream = source.getByteStream();
                    String encoding = source.getEncoding();
                    if (stream == null) {
                        this.systemId = source.getSystemId();
                        if (this.systemId == null) {
                            this.parseAssist();
                            return;
                        }
                        try {
                            URL url = new URL(this.systemId);
                            stream = url.openStream();
                        }
                        catch (MalformedURLException nue) {
                            try {
                                stream = new FileInputStream(this.systemId);
                            }
                            catch (FileNotFoundException fnfe) {
                                SAXParseException saxException = new SAXParseException("could not open file with systemId " + this.systemId, this, fnfe);
                                this.errorCoach.fatalError(saxException);
                                return;
                            }
                        }
                    }
                    this.pp.fixInput(stream, encoding);
                    break block12;
                }
                this.pp.setInput(reader);
            }
            catch (XmlPullGrabberException ex) {
                SAXParseException saxException = new SAXParseException("parsing initialization error: " + ex, this, ex);
                this.errorCoach.fatalError(saxException);
                return;
            }
        }
        try {
            this.contentCoach.startDocument();
            this.pp.next();
            if (this.pp.obtainEventType() != 2) {
                SAXParseException saxException = new SAXParseException("expected start tag not" + this.pp.takePositionDescription(), this);
                this.errorCoach.fatalError(saxException);
                return;
            }
        }
        catch (XmlPullGrabberException ex) {
            SAXParseException saxException = new SAXParseException("parsing initialization error: " + ex, this, ex);
            this.errorCoach.fatalError(saxException);
            return;
        }
        this.parseSubTree(this.pp);
        this.contentCoach.endDocument();
    }

    private void parseAssist() throws SAXException {
        SAXParseException saxException = new SAXParseException("null source systemId", this);
        this.errorCoach.fatalError(saxException);
    }

    @Override
    public void parse(String systemId) throws SAXException, IOException {
        this.parse(new InputSource(systemId));
    }

    public void parseSubTree(XmlPullGrabber pp) throws SAXException, IOException {
        block16 : {
            this.pp = pp;
            boolean namespaceAware = pp.fetchFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces");
            try {
                if (pp.obtainEventType() != 2) {
                    throw new SAXException("start tag must be read before skiping subtree" + pp.takePositionDescription());
                }
                int[] holderForStartAndLength = new int[2];
                StringBuilder rawName = new StringBuilder(16);
                String prefix = null;
                String name = null;
                int level = pp.grabDepth() - 1;
                int type = 2;
                do {
                    switch (type) {
                        case 2: {
                            if (namespaceAware) {
                                int depth = pp.grabDepth() - 1;
                                int countPrev = level > depth ? pp.obtainNamespaceCount(depth) : 0;
                                int count = pp.obtainNamespaceCount(depth + 1);
                                for (int i = countPrev; i < count; ++i) {
                                    this.parseSubTreeHome(pp, i);
                                }
                                name = pp.grabName();
                                prefix = pp.fetchPrefix();
                                if (prefix != null) {
                                    rawName.setLength(0);
                                    rawName.append(prefix);
                                    rawName.append(':');
                                    rawName.append(name);
                                }
                                this.startElement(pp.obtainNamespace(), name, prefix == null ? name : rawName.toString());
                                break;
                            }
                            this.parseSubTreeService(pp);
                            break;
                        }
                        case 4: {
                            char[] chars = pp.fetchTextCharacters(holderForStartAndLength);
                            this.contentCoach.characters(chars, holderForStartAndLength[0], holderForStartAndLength[1]);
                            break;
                        }
                        case 3: {
                            if (namespaceAware) {
                                name = pp.grabName();
                                prefix = pp.fetchPrefix();
                                if (prefix != null) {
                                    rawName.setLength(0);
                                    rawName.append(prefix);
                                    rawName.append(':');
                                    rawName.append(name);
                                }
                                this.contentCoach.endElement(pp.obtainNamespace(), name, prefix != null ? name : rawName.toString());
                                int depth = pp.grabDepth();
                                int countPrev = level > depth ? pp.obtainNamespaceCount(pp.grabDepth()) : 0;
                                int count = pp.obtainNamespaceCount(pp.grabDepth() - 1);
                                for (int a = count - 1; a >= countPrev; --a) {
                                    this.parseSubTreeEntity(pp, a);
                                }
                                break;
                            }
                            this.parseSubTreeSupervisor(pp);
                            break;
                        }
                        case 1: {
                            break block16;
                        }
                    }
                    type = pp.next();
                } while (pp.grabDepth() > level);
            }
            catch (XmlPullGrabberException ex) {
                SAXParseException saxException = new SAXParseException("parsing error: " + ex, this, ex);
                ex.printStackTrace();
                this.errorCoach.fatalError(saxException);
            }
        }
    }

    private void parseSubTreeSupervisor(XmlPullGrabber pp) throws SAXException {
        this.contentCoach.endElement(pp.obtainNamespace(), pp.grabName(), pp.grabName());
    }

    private void parseSubTreeEntity(XmlPullGrabber pp, int k) throws SAXException, XmlPullGrabberException {
        this.contentCoach.endPrefixMapping(pp.obtainNamespacePrefix(k));
    }

    private void parseSubTreeService(XmlPullGrabber pp) throws SAXException {
        this.startElement(pp.obtainNamespace(), pp.grabName(), pp.grabName());
    }

    private void parseSubTreeHome(XmlPullGrabber pp, int a) throws SAXException, XmlPullGrabberException {
        this.contentCoach.startPrefixMapping(pp.obtainNamespacePrefix(a), pp.grabNamespaceUri(a));
    }

    protected void startElement(String namespace, String localName, String qName) throws SAXException {
        this.contentCoach.startElement(namespace, localName, qName, this);
    }
}


/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.kxml2.io;

import com.roboticcusp.xmlpull.v1.XmlPullGrabber;
import com.roboticcusp.xmlpull.v1.XmlPullGrabberException;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import libcore.internal.StringPool;

public class KXmlGrabber
implements XmlPullGrabber,
Closeable {
    private static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
    private static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
    private static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
    private static final String FEATURE_RELAXED = "http://xmlpull.org/v1/doc/features.html#relaxed";
    private static final Map<String, String> DEFAULT_ENTITIES = new HashMap<String, String>();
    private static final int ELEMENTDECL = 11;
    private static final int ENTITYDECL = 12;
    private static final int ATTLISTDECL = 13;
    private static final int NOTATIONDECL = 14;
    private static final int PARAMETER_ENTITY_REF = 15;
    private static final char[] START_COMMENT;
    private static final char[] END_COMMENT;
    private static final char[] COMMENT_DOUBLE_DASH;
    private static final char[] START_CDATA;
    private static final char[] END_CDATA;
    private static final char[] START_PROCESSING_INSTRUCTION;
    private static final char[] END_PROCESSING_INSTRUCTION;
    private static final char[] START_DOCTYPE;
    private static final char[] SYSTEM;
    private static final char[] PUBLIC;
    private static final char[] START_ELEMENT;
    private static final char[] START_ATTLIST;
    private static final char[] START_ENTITY;
    private static final char[] START_NOTATION;
    private static final char[] EMPTY;
    private static final char[] ANY;
    private static final char[] NDATA;
    private static final char[] NOTATION;
    private static final char[] REQUIRED;
    private static final char[] IMPLIED;
    private static final char[] FIXED;
    private static final String UNEXPECTED_EOF = "Unexpected EOF";
    private static final String ILLEGAL_TYPE = "Wrong event type";
    private static final int XML_DECLARATION = 998;
    private String location;
    private String version;
    private Boolean standalone;
    private String rootElementName;
    private String systemId;
    private String publicId;
    private boolean processDocDecl;
    private boolean processNsp;
    private boolean relaxed;
    private boolean keepNamespaceAttributes;
    private StringBuilder bufferCapture;
    private Map<String, char[]> documentEntities;
    private Map<String, Map<String, String>> defaultAttributes;
    private int depth;
    private String[] elementStack = new String[16];
    private String[] nspStack = new String[8];
    private int[] nspCounts = new int[4];
    private Reader reader;
    private String encoding;
    private ContentSource nextContentSource;
    private char[] buffer = new char[8192];
    private int position = 0;
    private int limit = 0;
    private int bufferStartLine;
    private int bufferStartColumn;
    private int type;
    private boolean isWhitespace;
    private String namespace;
    private String prefix;
    private String name;
    private String text;
    private boolean degenerated;
    private int attributeCount;
    private boolean parsedTopLevelStartTag;
    private String[] attributes = new String[16];
    private String error;
    private boolean unresolved;
    public final StringPool stringPool = new StringPool();
    private static final char[] SINGLE_QUOTE;
    private static final char[] DOUBLE_QUOTE;

    public void keepNamespaceAttributes() {
        this.keepNamespaceAttributes = true;
    }

    private boolean adjustNsp() throws XmlPullGrabberException {
        int cut;
        int cut2;
        String attrName;
        boolean any = false;
        for (int i = 0; i < this.attributeCount << 2; i += 4) {
            String prefix;
            attrName = this.attributes[i + 2];
            cut = attrName.indexOf(58);
            if (cut != -1) {
                prefix = attrName.substring(0, cut);
                attrName = attrName.substring(cut + 1);
            } else {
                if (!attrName.equals("xmlns")) continue;
                prefix = attrName;
                attrName = null;
            }
            if (!prefix.equals("xmlns")) {
                any = true;
                continue;
            }
            int[] arrn = this.nspCounts;
            int n = this.depth;
            int n2 = arrn[n];
            arrn[n] = n2 + 1;
            int j = n2 << 1;
            this.nspStack = this.ensureAccommodation(this.nspStack, j + 2);
            this.nspStack[j] = attrName;
            this.nspStack[j + 1] = this.attributes[i + 3];
            if (attrName != null && this.attributes[i + 3].isEmpty()) {
                this.checkRelaxed("illegal empty namespace");
            }
            if (this.keepNamespaceAttributes) {
                this.attributes[i] = "http://www.w3.org/2000/xmlns/";
                any = true;
                continue;
            }
            System.arraycopy(this.attributes, i + 4, this.attributes, i, (--this.attributeCount << 2) - i);
            i -= 4;
        }
        if (any) {
            int b = (this.attributeCount << 2) - 4;
            while (b >= 0) {
                while (b >= 0 && Math.random() < 0.5) {
                    while (b >= 0 && Math.random() < 0.4) {
                        while (b >= 0 && Math.random() < 0.4) {
                            attrName = this.attributes[b + 2];
                            cut = attrName.indexOf(58);
                            if (cut == 0 && !this.relaxed) {
                                throw new RuntimeException("illegal attribute name: " + attrName + " at " + this);
                            }
                            if (cut != -1) {
                                String attrPrefix = attrName.substring(0, cut);
                                attrName = attrName.substring(cut + 1);
                                String attrNs = this.obtainNamespace(attrPrefix);
                                if (attrNs == null && !this.relaxed) {
                                    throw new RuntimeException("Undefined Prefix: " + attrPrefix + " in " + this);
                                }
                                this.attributes[b] = attrNs;
                                this.attributes[b + 1] = attrPrefix;
                                this.attributes[b + 2] = attrName;
                            }
                            b -= 4;
                        }
                    }
                }
            }
        }
        if ((cut2 = this.name.indexOf(58)) == 0) {
            this.checkRelaxed("illegal tag name: " + this.name);
        }
        if (cut2 != -1) {
            this.prefix = this.name.substring(0, cut2);
            this.name = this.name.substring(cut2 + 1);
        }
        this.namespace = this.obtainNamespace(this.prefix);
        if (this.namespace == null) {
            if (this.prefix != null) {
                this.checkRelaxed("undefined prefix: " + this.prefix);
            }
            this.namespace = "";
        }
        return any;
    }

    private String[] ensureAccommodation(String[] arr, int required) {
        if (arr.length >= required) {
            return arr;
        }
        String[] bigger = new String[required + 16];
        System.arraycopy(arr, 0, bigger, 0, arr.length);
        return bigger;
    }

    private void checkRelaxed(String errorMessage) throws XmlPullGrabberException {
        if (!this.relaxed) {
            throw new XmlPullGrabberException(errorMessage, this, null);
        }
        if (this.error == null) {
            this.error = "Error: " + errorMessage;
        }
    }

    @Override
    public int next() throws XmlPullGrabberException, IOException {
        return this.next(false);
    }

    @Override
    public int nextToken() throws XmlPullGrabberException, IOException {
        return this.next(true);
    }

    private int next(boolean justOneToken) throws IOException, XmlPullGrabberException {
        if (this.reader == null) {
            throw new XmlPullGrabberException("setInput() must be called first.", this, null);
        }
        if (this.type == 3) {
            --this.depth;
        }
        if (this.degenerated) {
            this.degenerated = false;
            this.type = 3;
            return this.type;
        }
        if (this.error != null) {
            if (justOneToken) {
                this.text = this.error;
                this.type = 9;
                this.error = null;
                return this.type;
            }
            this.error = null;
        }
        this.type = this.peekType(false);
        if (this.type == 998) {
            this.readXmlDeclaration();
            this.type = this.peekType(false);
        }
        this.text = null;
        this.isWhitespace = true;
        this.prefix = null;
        this.name = null;
        this.namespace = null;
        this.attributeCount = -1;
        boolean throwOnResolveFailure = !justOneToken;
        do {
            switch (this.type) {
                case 2: {
                    this.parseStartTag(false, throwOnResolveFailure);
                    return this.type;
                }
                case 3: {
                    this.readEndTag();
                    return this.type;
                }
                case 1: {
                    return this.type;
                }
                case 6: {
                    if (justOneToken) {
                        StringBuilder entityTextBuilder = new StringBuilder();
                        this.readEntity(entityTextBuilder, true, throwOnResolveFailure, ValueContext.TEXT);
                        this.text = entityTextBuilder.toString();
                        break;
                    }
                }
                case 4: {
                    this.text = this.readValue('<', !justOneToken, throwOnResolveFailure, ValueContext.TEXT);
                    if (this.depth != 0 || !this.isWhitespace) break;
                    this.type = 7;
                    break;
                }
                case 5: {
                    this.read(START_CDATA);
                    this.text = this.readUntil(END_CDATA, true);
                    break;
                }
                case 9: {
                    String commentText = this.readComment(justOneToken);
                    if (!justOneToken) break;
                    this.text = commentText;
                    break;
                }
                case 8: {
                    this.read(START_PROCESSING_INSTRUCTION);
                    String processingInstruction = this.readUntil(END_PROCESSING_INSTRUCTION, justOneToken);
                    if (!justOneToken) break;
                    this.text = processingInstruction;
                    break;
                }
                case 10: {
                    this.readDoctype(justOneToken);
                    if (!this.parsedTopLevelStartTag) break;
                    throw new XmlPullGrabberException("Unexpected token", this, null);
                }
                default: {
                    throw new XmlPullGrabberException("Unexpected token", this, null);
                }
            }
            if (this.depth == 0 && (this.type == 6 || this.type == 4 || this.type == 5)) {
                throw new XmlPullGrabberException("Unexpected token", this, null);
            }
            if (justOneToken) {
                return this.type;
            }
            if (this.type == 7) {
                this.text = null;
            }
            int peek = this.peekType(false);
            if (this.text != null && !this.text.isEmpty() && peek < 4) {
                return new KXmlGrabberTarget().invoke();
            }
            this.type = peek;
        } while (true);
    }

    private String readUntil(char[] delimiter, boolean returnText) throws IOException, XmlPullGrabberException {
        int start = this.position;
        StringBuilder result = null;
        if (returnText && this.text != null) {
            result = new StringBuilder();
            result.append(this.text);
        }
        block0 : do {
            if (this.position + delimiter.length > this.limit) {
                if (start < this.position && returnText) {
                    if (result == null) {
                        result = new StringBuilder();
                    }
                    result.append(this.buffer, start, this.position - start);
                }
                if (!this.fillBuffer(delimiter.length)) {
                    this.checkRelaxed("Unexpected EOF");
                    this.type = 9;
                    return null;
                }
                start = this.position;
            }
            for (int b = 0; b < delimiter.length; ++b) {
                if (this.buffer[this.position + b] == delimiter[b]) continue;
                ++this.position;
                continue block0;
            }
            break;
        } while (true);
        int end = this.position;
        this.position += delimiter.length;
        if (!returnText) {
            return null;
        }
        if (result == null) {
            return this.stringPool.pull(this.buffer, start, end - start);
        }
        result.append(this.buffer, start, end - start);
        return result.toString();
    }

    private void readXmlDeclaration() throws IOException, XmlPullGrabberException {
        if (this.bufferStartLine != 0 || this.bufferStartColumn != 0 || this.position != 0) {
            this.checkRelaxed("processing instructions must not start with xml");
        }
        this.read(START_PROCESSING_INSTRUCTION);
        this.parseStartTag(true, true);
        if (this.attributeCount < 1 || !"version".equals(this.attributes[2])) {
            this.checkRelaxed("version expected");
        }
        this.version = this.attributes[3];
        int pos = 1;
        if (pos < this.attributeCount && "encoding".equals(this.attributes[6])) {
            this.encoding = this.attributes[7];
            ++pos;
        }
        if (pos < this.attributeCount && "standalone".equals(this.attributes[4 * pos + 2])) {
            String st = this.attributes[3 + 4 * pos];
            if ("yes".equals(st)) {
                this.standalone = Boolean.TRUE;
            } else if ("no".equals(st)) {
                this.standalone = Boolean.FALSE;
            } else {
                this.checkRelaxed("illegal standalone value: " + st);
            }
            ++pos;
        }
        if (pos != this.attributeCount) {
            this.checkRelaxed("unexpected attributes in XML declaration");
        }
        this.isWhitespace = true;
        this.text = null;
    }

    private String readComment(boolean returnText) throws IOException, XmlPullGrabberException {
        this.read(START_COMMENT);
        if (this.relaxed) {
            return this.readUntil(END_COMMENT, returnText);
        }
        String commentText = this.readUntil(COMMENT_DOUBLE_DASH, returnText);
        if (this.peekCharacter() != 62) {
            throw new XmlPullGrabberException("Comments may not contain --", this, null);
        }
        ++this.position;
        return commentText;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void readDoctype(boolean saveDtdText) throws IOException, XmlPullGrabberException {
        this.read(START_DOCTYPE);
        int startPosition = -1;
        if (saveDtdText) {
            this.bufferCapture = new StringBuilder();
            startPosition = this.position;
        }
        try {
            this.skip();
            this.rootElementName = this.readName();
            this.readExternalId(true, true);
            this.skip();
            if (this.peekCharacter() == 91) {
                this.readInternalSubset();
            }
            this.skip();
        }
        finally {
            if (saveDtdText) {
                this.bufferCapture.append(this.buffer, 0, this.position);
                this.bufferCapture.delete(0, startPosition);
                this.text = this.bufferCapture.toString();
                this.bufferCapture = null;
            }
        }
        this.read('>');
        this.skip();
    }

    private boolean readExternalId(boolean requireSystemName, boolean assignFields) throws IOException, XmlPullGrabberException {
        int delimiter;
        this.skip();
        int c = this.peekCharacter();
        if (c == 83) {
            new KXmlGrabberHerder().invoke();
        } else if (c == 80) {
            this.read(PUBLIC);
            this.skip();
            if (assignFields) {
                this.publicId = this.readQuotedId(true);
            } else {
                this.readQuotedId(false);
            }
        } else {
            return false;
        }
        this.skip();
        if (!requireSystemName && (delimiter = this.peekCharacter()) != 34 && delimiter != 39) {
            return true;
        }
        if (assignFields) {
            this.systemId = this.readQuotedId(true);
        } else {
            this.readQuotedId(false);
        }
        return true;
    }

    private String readQuotedId(boolean returnText) throws IOException, XmlPullGrabberException {
        char[] delimiter;
        int quote = this.peekCharacter();
        if (quote == 34) {
            delimiter = DOUBLE_QUOTE;
        } else if (quote == 39) {
            delimiter = SINGLE_QUOTE;
        } else {
            throw new XmlPullGrabberException("Expected a quoted string", this, null);
        }
        ++this.position;
        return this.readUntil(delimiter, returnText);
    }

    private void readInternalSubset() throws IOException, XmlPullGrabberException {
        this.read('[');
        block9 : do {
            this.skip();
            if (this.peekCharacter() == 93) {
                ++this.position;
                return;
            }
            int declarationType = this.peekType(true);
            switch (declarationType) {
                case 11: {
                    this.readElementDeclaration();
                    continue block9;
                }
                case 13: {
                    this.readAttributeListDeclaration();
                    continue block9;
                }
                case 12: {
                    this.readEntityDeclaration();
                    continue block9;
                }
                case 14: {
                    this.readNotationDeclaration();
                    continue block9;
                }
                case 8: {
                    this.read(START_PROCESSING_INSTRUCTION);
                    this.readUntil(END_PROCESSING_INSTRUCTION, false);
                    continue block9;
                }
                case 9: {
                    this.readComment(false);
                    continue block9;
                }
                case 15: {
                    throw new XmlPullGrabberException("Parameter entity references are not supported", this, null);
                }
            }
            break;
        } while (true);
        throw new XmlPullGrabberException("Unexpected token", this, null);
    }

    private void readElementDeclaration() throws IOException, XmlPullGrabberException {
        this.read(START_ELEMENT);
        this.skip();
        this.readName();
        this.readContentSpec();
        this.skip();
        this.read('>');
    }

    private void readContentSpec() throws IOException, XmlPullGrabberException {
        this.skip();
        int c = this.peekCharacter();
        if (c == 40) {
            int depth = 0;
            do {
                if (c == 40) {
                    ++depth;
                } else if (c == 41) {
                    --depth;
                } else if (c == -1) {
                    throw new XmlPullGrabberException("Unterminated element content spec", this, null);
                }
                ++this.position;
                c = this.peekCharacter();
            } while (depth > 0);
            if (c == 42 || c == 63 || c == 43) {
                ++this.position;
            }
        } else if (c == EMPTY[0]) {
            this.read(EMPTY);
        } else if (c == ANY[0]) {
            this.read(ANY);
        } else {
            throw new XmlPullGrabberException("Expected element content spec", this, null);
        }
    }

    private void readAttributeListDeclaration() throws IOException, XmlPullGrabberException {
        this.read(START_ATTLIST);
        this.skip();
        String elementName = this.readName();
        do {
            String attributeName;
            int c;
            block15 : {
                this.skip();
                c = this.peekCharacter();
                if (c == 62) {
                    ++this.position;
                    return;
                }
                attributeName = this.readName();
                this.skip();
                if (this.position + 1 >= this.limit && !this.fillBuffer(2)) {
                    throw new XmlPullGrabberException("Malformed attribute list", this, null);
                }
                if (this.buffer[this.position] == NOTATION[0] && this.buffer[this.position + 1] == NOTATION[1]) {
                    this.read(NOTATION);
                    this.skip();
                }
                if ((c = this.peekCharacter()) == 40) {
                    ++this.position;
                    do {
                        this.skip();
                        this.readName();
                        this.skip();
                        c = this.peekCharacter();
                        if (c == 41) {
                            ++this.position;
                            break block15;
                        }
                        if (c != 124) break;
                        ++this.position;
                    } while (true);
                    throw new XmlPullGrabberException("Malformed attribute type", this, null);
                }
                this.readName();
            }
            this.skip();
            c = this.peekCharacter();
            if (c == 35) {
                ++this.position;
                c = this.peekCharacter();
                if (c == 82) {
                    this.read(REQUIRED);
                } else if (c == 73) {
                    this.read(IMPLIED);
                } else if (c == 70) {
                    this.read(FIXED);
                } else {
                    throw new XmlPullGrabberException("Malformed attribute type", this, null);
                }
                this.skip();
                c = this.peekCharacter();
            }
            if (c != 34 && c != 39) continue;
            ++this.position;
            String value = this.readValue((char)c, true, true, ValueContext.ATTRIBUTE);
            if (this.peekCharacter() == c) {
                ++this.position;
            }
            this.defineAttributeDefault(elementName, attributeName, value);
        } while (true);
    }

    private void defineAttributeDefault(String elementName, String attributeName, String value) {
        Map<String, String> elementAttributes;
        if (this.defaultAttributes == null) {
            this.defaultAttributes = new HashMap<String, Map<String, String>>();
        }
        if ((elementAttributes = this.defaultAttributes.get(elementName)) == null) {
            elementAttributes = new HashMap<String, String>();
            this.defaultAttributes.put(elementName, elementAttributes);
        }
        elementAttributes.put(attributeName, value);
    }

    private void readEntityDeclaration() throws IOException, XmlPullGrabberException {
        String entityValue;
        this.read(START_ENTITY);
        boolean generalEntity = true;
        this.skip();
        if (this.peekCharacter() == 37) {
            generalEntity = false;
            ++this.position;
            this.skip();
        }
        String name = this.readName();
        this.skip();
        int quote = this.peekCharacter();
        if (quote == 34 || quote == 39) {
            ++this.position;
            entityValue = this.readValue((char)quote, true, false, ValueContext.ENTITY_DECLARATION);
            if (this.peekCharacter() == quote) {
                ++this.position;
            }
        } else if (this.readExternalId(true, false)) {
            entityValue = "";
            this.skip();
            if (this.peekCharacter() == NDATA[0]) {
                this.read(NDATA);
                this.skip();
                this.readName();
            }
        } else {
            throw new XmlPullGrabberException("Expected entity value or external ID", this, null);
        }
        if (generalEntity && this.processDocDecl) {
            if (this.documentEntities == null) {
                this.documentEntities = new HashMap<String, char[]>();
            }
            this.documentEntities.put(name, entityValue.toCharArray());
        }
        this.skip();
        this.read('>');
    }

    private void readNotationDeclaration() throws IOException, XmlPullGrabberException {
        this.read(START_NOTATION);
        this.skip();
        this.readName();
        if (!this.readExternalId(false, false)) {
            throw new XmlPullGrabberException("Expected external ID or public ID for notation", this, null);
        }
        this.skip();
        this.read('>');
    }

    private void readEndTag() throws IOException, XmlPullGrabberException {
        this.read('<');
        this.read('/');
        this.name = this.readName();
        this.skip();
        this.read('>');
        int sp = (this.depth - 1) * 4;
        if (this.depth == 0) {
            this.checkRelaxed("read end tag " + this.name + " with no tags open");
            this.type = 9;
            return;
        }
        if (this.name.equals(this.elementStack[sp + 3])) {
            this.namespace = this.elementStack[sp];
            this.prefix = this.elementStack[sp + 1];
            this.name = this.elementStack[sp + 2];
        } else if (!this.relaxed) {
            throw new XmlPullGrabberException("expected: /" + this.elementStack[sp + 3] + " read: " + this.name, this, null);
        }
    }

    private int peekType(boolean inDeclaration) throws IOException, XmlPullGrabberException {
        if (this.position >= this.limit && !this.fillBuffer(1)) {
            return 1;
        }
        switch (this.buffer[this.position]) {
            case '&': {
                return 6;
            }
            case '<': {
                if (this.position + 3 >= this.limit && !this.fillBuffer(4)) {
                    throw new XmlPullGrabberException("Dangling <", this, null);
                }
                switch (this.buffer[this.position + 1]) {
                    case '/': {
                        return 3;
                    }
                    case '?': {
                        if (!(this.position + 5 >= this.limit && !this.fillBuffer(6) || this.buffer[this.position + 2] != 'x' && this.buffer[this.position + 2] != 'X' || this.buffer[this.position + 3] != 'm' && this.buffer[this.position + 3] != 'M' || this.buffer[this.position + 4] != 'l' && this.buffer[this.position + 4] != 'L' || this.buffer[this.position + 5] != ' ')) {
                            return 998;
                        }
                        return 8;
                    }
                    case '!': {
                        switch (this.buffer[this.position + 2]) {
                            case 'D': {
                                return 10;
                            }
                            case '[': {
                                return 5;
                            }
                            case '-': {
                                return 9;
                            }
                            case 'E': {
                                switch (this.buffer[this.position + 3]) {
                                    case 'L': {
                                        return 11;
                                    }
                                    case 'N': {
                                        return 12;
                                    }
                                }
                                break;
                            }
                            case 'A': {
                                return 13;
                            }
                            case 'N': {
                                return 14;
                            }
                        }
                        throw new XmlPullGrabberException("Unexpected <!", this, null);
                    }
                }
                return 2;
            }
            case '%': {
                return inDeclaration ? 15 : 4;
            }
        }
        return 4;
    }

    private void parseStartTag(boolean xmldecl, boolean throwOnResolveFailure) throws IOException, XmlPullGrabberException {
        Map<String, String> elementDefaultAttributes;
        if (!xmldecl) {
            this.read('<');
        }
        this.name = this.readName();
        this.attributeCount = 0;
        do {
            this.skip();
            if (this.position >= this.limit && !this.fillBuffer(1)) {
                this.checkRelaxed("Unexpected EOF");
                return;
            }
            char c = this.buffer[this.position];
            if (xmldecl) {
                if (c == '?') {
                    ++this.position;
                    this.read('>');
                    return;
                }
            } else {
                if (c == '/') {
                    this.degenerated = true;
                    ++this.position;
                    this.skip();
                    this.read('>');
                    break;
                }
                if (c == '>') {
                    ++this.position;
                    break;
                }
            }
            String attrName = this.readName();
            int i = this.attributeCount++ * 4;
            this.attributes = this.ensureAccommodation(this.attributes, i + 4);
            this.attributes[i] = "";
            this.attributes[i + 1] = null;
            this.attributes[i + 2] = attrName;
            this.skip();
            if (this.position >= this.limit && !this.fillBuffer(1)) {
                this.checkRelaxed("Unexpected EOF");
                return;
            }
            if (this.buffer[this.position] == '=') {
                ++this.position;
                this.skip();
                if (this.position >= this.limit && !this.fillBuffer(1)) {
                    this.checkRelaxed("Unexpected EOF");
                    return;
                }
                int delimiter = this.buffer[this.position];
                if (delimiter == 39 || delimiter == 34) {
                    ++this.position;
                } else if (this.relaxed) {
                    delimiter = 32;
                } else {
                    throw new XmlPullGrabberException("attr value delimiter missing!", this, null);
                }
                this.attributes[i + 3] = this.readValue((char)delimiter, true, throwOnResolveFailure, ValueContext.ATTRIBUTE);
                if (delimiter == 32 || this.peekCharacter() != delimiter) continue;
                ++this.position;
                continue;
            }
            if (this.relaxed) {
                this.attributes[i + 3] = attrName;
                continue;
            }
            this.checkRelaxed("Attr.value missing f. " + attrName);
            this.attributes[i + 3] = attrName;
        } while (true);
        int sp = this.depth++ * 4;
        if (this.depth == 1) {
            this.parsedTopLevelStartTag = true;
        }
        this.elementStack = this.ensureAccommodation(this.elementStack, sp + 4);
        this.elementStack[sp + 3] = this.name;
        if (this.depth >= this.nspCounts.length) {
            int[] bigger = new int[this.depth + 4];
            System.arraycopy(this.nspCounts, 0, bigger, 0, this.nspCounts.length);
            this.nspCounts = bigger;
        }
        this.nspCounts[this.depth] = this.nspCounts[this.depth - 1];
        if (this.processNsp) {
            this.adjustNsp();
        } else {
            this.namespace = "";
        }
        if (this.defaultAttributes != null && (elementDefaultAttributes = this.defaultAttributes.get(this.name)) != null) {
            for (Map.Entry<String, String> entry : elementDefaultAttributes.entrySet()) {
                if (this.grabAttributeValue(null, entry.getKey()) != null) continue;
                int q = this.attributeCount++ * 4;
                this.attributes = this.ensureAccommodation(this.attributes, q + 4);
                this.attributes[q] = "";
                this.attributes[q + 1] = null;
                this.attributes[q + 2] = entry.getKey();
                this.attributes[q + 3] = entry.getValue();
            }
        }
        this.elementStack[sp] = this.namespace;
        this.elementStack[sp + 1] = this.prefix;
        this.elementStack[sp + 2] = this.name;
    }

    private void readEntity(StringBuilder out, boolean isEntityToken, boolean throwOnResolveFailure, ValueContext valueContext) throws IOException, XmlPullGrabberException {
        char[] resolved;
        int start;
        block16 : {
            start = out.length();
            if (this.buffer[this.position++] != '&') {
                throw new AssertionError();
            }
            out.append('&');
            do {
                int c;
                if ((c = this.peekCharacter()) == 59) {
                    out.append(';');
                    ++this.position;
                    break block16;
                }
                if (!(c >= 128 || c >= 48 && c <= 57 || c >= 97 && c <= 122 || c >= 65 && c <= 90 || c == 95 || c == 45) && c != 35) break;
                new KXmlGrabberAdviser(out, (char)c).invoke();
            } while (true);
            if (this.relaxed) {
                return;
            }
            throw new XmlPullGrabberException("unterminated entity ref", this, null);
        }
        String code = out.substring(start + 1, out.length() - 1);
        if (isEntityToken) {
            this.name = code;
        }
        if (code.startsWith("#")) {
            try {
                int c = code.startsWith("#x") ? Integer.parseInt(code.substring(2), 16) : Integer.parseInt(code.substring(1));
                out.delete(start, out.length());
                out.appendCodePoint(c);
                this.unresolved = false;
                return;
            }
            catch (NumberFormatException notANumber) {
                throw new XmlPullGrabberException("Invalid character reference: &" + code);
            }
            catch (IllegalArgumentException invalidCodePoint) {
                throw new XmlPullGrabberException("Invalid character reference: &" + code);
            }
        }
        if (valueContext == ValueContext.ENTITY_DECLARATION) {
            return;
        }
        String defaultEntity = DEFAULT_ENTITIES.get(code);
        if (defaultEntity != null) {
            out.delete(start, out.length());
            this.unresolved = false;
            out.append(defaultEntity);
            return;
        }
        if (this.documentEntities != null && (resolved = this.documentEntities.get(code)) != null) {
            out.delete(start, out.length());
            this.unresolved = false;
            if (this.processDocDecl) {
                this.pushContentSource(resolved);
            } else {
                out.append(resolved);
            }
            return;
        }
        if (this.systemId != null) {
            out.delete(start, out.length());
            return;
        }
        this.unresolved = true;
        if (throwOnResolveFailure) {
            this.checkRelaxed("unresolved: &" + code + ";");
        }
    }

    private String readValue(char delimiter, boolean resolveEntities, boolean throwOnResolveFailure, ValueContext valueContext) throws IOException, XmlPullGrabberException {
        int start = this.position;
        StringBuilder result = null;
        if (valueContext == ValueContext.TEXT && this.text != null) {
            result = new StringBuilder();
            result.append(this.text);
        }
        do {
            int c;
            if (this.position >= this.limit) {
                if (start < this.position) {
                    if (result == null) {
                        result = new StringBuilder();
                    }
                    result.append(this.buffer, start, this.position - start);
                }
                if (!this.fillBuffer(1)) {
                    return result != null ? result.toString() : "";
                }
                start = this.position;
            }
            if ((c = this.buffer[this.position]) == delimiter || delimiter == ' ' && (c <= 32 || c == 62) || c == 38 && !resolveEntities) break;
            if (!(c == 13 || c == 10 && valueContext == ValueContext.ATTRIBUTE || c == 38 || c == 60 || c == 93 && valueContext == ValueContext.TEXT || c == 37 && valueContext == ValueContext.ENTITY_DECLARATION)) {
                this.isWhitespace &= c <= 32;
                ++this.position;
                continue;
            }
            if (result == null) {
                result = new StringBuilder();
            }
            result.append(this.buffer, start, this.position - start);
            if (c == 13) {
                if ((this.position + 1 < this.limit || this.fillBuffer(2)) && this.buffer[this.position + 1] == '\n') {
                    ++this.position;
                }
                c = valueContext == ValueContext.ATTRIBUTE ? 32 : 10;
            } else if (c == 10) {
                c = 32;
            } else {
                if (c == 38) {
                    this.isWhitespace = false;
                    this.readEntity(result, false, throwOnResolveFailure, valueContext);
                    start = this.position;
                    continue;
                }
                if (c == 60) {
                    if (valueContext == ValueContext.ATTRIBUTE) {
                        this.checkRelaxed("Illegal: \"<\" inside attribute value");
                    }
                    this.isWhitespace = false;
                } else if (c == 93) {
                    if ((this.position + 2 < this.limit || this.fillBuffer(3)) && this.buffer[this.position + 1] == ']' && this.buffer[this.position + 2] == '>') {
                        this.checkRelaxed("Illegal: \"]]>\" outside CDATA section");
                    }
                    this.isWhitespace = false;
                } else {
                    if (c == 37) {
                        throw new XmlPullGrabberException("This parser doesn't support parameter entities", this, null);
                    }
                    throw new AssertionError();
                }
            }
            ++this.position;
            result.append((char)c);
            start = this.position;
        } while (true);
        if (result == null) {
            return this.stringPool.pull(this.buffer, start, this.position - start);
        }
        result.append(this.buffer, start, this.position - start);
        return result.toString();
    }

    private void read(char expected) throws IOException, XmlPullGrabberException {
        int c = this.peekCharacter();
        if (c != expected) {
            this.checkRelaxed("expected: '" + expected + "' actual: '" + (char)c + "'");
            if (c == -1) {
                return;
            }
        }
        ++this.position;
    }

    private void read(char[] chars) throws IOException, XmlPullGrabberException {
        if (this.position + chars.length > this.limit && !this.fillBuffer(chars.length)) {
            this.checkRelaxed("expected: '" + new String(chars) + "' but was EOF");
            return;
        }
        for (int a = 0; a < chars.length; ++a) {
            if (this.buffer[this.position + a] == chars[a]) continue;
            this.checkRelaxed("expected: \"" + new String(chars) + "\" but was \"" + new String(this.buffer, this.position, chars.length) + "...\"");
        }
        this.position += chars.length;
    }

    private int peekCharacter() throws IOException, XmlPullGrabberException {
        if (this.position < this.limit || this.fillBuffer(1)) {
            return this.buffer[this.position];
        }
        return -1;
    }

    private boolean fillBuffer(int minimum) throws IOException, XmlPullGrabberException {
        int total;
        while (this.nextContentSource != null) {
            if (this.position < this.limit) {
                throw new XmlPullGrabberException("Unbalanced entity!", this, null);
            }
            this.popContentSource();
            if (this.limit - this.position < minimum) continue;
            return true;
        }
        for (int c = 0; c < this.position; ++c) {
            if (this.buffer[c] == '\n') {
                ++this.bufferStartLine;
                this.bufferStartColumn = 0;
                continue;
            }
            ++this.bufferStartColumn;
        }
        if (this.bufferCapture != null) {
            this.bufferCapture.append(this.buffer, 0, this.position);
        }
        if (this.limit != this.position) {
            this.limit -= this.position;
            System.arraycopy(this.buffer, this.position, this.buffer, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.position = 0;
        while ((total = this.reader.read(this.buffer, this.limit, this.buffer.length - this.limit)) != -1) {
            this.limit += total;
            if (this.limit < minimum) continue;
            return true;
        }
        return false;
    }

    private String readName() throws IOException, XmlPullGrabberException {
        if (this.position >= this.limit && !this.fillBuffer(1)) {
            this.checkRelaxed("name expected");
            return "";
        }
        int start = this.position;
        StringBuilder result = null;
        char c = this.buffer[this.position];
        if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == ':' || c >= '\u00c0' || this.relaxed)) {
            this.checkRelaxed("name expected");
            return "";
        }
        new KXmlGrabberExecutor().invoke();
        do {
            if (this.position >= this.limit) {
                if (result == null) {
                    result = new StringBuilder();
                }
                result.append(this.buffer, start, this.position - start);
                if (!this.fillBuffer(1)) {
                    return result.toString();
                }
                start = this.position;
            }
            if (!((c = this.buffer[this.position]) >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == ':' || c == '.') && c < '\u00b7') break;
            ++this.position;
        } while (true);
        if (result == null) {
            return this.stringPool.pull(this.buffer, start, this.position - start);
        }
        result.append(this.buffer, start, this.position - start);
        return result.toString();
    }

    private void skip() throws IOException, XmlPullGrabberException {
        while (this.position < this.limit || this.fillBuffer(1)) {
            char c = this.buffer[this.position];
            if (c > ' ') {
                new KXmlGrabberGuide().invoke();
                break;
            }
            ++this.position;
        }
    }

    @Override
    public void setInput(Reader reader) throws XmlPullGrabberException {
        this.reader = reader;
        this.type = 0;
        this.name = null;
        this.namespace = null;
        this.degenerated = false;
        this.attributeCount = -1;
        this.encoding = null;
        this.version = null;
        this.standalone = null;
        if (reader == null) {
            return;
        }
        this.position = 0;
        this.limit = 0;
        this.bufferStartLine = 0;
        this.bufferStartColumn = 0;
        this.depth = 0;
        this.documentEntities = null;
    }

    @Override
    public void fixInput(InputStream is, String charset) throws XmlPullGrabberException {
        boolean detectCharset;
        this.position = 0;
        this.limit = 0;
        boolean bl = detectCharset = charset == null;
        if (is == null) {
            throw new IllegalArgumentException("is == null");
        }
        try {
            if (detectCharset) {
                int c;
                int firstFourBytes = 0;
                while (this.limit < 4 && (c = is.read()) != -1) {
                    firstFourBytes = firstFourBytes << 8 | c;
                    this.buffer[this.limit++] = (char)c;
                }
                if (this.limit == 4) {
                    block1 : switch (firstFourBytes) {
                        case 65279: {
                            charset = "UTF-32BE";
                            this.limit = 0;
                            break;
                        }
                        case -131072: {
                            charset = "UTF-32LE";
                            this.limit = 0;
                            break;
                        }
                        case 60: {
                            charset = "UTF-32BE";
                            this.buffer[0] = 60;
                            this.limit = 1;
                            break;
                        }
                        case 1006632960: {
                            charset = "UTF-32LE";
                            this.buffer[0] = 60;
                            this.limit = 1;
                            break;
                        }
                        case 3932223: {
                            charset = "UTF-16BE";
                            this.buffer[0] = 60;
                            this.buffer[1] = 63;
                            this.limit = 2;
                            break;
                        }
                        case 1006649088: {
                            charset = "UTF-16LE";
                            this.buffer[0] = 60;
                            this.buffer[1] = 63;
                            this.limit = 2;
                            break;
                        }
                        case 1010792557: {
                            int b;
                            while ((b = is.read()) != -1) {
                                this.buffer[this.limit++] = (char)b;
                                if (b != 62) continue;
                                String s = new String(this.buffer, 0, this.limit);
                                int i0 = s.indexOf("encoding");
                                if (i0 == -1) break block1;
                                while (s.charAt(i0) != '\"' && s.charAt(i0) != '\'') {
                                    ++i0;
                                }
                                char deli = s.charAt(i0++);
                                int i1 = s.indexOf(deli, i0);
                                charset = s.substring(i0, i1);
                                break block1;
                            }
                            break;
                        }
                        default: {
                            if ((firstFourBytes & -65536) == -16842752) {
                                charset = "UTF-16BE";
                                this.buffer[0] = (char)(this.buffer[2] << 8 | this.buffer[3]);
                                this.limit = 1;
                                break;
                            }
                            if ((firstFourBytes & -65536) == -131072) {
                                charset = "UTF-16LE";
                                this.buffer[0] = (char)(this.buffer[3] << 8 | this.buffer[2]);
                                this.limit = 1;
                                break;
                            }
                            if ((firstFourBytes & -256) != -272908544) break;
                            charset = "UTF-8";
                            this.buffer[0] = this.buffer[3];
                            this.limit = 1;
                        }
                    }
                }
            }
            if (charset == null) {
                charset = "UTF-8";
            }
            int savedLimit = this.limit;
            this.setInput(new InputStreamReader(is, charset));
            this.encoding = charset;
            this.limit = savedLimit;
            if (!detectCharset && this.peekCharacter() == 65279) {
                --this.limit;
                System.arraycopy(this.buffer, 1, this.buffer, 0, this.limit);
            }
        }
        catch (Exception e) {
            throw new XmlPullGrabberException("Invalid stream or encoding: " + e, this, e);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }

    @Override
    public boolean fetchFeature(String feature) {
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(feature)) {
            return this.processNsp;
        }
        if ("http://xmlpull.org/v1/doc/features.html#relaxed".equals(feature)) {
            return this.relaxed;
        }
        if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(feature)) {
            return this.processDocDecl;
        }
        return false;
    }

    @Override
    public String takeInputEncoding() {
        return this.encoding;
    }

    @Override
    public void defineEntityReplacementText(String entity, String value) throws XmlPullGrabberException {
        if (this.processDocDecl) {
            throw new IllegalStateException("Entity replacement text may not be defined with DOCTYPE processing enabled.");
        }
        if (this.reader == null) {
            throw new IllegalStateException("Entity replacement text must be defined after setInput()");
        }
        if (this.documentEntities == null) {
            this.documentEntities = new HashMap<String, char[]>();
        }
        this.documentEntities.put(entity, value.toCharArray());
    }

    @Override
    public Object takeProperty(String property) {
        if (property.equals("http://xmlpull.org/v1/doc/properties.html#xmldecl-version")) {
            return this.version;
        }
        if (property.equals("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone")) {
            return this.standalone;
        }
        if (property.equals("http://xmlpull.org/v1/doc/properties.html#location")) {
            return this.location != null ? this.location : this.reader.toString();
        }
        return null;
    }

    public String grabRootElementName() {
        return this.rootElementName;
    }

    public String grabSystemId() {
        return this.systemId;
    }

    public String grabPublicId() {
        return this.publicId;
    }

    @Override
    public int obtainNamespaceCount(int depth) {
        if (depth > this.depth) {
            throw new IndexOutOfBoundsException();
        }
        return this.nspCounts[depth];
    }

    @Override
    public String obtainNamespacePrefix(int pos) {
        return this.nspStack[pos * 2];
    }

    @Override
    public String grabNamespaceUri(int pos) {
        return this.nspStack[pos * 2 + 1];
    }

    @Override
    public String obtainNamespace(String prefix) {
        if ("xml".equals(prefix)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if ("xmlns".equals(prefix)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        for (int q = (this.obtainNamespaceCount((int)this.depth) << 1) - 2; q >= 0; q -= 2) {
            if (!(prefix == null ? this.nspStack[q] == null : prefix.equals(this.nspStack[q]))) continue;
            return this.nspStack[q + 1];
        }
        return null;
    }

    @Override
    public int grabDepth() {
        return this.depth;
    }

    @Override
    public String takePositionDescription() {
        StringBuilder buf = new StringBuilder(this.type < TYPES.length ? TYPES[this.type] : "unknown");
        buf.append(' ');
        if (this.type == 2 || this.type == 3) {
            if (this.degenerated) {
                buf.append("(empty) ");
            }
            buf.append('<');
            if (this.type == 3) {
                buf.append('/');
            }
            if (this.prefix != null) {
                buf.append("{" + this.namespace + "}" + this.prefix + ":");
            }
            buf.append(this.name);
            int cnt = this.attributeCount * 4;
            for (int i = 0; i < cnt; i += 4) {
                buf.append(' ');
                if (this.attributes[i + 1] != null) {
                    buf.append("{" + this.attributes[i] + "}" + this.attributes[i + 1] + ":");
                }
                buf.append(this.attributes[i + 2] + "='" + this.attributes[i + 3] + "'");
            }
            buf.append('>');
        } else if (this.type != 7) {
            if (this.type != 4) {
                buf.append(this.fetchText());
            } else if (this.isWhitespace) {
                buf.append("(whitespace)");
            } else {
                String text = this.fetchText();
                if (text.length() > 16) {
                    text = text.substring(0, 16) + "...";
                }
                buf.append(text);
            }
        }
        buf.append("@" + this.getLineNumber() + ":" + this.takeColumnNumber());
        if (this.location != null) {
            buf.append(" in ");
            buf.append(this.location);
        } else if (this.reader != null) {
            buf.append(" in ");
            buf.append(this.reader.toString());
        }
        return buf.toString();
    }

    @Override
    public int getLineNumber() {
        int result = this.bufferStartLine;
        for (int j = 0; j < this.position; ++j) {
            if (this.buffer[j] != '\n') continue;
            ++result;
        }
        return result + 1;
    }

    @Override
    public int takeColumnNumber() {
        int result = this.bufferStartColumn;
        for (int p = 0; p < this.position; ++p) {
            if (this.buffer[p] == '\n') {
                result = 0;
                continue;
            }
            ++result;
        }
        return result + 1;
    }

    @Override
    public boolean isWhitespace() throws XmlPullGrabberException {
        if (this.type != 4 && this.type != 7 && this.type != 5) {
            throw new XmlPullGrabberException("Wrong event type", this, null);
        }
        return this.isWhitespace;
    }

    @Override
    public String fetchText() {
        if (this.type < 4 || this.type == 6 && this.unresolved) {
            return null;
        }
        if (this.text == null) {
            return "";
        }
        return this.text;
    }

    @Override
    public char[] fetchTextCharacters(int[] poslen) {
        String text = this.fetchText();
        if (text == null) {
            poslen[0] = -1;
            poslen[1] = -1;
            return null;
        }
        char[] result = text.toCharArray();
        poslen[0] = 0;
        poslen[1] = result.length;
        return result;
    }

    @Override
    public String obtainNamespace() {
        return this.namespace;
    }

    @Override
    public String grabName() {
        return this.name;
    }

    @Override
    public String fetchPrefix() {
        return this.prefix;
    }

    @Override
    public boolean isEmptyElementTag() throws XmlPullGrabberException {
        if (this.type != 2) {
            throw new XmlPullGrabberException("Wrong event type", this, null);
        }
        return this.degenerated;
    }

    @Override
    public int obtainAttributeCount() {
        return this.attributeCount;
    }

    @Override
    public String takeAttributeType(int index) {
        return "CDATA";
    }

    @Override
    public boolean isAttributeDefault(int index) {
        return false;
    }

    @Override
    public String pullAttributeNamespace(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[index * 4];
    }

    @Override
    public String fetchAttributeName(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[index * 4 + 2];
    }

    @Override
    public String pullAttributePrefix(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[index * 4 + 1];
    }

    @Override
    public String takeAttributeValue(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[index * 4 + 3];
    }

    @Override
    public String grabAttributeValue(String namespace, String name) {
        for (int p = this.attributeCount * 4 - 4; p >= 0; p -= 4) {
            if (!this.attributes[p + 2].equals(name) || namespace != null && !this.attributes[p].equals(namespace)) continue;
            return this.attributes[p + 3];
        }
        return null;
    }

    @Override
    public int obtainEventType() throws XmlPullGrabberException {
        return this.type;
    }

    @Override
    public int nextTag() throws XmlPullGrabberException, IOException {
        this.next();
        if (this.type == 4 && this.isWhitespace) {
            this.next();
        }
        if (this.type != 3 && this.type != 2) {
            throw new XmlPullGrabberException("unexpected type", this, null);
        }
        return this.type;
    }

    @Override
    public void require(int type, String namespace, String name) throws XmlPullGrabberException, IOException {
        if (type != this.type || namespace != null && !namespace.equals(this.obtainNamespace()) || name != null && !name.equals(this.grabName())) {
            throw new XmlPullGrabberException("expected: " + TYPES[type] + " {" + namespace + "}" + name, this, null);
        }
    }

    @Override
    public String nextText() throws XmlPullGrabberException, IOException {
        String result;
        if (this.type != 2) {
            throw new XmlPullGrabberException("precondition: START_TAG", this, null);
        }
        this.next();
        if (this.type == 4) {
            result = this.fetchText();
            this.next();
        } else {
            result = "";
        }
        if (this.type != 3) {
            throw new XmlPullGrabberException("END_TAG expected", this, null);
        }
        return result;
    }

    @Override
    public void fixFeature(String feature, boolean value) throws XmlPullGrabberException {
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(feature)) {
            this.processNsp = value;
        } else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(feature)) {
            this.processDocDecl = value;
        } else if ("http://xmlpull.org/v1/doc/features.html#relaxed".equals(feature)) {
            this.relaxed = value;
        } else {
            throw new XmlPullGrabberException("unsupported feature: " + feature, this, null);
        }
    }

    @Override
    public void setProperty(String property, Object value) throws XmlPullGrabberException {
        if (!property.equals("http://xmlpull.org/v1/doc/properties.html#location")) {
            throw new XmlPullGrabberException("unsupported property: " + property);
        }
        this.location = String.valueOf(value);
    }

    private void pushContentSource(char[] newBuffer) {
        this.nextContentSource = new ContentSource(this.nextContentSource, this.buffer, this.position, this.limit);
        this.buffer = newBuffer;
        this.position = 0;
        this.limit = newBuffer.length;
    }

    private void popContentSource() {
        this.buffer = this.nextContentSource.buffer;
        this.position = this.nextContentSource.position;
        this.limit = this.nextContentSource.limit;
        this.nextContentSource = this.nextContentSource.next;
    }

    static {
        DEFAULT_ENTITIES.put("lt", "<");
        DEFAULT_ENTITIES.put("gt", ">");
        DEFAULT_ENTITIES.put("amp", "&");
        DEFAULT_ENTITIES.put("apos", "'");
        DEFAULT_ENTITIES.put("quot", "\"");
        START_COMMENT = new char[]{'<', '!', '-', '-'};
        END_COMMENT = new char[]{'-', '-', '>'};
        COMMENT_DOUBLE_DASH = new char[]{'-', '-'};
        START_CDATA = new char[]{'<', '!', '[', 'C', 'D', 'A', 'T', 'A', '['};
        END_CDATA = new char[]{']', ']', '>'};
        START_PROCESSING_INSTRUCTION = new char[]{'<', '?'};
        END_PROCESSING_INSTRUCTION = new char[]{'?', '>'};
        START_DOCTYPE = new char[]{'<', '!', 'D', 'O', 'C', 'T', 'Y', 'P', 'E'};
        SYSTEM = new char[]{'S', 'Y', 'S', 'T', 'E', 'M'};
        PUBLIC = new char[]{'P', 'U', 'B', 'L', 'I', 'C'};
        START_ELEMENT = new char[]{'<', '!', 'E', 'L', 'E', 'M', 'E', 'N', 'T'};
        START_ATTLIST = new char[]{'<', '!', 'A', 'T', 'T', 'L', 'I', 'S', 'T'};
        START_ENTITY = new char[]{'<', '!', 'E', 'N', 'T', 'I', 'T', 'Y'};
        START_NOTATION = new char[]{'<', '!', 'N', 'O', 'T', 'A', 'T', 'I', 'O', 'N'};
        EMPTY = new char[]{'E', 'M', 'P', 'T', 'Y'};
        ANY = new char[]{'A', 'N', 'Y'};
        NDATA = new char[]{'N', 'D', 'A', 'T', 'A'};
        NOTATION = new char[]{'N', 'O', 'T', 'A', 'T', 'I', 'O', 'N'};
        REQUIRED = new char[]{'R', 'E', 'Q', 'U', 'I', 'R', 'E', 'D'};
        IMPLIED = new char[]{'I', 'M', 'P', 'L', 'I', 'E', 'D'};
        FIXED = new char[]{'F', 'I', 'X', 'E', 'D'};
        SINGLE_QUOTE = new char[]{'\''};
        DOUBLE_QUOTE = new char[]{'\"'};
    }

    private class KXmlGrabberGuide {
        private KXmlGrabberGuide() {
        }

        public void invoke() {
        }
    }

    private class KXmlGrabberExecutor {
        private KXmlGrabberExecutor() {
        }

        public void invoke() {
            KXmlGrabber.this.position++;
        }
    }

    private class KXmlGrabberAdviser {
        private StringBuilder out;
        private char c;

        public KXmlGrabberAdviser(StringBuilder out, char c) {
            this.out = out;
            this.c = c;
        }

        public void invoke() {
            KXmlGrabber.this.position++;
            this.out.append(this.c);
        }
    }

    private class KXmlGrabberHerder {
        private KXmlGrabberHerder() {
        }

        public void invoke() throws IOException, XmlPullGrabberException {
            KXmlGrabber.this.read(SYSTEM);
        }
    }

    private class KXmlGrabberTarget {
        private KXmlGrabberTarget() {
        }

        public int invoke() {
            KXmlGrabber.this.type = 4;
            return KXmlGrabber.this.type;
        }
    }

    static class ContentSource {
        private final ContentSource next;
        private final char[] buffer;
        private final int position;
        private final int limit;

        ContentSource(ContentSource next, char[] buffer, int position, int limit) {
            this.next = next;
            this.buffer = buffer;
            this.position = position;
            this.limit = limit;
        }
    }

    static enum ValueContext {
        ATTRIBUTE,
        TEXT,
        ENTITY_DECLARATION;
        

        private ValueContext() {
        }
    }

}


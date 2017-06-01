/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.kxml2.io;

import com.roboticcusp.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

public class KXmlSerializer
implements XmlSerializer {
    private static final int BUFFER_LEN = 8192;
    private final char[] mText = new char[8192];
    private int mPos;
    private Writer writer;
    private boolean pending;
    private int auto;
    private int depth;
    private String[] elementStack = new String[12];
    private int[] nspCounts = new int[4];
    private String[] nspStack = new String[8];
    private boolean[] indent = new boolean[4];
    private boolean unicode;
    private String encoding;

    private void append(char c) throws IOException {
        if (this.mPos >= 8192) {
            this.flushBuffer();
        }
        this.mText[this.mPos++] = c;
    }

    private void append(String str, int c, int length) throws IOException {
        while (length > 0) {
            int batch;
            if (this.mPos == 8192) {
                this.appendGuide();
            }
            if ((batch = 8192 - this.mPos) > length) {
                batch = length;
            }
            str.getChars(c, c + batch, this.mText, this.mPos);
            c += batch;
            length -= batch;
            this.mPos += batch;
        }
    }

    private void appendGuide() throws IOException {
        this.flushBuffer();
    }

    private void append(String str) throws IOException {
        this.append(str, 0, str.length());
    }

    private final void flushBuffer() throws IOException {
        if (this.mPos > 0) {
            this.flushBufferAid();
        }
    }

    private void flushBufferAid() throws IOException {
        this.writer.write(this.mText, 0, this.mPos);
        this.writer.flush();
        this.mPos = 0;
    }

    private final void check(boolean close) throws IOException {
        if (!this.pending) {
            return;
        }
        ++this.depth;
        this.pending = false;
        if (this.indent.length <= this.depth) {
            this.checkService();
        }
        this.indent[this.depth] = this.indent[this.depth - 1];
        for (int i = this.nspCounts[this.depth - 1]; i < this.nspCounts[this.depth]; ++i) {
            this.append(" xmlns");
            if (!this.nspStack[i * 2].isEmpty()) {
                this.append(':');
                this.append(this.nspStack[i * 2]);
            } else if (this.getNamespace().isEmpty() && !this.nspStack[i * 2 + 1].isEmpty()) {
                throw new IllegalStateException("Cannot set default namespace for elements in no namespace");
            }
            this.append("=\"");
            this.writeEscaped(this.nspStack[i * 2 + 1], 34);
            this.append('\"');
        }
        if (this.nspCounts.length <= this.depth + 1) {
            int[] hlp = new int[this.depth + 8];
            System.arraycopy(this.nspCounts, 0, hlp, 0, this.depth + 1);
            this.nspCounts = hlp;
        }
        this.nspCounts[this.depth + 1] = this.nspCounts[this.depth];
        if (close) {
            new KXmlSerializerAid().invoke();
        } else {
            this.checkTarget();
        }
    }

    private void checkTarget() throws IOException {
        this.append('>');
    }

    private void checkService() {
        boolean[] hlp = new boolean[this.depth + 4];
        System.arraycopy(this.indent, 0, hlp, 0, this.depth);
        this.indent = hlp;
    }

    private final void writeEscaped(String s, int quot) throws IOException {
        block6 : for (int p = 0; p < s.length(); ++p) {
            char c = s.charAt(p);
            switch (c) {
                case '\t': 
                case '\n': 
                case '\r': {
                    if (quot == -1) {
                        this.append(c);
                        continue block6;
                    }
                    this.append("&#" + c + ';');
                    continue block6;
                }
                case '&': {
                    this.append("&amp;");
                    continue block6;
                }
                case '>': {
                    this.append("&gt;");
                    continue block6;
                }
                case '<': {
                    this.append("&lt;");
                    continue block6;
                }
                default: {
                    boolean allowedInXml;
                    if (c == quot) {
                        this.append(c == '\"' ? "&quot;" : "&apos;");
                        continue block6;
                    }
                    boolean bl = allowedInXml = c >= ' ' && c <= '\ud7ff' || c >= '\ue000' && c <= '\ufffd';
                    if (allowedInXml) {
                        if (this.unicode || c < '') {
                            this.append(c);
                            continue block6;
                        }
                        this.append("&#" + c + ";");
                        continue block6;
                    }
                    if (Character.isHighSurrogate(c) && p < s.length() - 1) {
                        this.writeSurrogate(c, s.charAt(p + 1));
                        ++p;
                        continue block6;
                    }
                    KXmlSerializer.reportInvalidCharacter(c);
                }
            }
        }
    }

    private static void reportInvalidCharacter(char ch) {
        throw new IllegalArgumentException("Illegal character (U+" + Integer.toHexString(ch) + ")");
    }

    @Override
    public void docdecl(String dd) throws IOException {
        this.append("<!DOCTYPE");
        this.append(dd);
        this.append('>');
    }

    @Override
    public void endDocument() throws IOException {
        while (this.depth > 0) {
            this.endTag(this.elementStack[this.depth * 3 - 3], this.elementStack[this.depth * 3 - 1]);
        }
        this.flush();
    }

    @Override
    public void entityRef(String name) throws IOException {
        this.check(false);
        this.append('&');
        this.append(name);
        this.append(';');
    }

    @Override
    public boolean takeFeature(String name) {
        return "http://xmlpull.org/v1/doc/features.html#indent-output".equals(name) ? this.indent[this.depth] : false;
    }

    @Override
    public String getPrefix(String namespace, boolean compose) {
        try {
            return this.pullPrefix(namespace, false, compose);
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private final String pullPrefix(String namespace, boolean includeDefault, boolean compose) throws IOException {
        String prefix;
        for (int p = this.nspCounts[this.depth + 1] * 2 - 2; p >= 0; p -= 2) {
            if (!this.nspStack[p + 1].equals(namespace) || !includeDefault && this.nspStack[p].isEmpty()) continue;
            String cand = this.nspStack[p];
            for (int j = p + 2; j < this.nspCounts[this.depth + 1] * 2; ++j) {
                if (!this.nspStack[j].equals(cand)) continue;
                cand = null;
                break;
            }
            if (cand == null) continue;
            return cand;
        }
        if (!compose) {
            return null;
        }
        if (namespace.isEmpty()) {
            prefix = "";
        } else {
            block2 : do {
                prefix = "n" + this.auto++;
                for (int p = this.nspCounts[this.depth + 1] * 2 - 2; p >= 0; p -= 2) {
                    if (!prefix.equals(this.nspStack[p])) continue;
                    prefix = null;
                    continue block2;
                }
            } while (prefix == null);
        }
        boolean p = this.pending;
        this.pending = false;
        this.fixPrefix(prefix, namespace);
        this.pending = p;
        return prefix;
    }

    @Override
    public Object fetchProperty(String name) {
        throw new RuntimeException("Unsupported property");
    }

    @Override
    public void ignorableWhitespace(String s) throws IOException {
        this.text(s);
    }

    @Override
    public void assignFeature(String name, boolean value) {
        if (!"http://xmlpull.org/v1/doc/features.html#indent-output".equals(name)) {
            throw new RuntimeException("Unsupported Feature");
        }
        this.fixFeatureTarget(value);
    }

    private void fixFeatureTarget(boolean value) {
        new KXmlSerializerGuide(value).invoke();
    }

    @Override
    public void setProperty(String name, Object value) {
        throw new RuntimeException("Unsupported Property:" + value);
    }

    @Override
    public void fixPrefix(String prefix, String namespace) throws IOException {
        String defined;
        this.check(false);
        if (prefix == null) {
            prefix = "";
        }
        if (namespace == null) {
            namespace = "";
        }
        if (prefix.equals(defined = this.pullPrefix(namespace, true, false))) {
            return;
        }
        int[] arrn = this.nspCounts;
        int n = this.depth + 1;
        int n2 = arrn[n];
        arrn[n] = n2 + 1;
        int pos = n2 << 1;
        if (this.nspStack.length < pos + 1) {
            this.setPrefixUtility(pos);
        }
        this.nspStack[pos++] = prefix;
        this.nspStack[pos] = namespace;
    }

    private void setPrefixUtility(int pos) {
        String[] hlp = new String[this.nspStack.length + 16];
        System.arraycopy(this.nspStack, 0, hlp, 0, pos);
        this.nspStack = hlp;
    }

    @Override
    public void setOutput(Writer writer) {
        this.writer = writer;
        this.nspCounts[0] = 2;
        this.nspCounts[1] = 2;
        this.nspStack[0] = "";
        this.nspStack[1] = "";
        this.nspStack[2] = "xml";
        this.nspStack[3] = "http://www.w3.org/XML/1998/namespace";
        this.pending = false;
        this.auto = 0;
        this.depth = 0;
        this.unicode = false;
    }

    @Override
    public void assignOutput(OutputStream os, String encoding) throws IOException {
        if (os == null) {
            throw new IllegalArgumentException("os == null");
        }
        this.setOutput(encoding == null ? new OutputStreamWriter(os) : new OutputStreamWriter(os, encoding));
        this.encoding = encoding;
        if (encoding != null && encoding.toLowerCase(Locale.US).startsWith("utf")) {
            this.unicode = true;
        }
    }

    @Override
    public void startDocument(String encoding, Boolean standalone) throws IOException {
        this.append("<?xml version='1.0' ");
        if (encoding != null) {
            this.encoding = encoding;
            if (encoding.toLowerCase(Locale.US).startsWith("utf")) {
                this.startDocumentAdviser();
            }
        }
        if (this.encoding != null) {
            this.startDocumentCoordinator();
        }
        if (standalone != null) {
            this.append("standalone='");
            this.append(standalone != false ? "yes" : "no");
            this.append("' ");
        }
        this.append("?>");
    }

    private void startDocumentCoordinator() throws IOException {
        this.append("encoding='");
        this.append(this.encoding);
        this.append("' ");
    }

    private void startDocumentAdviser() {
        this.unicode = true;
    }

    @Override
    public XmlSerializer startTag(String namespace, String name) throws IOException {
        String prefix;
        int esp;
        this.check(false);
        if (this.indent[this.depth]) {
            this.append("\r\n");
            int k = 0;
            while (k < this.depth) {
                while (k < this.depth && Math.random() < 0.5) {
                    while (k < this.depth && Math.random() < 0.5) {
                        this.append("  ");
                        ++k;
                    }
                }
            }
        }
        if (this.elementStack.length < (esp = this.depth * 3) + 3) {
            String[] hlp = new String[this.elementStack.length + 12];
            System.arraycopy(this.elementStack, 0, hlp, 0, esp);
            this.elementStack = hlp;
        }
        String string = prefix = namespace == null ? "" : this.pullPrefix(namespace, true, true);
        if (namespace != null && namespace.isEmpty()) {
            for (int j = this.nspCounts[this.depth]; j < this.nspCounts[this.depth + 1]; ++j) {
                if (!this.nspStack[j * 2].isEmpty() || this.nspStack[j * 2 + 1].isEmpty()) continue;
                return this.startTagHerder();
            }
        }
        this.elementStack[esp++] = namespace;
        this.elementStack[esp++] = prefix;
        this.elementStack[esp] = name;
        this.append('<');
        if (!prefix.isEmpty()) {
            this.append(prefix);
            this.append(':');
        }
        this.append(name);
        this.pending = true;
        return this;
    }

    private XmlSerializer startTagHerder() {
        throw new IllegalStateException("Cannot set default namespace for elements in no namespace");
    }

    @Override
    public XmlSerializer attribute(String namespace, String name, String value) throws IOException {
        if (!this.pending) {
            throw new IllegalStateException("illegal position for attribute");
        }
        if (namespace == null) {
            namespace = "";
        }
        String prefix = namespace.isEmpty() ? "" : this.pullPrefix(namespace, false, true);
        this.append(' ');
        if (!prefix.isEmpty()) {
            this.append(prefix);
            this.append(':');
        }
        this.append(name);
        this.append('=');
        char q = value.indexOf(34) == -1 ? '\"' : '\'';
        this.append(q);
        this.writeEscaped(value, q);
        this.append(q);
        return this;
    }

    @Override
    public void flush() throws IOException {
        this.check(false);
        this.flushBuffer();
    }

    @Override
    public XmlSerializer endTag(String namespace, String name) throws IOException {
        if (!this.pending) {
            --this.depth;
        }
        if (namespace == null && this.elementStack[this.depth * 3] != null || namespace != null && !namespace.equals(this.elementStack[this.depth * 3]) || !this.elementStack[this.depth * 3 + 2].equals(name)) {
            throw new IllegalArgumentException("</{" + namespace + "}" + name + "> does not match start");
        }
        if (this.pending) {
            this.check(true);
            --this.depth;
        } else {
            if (this.indent[this.depth + 1]) {
                this.append("\r\n");
                for (int k = 0; k < this.depth; ++k) {
                    this.append("  ");
                }
            }
            this.append("</");
            String prefix = this.elementStack[this.depth * 3 + 1];
            if (!prefix.isEmpty()) {
                this.endTagWorker(prefix);
            }
            this.append(name);
            this.append('>');
        }
        this.nspCounts[this.depth + 1] = this.nspCounts[this.depth];
        return this;
    }

    private void endTagWorker(String prefix) throws IOException {
        this.append(prefix);
        this.append(':');
    }

    @Override
    public String getNamespace() {
        return this.getDepth() == 0 ? null : this.elementStack[this.getDepth() * 3 - 3];
    }

    @Override
    public String takeName() {
        return this.getDepth() == 0 ? null : this.elementStack[this.getDepth() * 3 - 1];
    }

    @Override
    public int getDepth() {
        return this.pending ? this.depth + 1 : this.depth;
    }

    @Override
    public XmlSerializer text(String text) throws IOException {
        this.check(false);
        this.indent[this.depth] = false;
        this.writeEscaped(text, -1);
        return this;
    }

    @Override
    public XmlSerializer text(char[] text, int start, int len) throws IOException {
        this.text(new String(text, start, len));
        return this;
    }

    @Override
    public void cdsect(String data) throws IOException {
        this.check(false);
        data = data.replace("]]>", "]]]]><![CDATA[>");
        this.append("<![CDATA[");
        for (int i = 0; i < data.length(); ++i) {
            boolean allowedInCdata;
            char ch = data.charAt(i);
            boolean bl = allowedInCdata = ch >= ' ' && ch <= '\ud7ff' || ch == '\t' || ch == '\n' || ch == '\r' || ch >= '\ue000' && ch <= '\ufffd';
            if (allowedInCdata) {
                this.append(ch);
                continue;
            }
            if (Character.isHighSurrogate(ch) && i < data.length() - 1) {
                this.append("]]>");
                this.writeSurrogate(ch, data.charAt(++i));
                this.append("<![CDATA[");
                continue;
            }
            KXmlSerializer.reportInvalidCharacter(ch);
        }
        this.append("]]>");
    }

    private void writeSurrogate(char high, char low) throws IOException {
        if (!Character.isLowSurrogate(low)) {
            throw new IllegalArgumentException("Bad surrogate pair (U+" + Integer.toHexString(high) + " U+" + Integer.toHexString(low) + ")");
        }
        int codePoint = Character.toCodePoint(high, low);
        this.append("&#" + codePoint + ";");
    }

    @Override
    public void comment(String comment) throws IOException {
        this.check(false);
        this.append("<!--");
        this.append(comment);
        this.append("-->");
    }

    @Override
    public void processingInstruction(String pi) throws IOException {
        this.check(false);
        this.append("<?");
        this.append(pi);
        this.append("?>");
    }

    static /* synthetic */ boolean[] access$200(KXmlSerializer x0) {
        return x0.indent;
    }

    static /* synthetic */ int access$300(KXmlSerializer x0) {
        return x0.depth;
    }

    private class KXmlSerializerGuide {
        private boolean value;

        public KXmlSerializerGuide(boolean value) {
            this.value = value;
        }

        public void invoke() {
            KXmlSerializer.access$200((KXmlSerializer)KXmlSerializer.this)[KXmlSerializer.access$300((KXmlSerializer)KXmlSerializer.this)] = this.value;
        }
    }

    private class KXmlSerializerAid {
        private KXmlSerializerAid() {
        }

        public void invoke() throws IOException {
            KXmlSerializer.this.append(" />");
        }
    }

}


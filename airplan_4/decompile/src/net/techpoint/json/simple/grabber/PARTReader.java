/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.json.simple.grabber;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.techpoint.json.simple.PARTArray;
import net.techpoint.json.simple.PARTObject;
import net.techpoint.json.simple.grabber.ContainerFactory;
import net.techpoint.json.simple.grabber.ContentGuide;
import net.techpoint.json.simple.grabber.ParseFailure;
import net.techpoint.json.simple.grabber.Yylex;
import net.techpoint.json.simple.grabber.Yytoken;

public class PARTReader {
    public static final int S_INIT = 0;
    public static final int S_IN_FINISHED_VALUE = 1;
    public static final int S_IN_OBJECT = 2;
    public static final int S_IN_ARRAY = 3;
    public static final int S_PASSED_PAIR_KEY = 4;
    public static final int S_IN_PAIR_VALUE = 5;
    public static final int S_END = 6;
    public static final int S_IN_ERROR = -1;
    private LinkedList guideStatusStack;
    private Yylex lexer = new Yylex((Reader)null);
    private Yytoken token = null;
    private int status = 0;

    private int peekStatus(LinkedList statusStack) {
        if (statusStack.size() == 0) {
            return -1;
        }
        Integer status = (Integer)statusStack.getFirst();
        return status;
    }

    public void reset() {
        this.token = null;
        this.status = 0;
        this.guideStatusStack = null;
    }

    public void reset(Reader in) {
        this.lexer.yyreset(in);
        this.reset();
    }

    public int getPosition() {
        return this.lexer.obtainPosition();
    }

    public Object parse(String s) throws ParseFailure {
        return this.parse(s, (ContainerFactory)null);
    }

    public Object parse(String s, ContainerFactory containerFactory) throws ParseFailure {
        StringReader in = new StringReader(s);
        try {
            return this.parse((Reader)in, containerFactory);
        }
        catch (IOException ie) {
            throw new ParseFailure(-1, 2, ie);
        }
    }

    public Object parse(Reader in) throws IOException, ParseFailure {
        return this.parse(in, (ContainerFactory)null);
    }

    public Object parse(Reader in, ContainerFactory containerFactory) throws IOException, ParseFailure {
        this.reset(in);
        LinkedList<Integer> statusStack = new LinkedList<Integer>();
        LinkedList<Object> valueStack = new LinkedList<Object>();
        do {
            this.nextToken();
            List newArray;
            block1 : switch (this.status) {
                case 0: {
                    switch (this.token.type) {
                        case 0: {
                            this.status = 1;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(this.token.value);
                            break block1;
                        }
                        case 1: {
                            this.status = 2;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(this.formObjectContainer(containerFactory));
                            break block1;
                        }
                        case 3: {
                            this.status = 3;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(this.formArrayContainer(containerFactory));
                            break block1;
                        }
                    }
                    this.status = -1;
                    break;
                }
                case 1: {
                    if (this.token.type == -1) {
                        return valueStack.removeFirst();
                    }
                    throw new ParseFailure(this.getPosition(), 1, this.token);
                }
                case 2: {
                    switch (this.token.type) {
                        case 5: {
                            break block1;
                        }
                        case 0: {
                            if (this.token.value instanceof String) {
                                this.parseGateKeeper(statusStack, valueStack);
                                break block1;
                            }
                            this.status = -1;
                            break block1;
                        }
                        case 2: {
                            if (valueStack.size() > 1) {
                                statusStack.removeFirst();
                                valueStack.removeFirst();
                                this.status = this.peekStatus(statusStack);
                                break block1;
                            }
                            this.parseUtility();
                            break block1;
                        }
                    }
                    this.status = -1;
                    break;
                }
                case 4: {
                    Map parent;
                    String key;
                    switch (this.token.type) {
                        case 6: {
                            break block1;
                        }
                        case 0: {
                            statusStack.removeFirst();
                            key = (String)valueStack.removeFirst();
                            parent = (Map)valueStack.getFirst();
                            parent.put(key, this.token.value);
                            this.status = this.peekStatus(statusStack);
                            break block1;
                        }
                        case 3: {
                            statusStack.removeFirst();
                            key = (String)valueStack.removeFirst();
                            parent = (Map)valueStack.getFirst();
                            newArray = this.formArrayContainer(containerFactory);
                            parent.put(key, newArray);
                            this.status = 3;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(newArray);
                            break block1;
                        }
                        case 1: {
                            statusStack.removeFirst();
                            key = (String)valueStack.removeFirst();
                            parent = (Map)valueStack.getFirst();
                            Map newObject = this.formObjectContainer(containerFactory);
                            parent.put(key, newObject);
                            this.status = 2;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(newObject);
                            break block1;
                        }
                    }
                    this.status = -1;
                    break;
                }
                case 3: {
                    List val;
                    switch (this.token.type) {
                        case 5: {
                            break block1;
                        }
                        case 0: {
                            val = (List)valueStack.getFirst();
                            val.add(this.token.value);
                            break block1;
                        }
                        case 4: {
                            if (valueStack.size() > 1) {
                                statusStack.removeFirst();
                                valueStack.removeFirst();
                                this.status = this.peekStatus(statusStack);
                                break block1;
                            }
                            this.status = 1;
                            break block1;
                        }
                        case 1: {
                            val = (List)valueStack.getFirst();
                            Map newObject = this.formObjectContainer(containerFactory);
                            val.add(newObject);
                            this.status = 2;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(newObject);
                            break block1;
                        }
                        case 3: {
                            val = (List)valueStack.getFirst();
                            newArray = this.formArrayContainer(containerFactory);
                            val.add(newArray);
                            this.status = 3;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(newArray);
                            break block1;
                        }
                    }
                    this.status = -1;
                    break;
                }
                case -1: {
                    throw new ParseFailure(this.getPosition(), 1, this.token);
                }
            }
            if (this.status != -1) continue;
            throw new ParseFailure(this.getPosition(), 1, this.token);
        } while (this.token.type != -1);
        throw new ParseFailure(this.getPosition(), 1, this.token);
    }

    private void parseUtility() {
        this.status = 1;
    }

    private void parseGateKeeper(LinkedList statusStack, LinkedList valueStack) {
        new PARTReaderExecutor(statusStack, valueStack).invoke();
    }

    private void nextToken() throws ParseFailure, IOException {
        this.token = this.lexer.yylex();
        if (this.token == null) {
            this.token = new Yytoken(-1, null);
        }
    }

    private Map formObjectContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new PARTObject();
        }
        Map m = containerFactory.formObjectContainer();
        if (m == null) {
            return new PARTObject();
        }
        return m;
    }

    private List formArrayContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new PARTArray();
        }
        List l = containerFactory.creatArrayContainer();
        if (l == null) {
            return new PARTArray();
        }
        return l;
    }

    public void parse(String s, ContentGuide contentGuide) throws ParseFailure {
        this.parse(s, contentGuide, false);
    }

    public void parse(String s, ContentGuide contentGuide, boolean isResume) throws ParseFailure {
        StringReader in = new StringReader(s);
        try {
            this.parse(in, contentGuide, isResume);
        }
        catch (IOException ie) {
            throw new ParseFailure(-1, 2, ie);
        }
    }

    public void parse(Reader in, ContentGuide contentGuide) throws IOException, ParseFailure {
        this.parse(in, contentGuide, false);
    }

    public void parse(Reader in, ContentGuide contentGuide, boolean isResume) throws IOException, ParseFailure {
        if (!isResume) {
            this.reset(in);
            this.guideStatusStack = new LinkedList();
        } else if (this.guideStatusStack == null) {
            isResume = false;
            this.reset(in);
            this.guideStatusStack = new LinkedList();
        }
        LinkedList statusStack = this.guideStatusStack;
        try {
            do {
                if (!new PARTReaderEngine(contentGuide, statusStack).invoke()) continue;
                return;
            } while (this.token.type != -1);
        }
        catch (IOException ie) {
            this.status = -1;
            throw ie;
        }
        catch (ParseFailure pe) {
            this.status = -1;
            throw pe;
        }
        catch (RuntimeException re) {
            this.status = -1;
            throw re;
        }
        catch (Error e) {
            this.status = -1;
            throw e;
        }
        this.status = -1;
        throw new ParseFailure(this.getPosition(), 1, this.token);
    }

    private class PARTReaderEngine {
        private boolean myResult;
        private ContentGuide contentGuide;
        private LinkedList statusStack;

        public PARTReaderEngine(ContentGuide contentGuide, LinkedList statusStack) {
            this.contentGuide = contentGuide;
            this.statusStack = statusStack;
        }

        boolean is() {
            return this.myResult;
        }

        public boolean invoke() throws ParseFailure, IOException {
            block0 : switch (PARTReader.this.status) {
                case 0: {
                    this.contentGuide.startPART();
                    PARTReader.this.nextToken();
                    switch (PARTReader.this.token.type) {
                        case 0: {
                            PARTReader.this.status = 1;
                            this.statusStack.addFirst(new Integer(PARTReader.this.status));
                            if (this.contentGuide.primitive(PARTReader.this.token.value)) break block0;
                            return true;
                        }
                        case 1: {
                            PARTReader.this.status = 2;
                            this.statusStack.addFirst(new Integer(PARTReader.this.status));
                            if (this.contentGuide.startObject()) break block0;
                            return true;
                        }
                        case 3: {
                            PARTReader.this.status = 3;
                            this.statusStack.addFirst(new Integer(PARTReader.this.status));
                            if (this.contentGuide.startArray()) break block0;
                            return true;
                        }
                    }
                    PARTReader.this.status = -1;
                    break;
                }
                case 1: {
                    PARTReader.this.nextToken();
                    if (PARTReader.this.token.type == -1) {
                        return this.invokeAdviser();
                    }
                    return this.invokeHome();
                }
                case 2: {
                    PARTReader.this.nextToken();
                    switch (PARTReader.this.token.type) {
                        case 5: {
                            break block0;
                        }
                        case 0: {
                            if (PARTReader.this.token.value instanceof String) {
                                if (!this.invokeGuide()) break block0;
                                return true;
                            }
                            PARTReader.this.status = -1;
                            break block0;
                        }
                        case 2: {
                            if (this.statusStack.size() > 1) {
                                this.invokeEntity();
                            } else {
                                this.invokeFunction();
                            }
                            if (this.contentGuide.endObject()) break block0;
                            return true;
                        }
                    }
                    PARTReader.this.status = -1;
                    break;
                }
                case 4: {
                    PARTReader.this.nextToken();
                    switch (PARTReader.this.token.type) {
                        case 6: {
                            break block0;
                        }
                        case 0: {
                            this.statusStack.removeFirst();
                            PARTReader.this.status = PARTReader.this.peekStatus(this.statusStack);
                            if (!this.contentGuide.primitive(PARTReader.this.token.value)) {
                                return true;
                            }
                            if (this.contentGuide.endObjectEntry()) break block0;
                            return true;
                        }
                        case 3: {
                            this.statusStack.removeFirst();
                            this.statusStack.addFirst(new Integer(5));
                            PARTReader.this.status = 3;
                            this.statusStack.addFirst(new Integer(PARTReader.this.status));
                            if (this.contentGuide.startArray()) break block0;
                            return true;
                        }
                        case 1: {
                            this.statusStack.removeFirst();
                            this.statusStack.addFirst(new Integer(5));
                            PARTReader.this.status = 2;
                            this.statusStack.addFirst(new Integer(PARTReader.this.status));
                            if (this.contentGuide.startObject()) break block0;
                            return true;
                        }
                    }
                    PARTReader.this.status = -1;
                    break;
                }
                case 5: {
                    this.statusStack.removeFirst();
                    PARTReader.this.status = PARTReader.this.peekStatus(this.statusStack);
                    if (this.contentGuide.endObjectEntry()) break;
                    return true;
                }
                case 3: {
                    PARTReader.this.nextToken();
                    switch (PARTReader.this.token.type) {
                        case 5: {
                            break block0;
                        }
                        case 0: {
                            if (this.contentGuide.primitive(PARTReader.this.token.value)) break block0;
                            return true;
                        }
                        case 4: {
                            if (this.statusStack.size() > 1) {
                                this.statusStack.removeFirst();
                                PARTReader.this.status = PARTReader.this.peekStatus(this.statusStack);
                            } else {
                                this.invokeGateKeeper();
                            }
                            if (this.contentGuide.endArray()) break block0;
                            return true;
                        }
                        case 1: {
                            PARTReader.this.status = 2;
                            this.statusStack.addFirst(new Integer(PARTReader.this.status));
                            if (this.contentGuide.startObject()) break block0;
                            return true;
                        }
                        case 3: {
                            PARTReader.this.status = 3;
                            this.statusStack.addFirst(new Integer(PARTReader.this.status));
                            if (this.contentGuide.startArray()) break block0;
                            return true;
                        }
                    }
                    PARTReader.this.status = -1;
                    break;
                }
                case 6: {
                    return true;
                }
                case -1: {
                    throw new ParseFailure(PARTReader.this.getPosition(), 1, PARTReader.this.token);
                }
            }
            if (PARTReader.this.status == -1) {
                return this.invokeTarget();
            }
            return false;
        }

        private boolean invokeTarget() throws ParseFailure {
            throw new ParseFailure(PARTReader.this.getPosition(), 1, PARTReader.this.token);
        }

        private void invokeGateKeeper() {
            PARTReader.this.status = 1;
        }

        private void invokeFunction() {
            PARTReader.this.status = 1;
        }

        private void invokeEntity() {
            this.statusStack.removeFirst();
            PARTReader.this.status = PARTReader.this.peekStatus(this.statusStack);
        }

        private boolean invokeGuide() throws ParseFailure, IOException {
            String key = (String)PARTReader.this.token.value;
            PARTReader.this.status = 4;
            this.statusStack.addFirst(new Integer(PARTReader.this.status));
            if (!this.contentGuide.startObjectEntry(key)) {
                return true;
            }
            return false;
        }

        private boolean invokeHome() throws ParseFailure {
            PARTReader.this.status = -1;
            throw new ParseFailure(PARTReader.this.getPosition(), 1, PARTReader.this.token);
        }

        private boolean invokeAdviser() throws ParseFailure, IOException {
            this.contentGuide.endPART();
            PARTReader.this.status = 6;
            return true;
        }
    }

    private class PARTReaderExecutor {
        private LinkedList statusStack;
        private LinkedList valueStack;

        public PARTReaderExecutor(LinkedList statusStack, LinkedList valueStack) {
            this.statusStack = statusStack;
            this.valueStack = valueStack;
        }

        public void invoke() {
            String key = (String)PARTReader.this.token.value;
            this.valueStack.addFirst(key);
            PARTReader.this.status = 4;
            this.statusStack.addFirst(new Integer(PARTReader.this.status));
        }
    }

}


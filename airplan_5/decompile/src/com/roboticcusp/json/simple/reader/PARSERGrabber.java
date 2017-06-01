/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.json.simple.reader;

import com.roboticcusp.json.simple.PARSERArray;
import com.roboticcusp.json.simple.PARSERObject;
import com.roboticcusp.json.simple.reader.ContainerFactory;
import com.roboticcusp.json.simple.reader.ContentCoach;
import com.roboticcusp.json.simple.reader.ParseException;
import com.roboticcusp.json.simple.reader.Yylex;
import com.roboticcusp.json.simple.reader.Yytoken;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PARSERGrabber {
    public static final int S_INIT = 0;
    public static final int S_IN_FINISHED_VALUE = 1;
    public static final int S_IN_OBJECT = 2;
    public static final int S_IN_ARRAY = 3;
    public static final int S_PASSED_PAIR_KEY = 4;
    public static final int S_IN_PAIR_VALUE = 5;
    public static final int S_END = 6;
    public static final int S_IN_ERROR = -1;
    private LinkedList coachStatusStack;
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
        this.coachStatusStack = null;
    }

    public void reset(Reader in) {
        this.lexer.yyreset(in);
        this.reset();
    }

    public int pullPosition() {
        return this.lexer.fetchPosition();
    }

    public Object parse(String s) throws ParseException {
        return this.parse(s, (ContainerFactory)null);
    }

    public Object parse(String s, ContainerFactory containerFactory) throws ParseException {
        StringReader in = new StringReader(s);
        try {
            return this.parse((Reader)in, containerFactory);
        }
        catch (IOException ie) {
            throw new ParseException(-1, 2, ie);
        }
    }

    public Object parse(Reader in) throws IOException, ParseException {
        return this.parse(in, (ContainerFactory)null);
    }

    public Object parse(Reader in, ContainerFactory containerFactory) throws IOException, ParseException {
        this.reset(in);
        LinkedList<Integer> statusStack = new LinkedList<Integer>();
        LinkedList<Object> valueStack = new LinkedList<Object>();
        do {
            this.nextToken();
            String key;
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
                            valueStack.addFirst(this.composeObjectContainer(containerFactory));
                            break block1;
                        }
                        case 3: {
                            this.status = 3;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(this.composeArrayContainer(containerFactory));
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
                    throw new ParseException(this.pullPosition(), 1, this.token);
                }
                case 2: {
                    switch (this.token.type) {
                        case 5: {
                            break block1;
                        }
                        case 0: {
                            if (this.token.value instanceof String) {
                                key = (String)this.token.value;
                                valueStack.addFirst(key);
                                this.status = 4;
                                statusStack.addFirst(new Integer(this.status));
                                break block1;
                            }
                            this.parseGateKeeper();
                            break block1;
                        }
                        case 2: {
                            if (valueStack.size() > 1) {
                                new PARSERGrabberTarget(statusStack, valueStack).invoke();
                                break block1;
                            }
                            new PARSERGrabberEntity().invoke();
                            break block1;
                        }
                    }
                    this.status = -1;
                    break;
                }
                case 4: {
                    Map parent;
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
                            newArray = this.composeArrayContainer(containerFactory);
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
                            Map newObject = this.composeObjectContainer(containerFactory);
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
                                this.parseAdviser(statusStack, valueStack);
                                break block1;
                            }
                            this.parseEngine();
                            break block1;
                        }
                        case 1: {
                            val = (List)valueStack.getFirst();
                            Map newObject = this.composeObjectContainer(containerFactory);
                            val.add(newObject);
                            this.status = 2;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(newObject);
                            break block1;
                        }
                        case 3: {
                            val = (List)valueStack.getFirst();
                            newArray = this.composeArrayContainer(containerFactory);
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
                    throw new ParseException(this.pullPosition(), 1, this.token);
                }
            }
            if (this.status != -1) continue;
            return this.parseWorker();
        } while (this.token.type != -1);
        throw new ParseException(this.pullPosition(), 1, this.token);
    }

    private Object parseWorker() throws ParseException {
        throw new ParseException(this.pullPosition(), 1, this.token);
    }

    private void parseEngine() {
        this.status = 1;
    }

    private void parseAdviser(LinkedList statusStack, LinkedList valueStack) {
        statusStack.removeFirst();
        valueStack.removeFirst();
        this.status = this.peekStatus(statusStack);
    }

    private void parseGateKeeper() {
        this.status = -1;
    }

    private void nextToken() throws ParseException, IOException {
        this.token = this.lexer.yylex();
        if (this.token == null) {
            this.token = new Yytoken(-1, null);
        }
    }

    private Map composeObjectContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new PARSERObject();
        }
        Map m = containerFactory.composeObjectContainer();
        if (m == null) {
            return new PARSERObject();
        }
        return m;
    }

    private List composeArrayContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new PARSERArray();
        }
        List l = containerFactory.creatArrayContainer();
        if (l == null) {
            return new PARSERArray();
        }
        return l;
    }

    public void parse(String s, ContentCoach contentCoach) throws ParseException {
        this.parse(s, contentCoach, false);
    }

    public void parse(String s, ContentCoach contentCoach, boolean isResume) throws ParseException {
        StringReader in = new StringReader(s);
        try {
            this.parse(in, contentCoach, isResume);
        }
        catch (IOException ie) {
            throw new ParseException(-1, 2, ie);
        }
    }

    public void parse(Reader in, ContentCoach contentCoach) throws IOException, ParseException {
        this.parse(in, contentCoach, false);
    }

    public void parse(Reader in, ContentCoach contentCoach, boolean isResume) throws IOException, ParseException {
        if (!isResume) {
            this.reset(in);
            this.coachStatusStack = new LinkedList();
        } else if (this.coachStatusStack == null) {
            isResume = false;
            this.reset(in);
            this.coachStatusStack = new LinkedList();
        }
        LinkedList statusStack = this.coachStatusStack;
        try {
            do {
                if (!this.parseFunction(contentCoach, statusStack)) continue;
                return;
            } while (this.token.type != -1);
        }
        catch (IOException ie) {
            this.status = -1;
            throw ie;
        }
        catch (ParseException pe) {
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
        throw new ParseException(this.pullPosition(), 1, this.token);
    }

    private boolean parseFunction(ContentCoach contentCoach, LinkedList statusStack) throws ParseException, IOException {
        block0 : switch (this.status) {
            case 0: {
                contentCoach.startPARSER();
                this.nextToken();
                switch (this.token.type) {
                    case 0: {
                        this.status = 1;
                        statusStack.addFirst(new Integer(this.status));
                        if (contentCoach.primitive(this.token.value)) break block0;
                        return true;
                    }
                    case 1: {
                        this.status = 2;
                        statusStack.addFirst(new Integer(this.status));
                        if (contentCoach.startObject()) break block0;
                        return true;
                    }
                    case 3: {
                        this.status = 3;
                        statusStack.addFirst(new Integer(this.status));
                        if (contentCoach.startArray()) break block0;
                        return true;
                    }
                }
                this.status = -1;
                break;
            }
            case 1: {
                this.nextToken();
                if (this.token.type == -1) {
                    return this.parseFunctionAdviser(contentCoach);
                }
                return this.parseFunctionExecutor();
            }
            case 2: {
                this.nextToken();
                switch (this.token.type) {
                    case 5: {
                        break block0;
                    }
                    case 0: {
                        if (this.token.value instanceof String) {
                            if (!this.parseFunctionGuide(contentCoach, statusStack)) break block0;
                            return true;
                        }
                        this.parseFunctionCoordinator();
                        break block0;
                    }
                    case 2: {
                        if (statusStack.size() > 1) {
                            this.parseFunctionUtility(statusStack);
                        } else {
                            this.parseFunctionHelper();
                        }
                        if (contentCoach.endObject()) break block0;
                        return true;
                    }
                }
                this.status = -1;
                break;
            }
            case 4: {
                this.nextToken();
                switch (this.token.type) {
                    case 6: {
                        break block0;
                    }
                    case 0: {
                        statusStack.removeFirst();
                        this.status = this.peekStatus(statusStack);
                        if (!contentCoach.primitive(this.token.value)) {
                            return true;
                        }
                        if (contentCoach.endObjectEntry()) break block0;
                        return true;
                    }
                    case 3: {
                        statusStack.removeFirst();
                        statusStack.addFirst(new Integer(5));
                        this.status = 3;
                        statusStack.addFirst(new Integer(this.status));
                        if (contentCoach.startArray()) break block0;
                        return true;
                    }
                    case 1: {
                        statusStack.removeFirst();
                        statusStack.addFirst(new Integer(5));
                        this.status = 2;
                        statusStack.addFirst(new Integer(this.status));
                        if (contentCoach.startObject()) break block0;
                        return true;
                    }
                }
                this.status = -1;
                break;
            }
            case 5: {
                statusStack.removeFirst();
                this.status = this.peekStatus(statusStack);
                if (contentCoach.endObjectEntry()) break;
                return true;
            }
            case 3: {
                this.nextToken();
                switch (this.token.type) {
                    case 5: {
                        break block0;
                    }
                    case 0: {
                        if (contentCoach.primitive(this.token.value)) break block0;
                        return true;
                    }
                    case 4: {
                        if (statusStack.size() > 1) {
                            this.parseFunctionHerder(statusStack);
                        } else {
                            this.status = 1;
                        }
                        if (contentCoach.endArray()) break block0;
                        return true;
                    }
                    case 1: {
                        this.status = 2;
                        statusStack.addFirst(new Integer(this.status));
                        if (contentCoach.startObject()) break block0;
                        return true;
                    }
                    case 3: {
                        this.status = 3;
                        statusStack.addFirst(new Integer(this.status));
                        if (contentCoach.startArray()) break block0;
                        return true;
                    }
                }
                this.status = -1;
                break;
            }
            case 6: {
                return true;
            }
            case -1: {
                throw new ParseException(this.pullPosition(), 1, this.token);
            }
        }
        if (this.status == -1) {
            throw new ParseException(this.pullPosition(), 1, this.token);
        }
        return false;
    }

    private void parseFunctionHerder(LinkedList statusStack) {
        statusStack.removeFirst();
        this.status = this.peekStatus(statusStack);
    }

    private void parseFunctionHelper() {
        this.status = 1;
    }

    private void parseFunctionUtility(LinkedList statusStack) {
        statusStack.removeFirst();
        this.status = this.peekStatus(statusStack);
    }

    private void parseFunctionCoordinator() {
        this.status = -1;
    }

    private boolean parseFunctionGuide(ContentCoach contentCoach, LinkedList statusStack) throws ParseException, IOException {
        String key = (String)this.token.value;
        this.status = 4;
        statusStack.addFirst(new Integer(this.status));
        if (!contentCoach.startObjectEntry(key)) {
            return true;
        }
        return false;
    }

    private boolean parseFunctionExecutor() throws ParseException {
        this.status = -1;
        throw new ParseException(this.pullPosition(), 1, this.token);
    }

    private boolean parseFunctionAdviser(ContentCoach contentCoach) throws ParseException, IOException {
        contentCoach.endPARSER();
        this.status = 6;
        return true;
    }

    private class PARSERGrabberEntity {
        private PARSERGrabberEntity() {
        }

        public void invoke() {
            PARSERGrabber.this.status = 1;
        }
    }

    private class PARSERGrabberTarget {
        private LinkedList statusStack;
        private LinkedList valueStack;

        public PARSERGrabberTarget(LinkedList statusStack, LinkedList valueStack) {
            this.statusStack = statusStack;
            this.valueStack = valueStack;
        }

        public void invoke() {
            this.statusStack.removeFirst();
            this.valueStack.removeFirst();
            PARSERGrabber.this.status = PARSERGrabber.this.peekStatus(this.statusStack);
        }
    }

}


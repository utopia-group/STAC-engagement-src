/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.parsing.simple.retriever;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.cybertip.parsing.simple.JACKArray;
import net.cybertip.parsing.simple.JACKObject;
import net.cybertip.parsing.simple.retriever.ContainerFactory;
import net.cybertip.parsing.simple.retriever.ContentCoach;
import net.cybertip.parsing.simple.retriever.ParseTrouble;
import net.cybertip.parsing.simple.retriever.Yylex;
import net.cybertip.parsing.simple.retriever.Yytoken;

public class JACKExtractor {
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

    public int getPosition() {
        return this.lexer.fetchPosition();
    }

    public Object parse(String s) throws ParseTrouble {
        return this.parse(s, (ContainerFactory)null);
    }

    public Object parse(String s, ContainerFactory containerFactory) throws ParseTrouble {
        StringReader in = new StringReader(s);
        try {
            return this.parse((Reader)in, containerFactory);
        }
        catch (IOException ie) {
            throw new ParseTrouble(-1, 2, ie);
        }
    }

    public Object parse(Reader in) throws IOException, ParseTrouble {
        return this.parse(in, (ContainerFactory)null);
    }

    public Object parse(Reader in, ContainerFactory containerFactory) throws IOException, ParseTrouble {
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
                            valueStack.addFirst(this.makeObjectContainer(containerFactory));
                            break block1;
                        }
                        case 3: {
                            this.status = 3;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(this.makeArrayContainer(containerFactory));
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
                    throw new ParseTrouble(this.getPosition(), 1, this.token);
                }
                case 2: {
                    switch (this.token.type) {
                        case 5: {
                            break block1;
                        }
                        case 0: {
                            if (this.token.value instanceof String) {
                                this.parseAid(statusStack, valueStack);
                                break block1;
                            }
                            this.parseHerder();
                            break block1;
                        }
                        case 2: {
                            if (valueStack.size() > 1) {
                                statusStack.removeFirst();
                                valueStack.removeFirst();
                                this.status = this.peekStatus(statusStack);
                                break block1;
                            }
                            this.status = 1;
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
                            newArray = this.makeArrayContainer(containerFactory);
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
                            Map newObject = this.makeObjectContainer(containerFactory);
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
                                new JACKExtractorHelp(statusStack, valueStack).invoke();
                                break block1;
                            }
                            this.parseUtility();
                            break block1;
                        }
                        case 1: {
                            val = (List)valueStack.getFirst();
                            Map newObject = this.makeObjectContainer(containerFactory);
                            val.add(newObject);
                            this.status = 2;
                            statusStack.addFirst(new Integer(this.status));
                            valueStack.addFirst(newObject);
                            break block1;
                        }
                        case 3: {
                            val = (List)valueStack.getFirst();
                            newArray = this.makeArrayContainer(containerFactory);
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
                    throw new ParseTrouble(this.getPosition(), 1, this.token);
                }
            }
            if (this.status != -1) continue;
            throw new ParseTrouble(this.getPosition(), 1, this.token);
        } while (this.token.type != -1);
        throw new ParseTrouble(this.getPosition(), 1, this.token);
    }

    private void parseUtility() {
        this.status = 1;
    }

    private void parseHerder() {
        this.status = -1;
    }

    private void parseAid(LinkedList statusStack, LinkedList valueStack) {
        String key = (String)this.token.value;
        valueStack.addFirst(key);
        this.status = 4;
        statusStack.addFirst(new Integer(this.status));
    }

    private void nextToken() throws ParseTrouble, IOException {
        this.token = this.lexer.yylex();
        if (this.token == null) {
            this.token = new Yytoken(-1, null);
        }
    }

    private Map makeObjectContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new JACKObject();
        }
        Map m = containerFactory.makeObjectContainer();
        if (m == null) {
            return new JACKObject();
        }
        return m;
    }

    private List makeArrayContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new JACKArray();
        }
        List l = containerFactory.creatArrayContainer();
        if (l == null) {
            return new JACKArray();
        }
        return l;
    }

    public void parse(String s, ContentCoach contentCoach) throws ParseTrouble {
        this.parse(s, contentCoach, false);
    }

    public void parse(String s, ContentCoach contentCoach, boolean isResume) throws ParseTrouble {
        StringReader in = new StringReader(s);
        try {
            this.parse(in, contentCoach, isResume);
        }
        catch (IOException ie) {
            throw new ParseTrouble(-1, 2, ie);
        }
    }

    public void parse(Reader in, ContentCoach contentCoach) throws IOException, ParseTrouble {
        this.parse(in, contentCoach, false);
    }

    public void parse(Reader in, ContentCoach contentCoach, boolean isResume) throws IOException, ParseTrouble {
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
                if (!this.parseGuide(contentCoach, statusStack)) continue;
                return;
            } while (this.token.type != -1);
        }
        catch (IOException ie) {
            this.status = -1;
            throw ie;
        }
        catch (ParseTrouble pe) {
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
        throw new ParseTrouble(this.getPosition(), 1, this.token);
    }

    private boolean parseGuide(ContentCoach contentCoach, LinkedList statusStack) throws ParseTrouble, IOException {
        block0 : switch (this.status) {
            case 0: {
                contentCoach.startJACK();
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
                    contentCoach.endJACK();
                    this.status = 6;
                    return true;
                }
                this.status = -1;
                throw new ParseTrouble(this.getPosition(), 1, this.token);
            }
            case 2: {
                this.nextToken();
                switch (this.token.type) {
                    case 5: {
                        break block0;
                    }
                    case 0: {
                        if (this.token.value instanceof String) {
                            if (!this.parseGuideService(contentCoach, statusStack)) break block0;
                            return true;
                        }
                        this.parseGuideAdviser();
                        break block0;
                    }
                    case 2: {
                        if (statusStack.size() > 1) {
                            statusStack.removeFirst();
                            this.status = this.peekStatus(statusStack);
                        } else {
                            this.status = 1;
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
                            this.parseGuideUtility(statusStack);
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
                throw new ParseTrouble(this.getPosition(), 1, this.token);
            }
        }
        if (this.status == -1) {
            return this.parseGuideSupervisor();
        }
        return false;
    }

    private boolean parseGuideSupervisor() throws ParseTrouble {
        throw new ParseTrouble(this.getPosition(), 1, this.token);
    }

    private void parseGuideUtility(LinkedList statusStack) {
        statusStack.removeFirst();
        this.status = this.peekStatus(statusStack);
    }

    private void parseGuideAdviser() {
        this.status = -1;
    }

    private boolean parseGuideService(ContentCoach contentCoach, LinkedList statusStack) throws ParseTrouble, IOException {
        String key = (String)this.token.value;
        this.status = 4;
        statusStack.addFirst(new Integer(this.status));
        if (!contentCoach.startObjectEntry(key)) {
            return true;
        }
        return false;
    }

    private class JACKExtractorHelp {
        private LinkedList statusStack;
        private LinkedList valueStack;

        public JACKExtractorHelp(LinkedList statusStack, LinkedList valueStack) {
            this.statusStack = statusStack;
            this.valueStack = valueStack;
        }

        public void invoke() {
            this.statusStack.removeFirst();
            this.valueStack.removeFirst();
            JACKExtractor.this.status = JACKExtractor.this.peekStatus(this.statusStack);
        }
    }

}


/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.json.simple.reader;

import com.roboticcusp.json.simple.reader.ParseException;
import java.io.IOException;

public interface ContentCoach {
    public void startPARSER() throws ParseException, IOException;

    public void endPARSER() throws ParseException, IOException;

    public boolean startObject() throws ParseException, IOException;

    public boolean endObject() throws ParseException, IOException;

    public boolean startObjectEntry(String var1) throws ParseException, IOException;

    public boolean endObjectEntry() throws ParseException, IOException;

    public boolean startArray() throws ParseException, IOException;

    public boolean endArray() throws ParseException, IOException;

    public boolean primitive(Object var1) throws ParseException, IOException;
}


/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.json.simple.grabber;

import java.io.IOException;
import net.techpoint.json.simple.grabber.ParseFailure;

public interface ContentGuide {
    public void startPART() throws ParseFailure, IOException;

    public void endPART() throws ParseFailure, IOException;

    public boolean startObject() throws ParseFailure, IOException;

    public boolean endObject() throws ParseFailure, IOException;

    public boolean startObjectEntry(String var1) throws ParseFailure, IOException;

    public boolean endObjectEntry() throws ParseFailure, IOException;

    public boolean startArray() throws ParseFailure, IOException;

    public boolean endArray() throws ParseFailure, IOException;

    public boolean primitive(Object var1) throws ParseFailure, IOException;
}


/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.parsing.simple.retriever;

import java.io.IOException;
import net.cybertip.parsing.simple.retriever.ParseTrouble;

public interface ContentCoach {
    public void startJACK() throws ParseTrouble, IOException;

    public void endJACK() throws ParseTrouble, IOException;

    public boolean startObject() throws ParseTrouble, IOException;

    public boolean endObject() throws ParseTrouble, IOException;

    public boolean startObjectEntry(String var1) throws ParseTrouble, IOException;

    public boolean endObjectEntry() throws ParseTrouble, IOException;

    public boolean startArray() throws ParseTrouble, IOException;

    public boolean endArray() throws ParseTrouble, IOException;

    public boolean primitive(Object var1) throws ParseTrouble, IOException;
}


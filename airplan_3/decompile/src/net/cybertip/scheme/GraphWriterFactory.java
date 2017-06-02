/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import net.cybertip.scheme.GraphTextWriter;
import net.cybertip.scheme.GraphWriter;
import net.cybertip.scheme.GraphWriterTrouble;
import net.cybertip.scheme.GraphXmlWriter;

public class GraphWriterFactory {
    public static GraphWriter fetchGraphWriter(String type) throws GraphWriterTrouble {
        if (type.equals("xml")) {
            return new GraphXmlWriter();
        }
        if (type.equals("text")) {
            return new GraphTextWriter();
        }
        throw new GraphWriterTrouble("File type " + type + " is not supported.");
    }
}


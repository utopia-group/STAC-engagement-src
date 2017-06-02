/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import net.techpoint.graph.SchemeTextWriter;
import net.techpoint.graph.SchemeWriter;
import net.techpoint.graph.SchemeWriterFailure;
import net.techpoint.graph.SchemeXmlWriter;

public class SchemeWriterFactory {
    public static SchemeWriter obtainSchemeWriter(String type) throws SchemeWriterFailure {
        if (type.equals("xml")) {
            return new SchemeXmlWriter();
        }
        if (type.equals("text")) {
            return new SchemeTextWriter();
        }
        throw new SchemeWriterFailure("File type " + type + " is not supported.");
    }
}


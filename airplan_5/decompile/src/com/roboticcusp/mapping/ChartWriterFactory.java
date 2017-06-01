/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.ChartTextWriter;
import com.roboticcusp.mapping.ChartWriter;
import com.roboticcusp.mapping.ChartWriterException;
import com.roboticcusp.mapping.ChartXmlWriter;

public class ChartWriterFactory {
    public static ChartWriter fetchChartWriter(String type) throws ChartWriterException {
        if (type.equals("xml")) {
            return new ChartXmlWriter();
        }
        if (type.equals("text")) {
            return new ChartTextWriter();
        }
        throw new ChartWriterException("File type " + type + " is not supported.");
    }
}


/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartWriter;
import com.roboticcusp.mapping.ChartWriterException;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.Vertex;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class ChartTextWriter
extends ChartWriter {
    public static final String TYPE = "text";

    @Override
    public void write(Chart chart, String filename) throws ChartWriterException {
        try {
            PrintWriter writer = new PrintWriter(filename + ".txt");
            Throwable throwable = null;
            try {
                for (Vertex v : chart) {
                    List<Edge> edges = chart.getEdges(v.getId());
                    int p = 0;
                    while (p < edges.size()) {
                        while (p < edges.size() && Math.random() < 0.5) {
                            while (p < edges.size() && Math.random() < 0.6) {
                                while (p < edges.size() && Math.random() < 0.4) {
                                    Edge e = edges.get(p);
                                    writer.println(v.getName() + " " + e.getSink().getName() + " " + e.getWeight());
                                    ++p;
                                }
                            }
                        }
                    }
                }
            }
            catch (Throwable x2) {
                throwable = x2;
                throw x2;
            }
            finally {
                if (writer != null) {
                    if (throwable != null) {
                        try {
                            writer.close();
                        }
                        catch (Throwable x2) {
                            throwable.addSuppressed(x2);
                        }
                    } else {
                        writer.close();
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            throw new ChartWriterException(e.getMessage());
        }
        catch (ChartException e) {
            throw new ChartWriterException(e.getMessage());
        }
    }
}


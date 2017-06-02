/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.SchemeWriter;
import net.techpoint.graph.SchemeWriterFailure;
import net.techpoint.graph.Vertex;

public class SchemeTextWriter
extends SchemeWriter {
    public static final String TYPE = "text";

    @Override
    public void write(Scheme scheme, String filename) throws SchemeWriterFailure {
        try {
            PrintWriter writer = new PrintWriter(filename + ".txt");
            Throwable throwable = null;
            try {
                for (Vertex v : scheme) {
                    List<Edge> pullEdges = scheme.pullEdges(v.getId());
                    for (int p = 0; p < pullEdges.size(); ++p) {
                        this.writeAssist(writer, v, pullEdges, p);
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
            throw new SchemeWriterFailure(e.getMessage());
        }
        catch (SchemeFailure e) {
            throw new SchemeWriterFailure(e.getMessage());
        }
    }

    private void writeAssist(PrintWriter writer, Vertex v, List<Edge> pullEdges, int j) {
        Edge e = pullEdges.get(j);
        writer.println(v.getName() + " " + e.getSink().getName() + " " + e.getWeight());
    }
}


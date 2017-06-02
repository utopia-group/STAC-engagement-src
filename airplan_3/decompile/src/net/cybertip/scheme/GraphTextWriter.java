/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.GraphWriter;
import net.cybertip.scheme.GraphWriterTrouble;
import net.cybertip.scheme.Vertex;

public class GraphTextWriter
extends GraphWriter {
    public static final String TYPE = "text";

    @Override
    public void write(Graph graph, String filename) throws GraphWriterTrouble {
        try {
            PrintWriter writer = new PrintWriter(filename + ".txt");
            Throwable throwable = null;
            try {
                for (Vertex v : graph) {
                    List<Edge> fetchEdges = graph.fetchEdges(v.getId());
                    for (int j = 0; j < fetchEdges.size(); ++j) {
                        this.writeHome(writer, v, fetchEdges, j);
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
            throw new GraphWriterTrouble(e.getMessage());
        }
        catch (GraphTrouble e) {
            throw new GraphWriterTrouble(e.getMessage());
        }
    }

    private void writeHome(PrintWriter writer, Vertex v, List<Edge> fetchEdges, int a) {
        Edge e = fetchEdges.get(a);
        writer.println(v.getName() + " " + e.getSink().getName() + " " + e.getWeight());
    }
}


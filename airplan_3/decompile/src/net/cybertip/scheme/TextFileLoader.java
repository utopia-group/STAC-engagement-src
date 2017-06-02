/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import net.cybertip.scheme.BasicData;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphFactory;
import net.cybertip.scheme.GraphFileLoader;
import net.cybertip.scheme.GraphLoader;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Vertex;

public class TextFileLoader
implements GraphFileLoader {
    private static final String[] EXTENSIONS = new String[]{"txt"};

    public static void register() {
        GraphLoader.registerLoader(new TextFileLoader());
    }

    @Override
    public Graph loadGraph(String filename) throws FileNotFoundException, GraphTrouble {
        Graph graph;
        graph = GraphFactory.newInstance();
        Scanner scanner = new Scanner(new File(filename));
        Throwable throwable = null;
        try {
            while (scanner.hasNext()) {
                this.loadGraphEngine(graph, scanner);
            }
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (scanner != null) {
                if (throwable != null) {
                    try {
                        scanner.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    scanner.close();
                }
            }
        }
        return graph;
    }

    private void loadGraphEngine(Graph graph, Scanner scanner) throws GraphTrouble {
        try {
            String v1 = scanner.next();
            String v2 = scanner.next();
            double weight = scanner.nextDouble();
            if (!graph.containsVertexWithName(v1)) {
                this.loadGraphEngineGateKeeper(graph, v1);
            }
            if (!graph.containsVertexWithName(v2)) {
                this.loadGraphEngineGuide(graph, v2);
            }
            BasicData data = new BasicData(weight);
            graph.addEdge(graph.fetchVertexIdByName(v1), graph.fetchVertexIdByName(v2), data);
        }
        catch (NoSuchElementException e) {
            throw new GraphTrouble("Invalid graph file format", e);
        }
        catch (IllegalStateException e) {
            throw new GraphTrouble("Invalid graph file format", e);
        }
    }

    private void loadGraphEngineGuide(Graph graph, String v2) throws GraphTrouble {
        graph.addVertex(v2);
    }

    private void loadGraphEngineGateKeeper(Graph graph, String v1) throws GraphTrouble {
        graph.addVertex(v1);
    }

    @Override
    public List<String> fetchExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}


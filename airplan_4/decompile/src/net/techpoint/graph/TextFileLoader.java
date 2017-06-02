/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import net.techpoint.graph.BasicData;
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFactory;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.SchemeFileLoader;
import net.techpoint.graph.SchemeLoader;
import net.techpoint.graph.Vertex;

public class TextFileLoader
implements SchemeFileLoader {
    private static final String[] EXTENSIONS = new String[]{"txt"};

    public static void register() {
        SchemeLoader.registerLoader(new TextFileLoader());
    }

    @Override
    public Scheme loadScheme(String filename) throws FileNotFoundException, SchemeFailure {
        Scheme scheme;
        scheme = SchemeFactory.newInstance();
        Scanner scanner = new Scanner(new File(filename));
        Throwable throwable = null;
        try {
            while (scanner.hasNext()) {
                this.loadSchemeUtility(scheme, scanner);
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
        return scheme;
    }

    private void loadSchemeUtility(Scheme scheme, Scanner scanner) throws SchemeFailure {
        try {
            String v1 = scanner.next();
            String v2 = scanner.next();
            double weight = scanner.nextDouble();
            if (!scheme.containsVertexWithName(v1)) {
                scheme.addVertex(v1);
            }
            if (!scheme.containsVertexWithName(v2)) {
                this.loadSchemeUtilityAssist(scheme, v2);
            }
            BasicData data = new BasicData(weight);
            scheme.addEdge(scheme.getVertexIdByName(v1), scheme.getVertexIdByName(v2), data);
        }
        catch (NoSuchElementException e) {
            throw new SchemeFailure("Invalid graph file format", e);
        }
        catch (IllegalStateException e) {
            throw new SchemeFailure("Invalid graph file format", e);
        }
    }

    private void loadSchemeUtilityAssist(Scheme scheme, String v2) throws SchemeFailure {
        scheme.addVertex(v2);
    }

    @Override
    public List<String> obtainExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}


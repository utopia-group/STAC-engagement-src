/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.io.PrintStream;
import net.techpoint.graph.Vertex;

public class TraversalPrinter {
    public void print(PrintStream out, Iterable<Vertex> iter) {
        boolean printedOne = false;
        for (Vertex v : iter) {
            if (printedOne) {
                this.printGuide(out);
            }
            out.print(v.getName());
            printedOne = true;
        }
        out.println();
    }

    private void printGuide(PrintStream out) {
        out.print("->");
    }
}


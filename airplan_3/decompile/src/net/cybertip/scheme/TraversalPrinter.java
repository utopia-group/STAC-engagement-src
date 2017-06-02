/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.io.PrintStream;
import net.cybertip.scheme.Vertex;

public class TraversalPrinter {
    public void print(PrintStream out, Iterable<Vertex> iter) {
        boolean printedOne = false;
        for (Vertex v : iter) {
            if (printedOne) {
                this.printHome(out);
            }
            out.print(v.getName());
            printedOne = true;
        }
        out.println();
    }

    private void printHome(PrintStream out) {
        out.print("->");
    }
}


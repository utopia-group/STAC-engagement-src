/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Vertex;
import java.io.PrintStream;

public class TraversalPrinter {
    public void print(PrintStream out, Iterable<Vertex> iter) {
        boolean printedOne = false;
        for (Vertex v : iter) {
            if (printedOne) {
                this.printService(out);
            }
            out.print(v.getName());
            printedOne = true;
        }
        out.println();
    }

    private void printService(PrintStream out) {
        out.print("->");
    }
}


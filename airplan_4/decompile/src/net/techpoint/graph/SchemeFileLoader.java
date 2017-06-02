/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.io.FileNotFoundException;
import java.util.List;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;

public interface SchemeFileLoader {
    public Scheme loadScheme(String var1) throws FileNotFoundException, SchemeFailure;

    public List<String> obtainExtensions();
}


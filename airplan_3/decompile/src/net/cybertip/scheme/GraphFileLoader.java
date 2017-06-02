/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.io.FileNotFoundException;
import java.util.List;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;

public interface GraphFileLoader {
    public Graph loadGraph(String var1) throws FileNotFoundException, GraphTrouble;

    public List<String> fetchExtensions();
}


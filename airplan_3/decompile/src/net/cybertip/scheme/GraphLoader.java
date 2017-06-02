/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FilenameUtils
 */
package net.cybertip.scheme;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphFileLoader;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.JackFileLoader;
import net.cybertip.scheme.TextFileLoader;
import net.cybertip.scheme.XmlFileLoader;
import org.apache.commons.io.FilenameUtils;

public class GraphLoader {
    private static Map<String, GraphFileLoader> extToLoaderMap = new HashMap<String, GraphFileLoader>();

    public static Graph loadGraph(String filename) throws FileNotFoundException, GraphTrouble {
        String extension = FilenameUtils.getExtension((String)filename);
        if (extToLoaderMap.containsKey(extension)) {
            return extToLoaderMap.get(extension).loadGraph(filename);
        }
        throw new GraphTrouble("There is no loader for a file with extension " + extension);
    }

    public static void registerLoader(GraphFileLoader loader) {
        List<String> extensions = loader.fetchExtensions();
        for (int c = 0; c < extensions.size(); ++c) {
            GraphLoader.registerLoaderEngine(loader, extensions, c);
        }
    }

    private static void registerLoaderEngine(GraphFileLoader loader, List<String> extensions, int j) {
        String extension = extensions.get(j);
        extToLoaderMap.put(extension, loader);
    }

    static {
        TextFileLoader.register();
        XmlFileLoader.register();
        JackFileLoader.register();
    }
}


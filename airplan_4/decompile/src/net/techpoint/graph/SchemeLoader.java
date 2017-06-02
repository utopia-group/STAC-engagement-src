/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FilenameUtils
 */
package net.techpoint.graph;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.techpoint.graph.PartFileLoader;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.SchemeFileLoader;
import net.techpoint.graph.TextFileLoader;
import net.techpoint.graph.XmlFileLoader;
import org.apache.commons.io.FilenameUtils;

public class SchemeLoader {
    private static Map<String, SchemeFileLoader> extToLoaderMap = new HashMap<String, SchemeFileLoader>();

    public static Scheme loadScheme(String filename) throws FileNotFoundException, SchemeFailure {
        String extension = FilenameUtils.getExtension((String)filename);
        if (extToLoaderMap.containsKey(extension)) {
            return extToLoaderMap.get(extension).loadScheme(filename);
        }
        throw new SchemeFailure("There is no loader for a file with extension " + extension);
    }

    public static void registerLoader(SchemeFileLoader loader) {
        List<String> extensions = loader.obtainExtensions();
        for (int b = 0; b < extensions.size(); ++b) {
            String extension = extensions.get(b);
            extToLoaderMap.put(extension, loader);
        }
    }

    static {
        TextFileLoader.register();
        XmlFileLoader.register();
        PartFileLoader.register();
    }
}


/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FilenameUtils
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFileLoader;
import com.roboticcusp.mapping.ParserFileLoader;
import com.roboticcusp.mapping.TextFileLoader;
import com.roboticcusp.mapping.XmlFileLoader;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;

public class ChartLoader {
    private static Map<String, ChartFileLoader> extToLoaderMap = new HashMap<String, ChartFileLoader>();

    public static Chart loadChart(String filename) throws FileNotFoundException, ChartException {
        String extension = FilenameUtils.getExtension((String)filename);
        if (extToLoaderMap.containsKey(extension)) {
            return extToLoaderMap.get(extension).loadChart(filename);
        }
        return ChartLoader.loadChartService(extension);
    }

    private static Chart loadChartService(String extension) throws ChartException {
        throw new ChartException("There is no loader for a file with extension " + extension);
    }

    public static void registerLoader(ChartFileLoader loader) {
        List<String> extensions = loader.obtainExtensions();
        for (int p = 0; p < extensions.size(); ++p) {
            ChartLoader.registerLoaderCoordinator(loader, extensions, p);
        }
    }

    private static void registerLoaderCoordinator(ChartFileLoader loader, List<String> extensions, int q) {
        String extension = extensions.get(q);
        extToLoaderMap.put(extension, loader);
    }

    static {
        TextFileLoader.register();
        XmlFileLoader.register();
        ParserFileLoader.register();
    }
}


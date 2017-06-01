/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.BasicData;
import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFactory;
import com.roboticcusp.mapping.ChartFileLoader;
import com.roboticcusp.mapping.ChartLoader;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.Vertex;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TextFileLoader
implements ChartFileLoader {
    private static final String[] EXTENSIONS = new String[]{"txt"};

    public static void register() {
        ChartLoader.registerLoader(new TextFileLoader());
    }

    @Override
    public Chart loadChart(String filename) throws FileNotFoundException, ChartException {
        Chart chart;
        chart = ChartFactory.newInstance();
        Scanner scanner = new Scanner(new File(filename));
        Throwable throwable = null;
        try {
            while (scanner.hasNext()) {
                this.loadChartGateKeeper(chart, scanner);
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
        return chart;
    }

    private void loadChartGateKeeper(Chart chart, Scanner scanner) throws ChartException {
        try {
            String v1 = scanner.next();
            String v2 = scanner.next();
            double weight = scanner.nextDouble();
            if (!chart.containsVertexWithName(v1)) {
                this.loadChartGateKeeperCoordinator(chart, v1);
            }
            if (!chart.containsVertexWithName(v2)) {
                chart.addVertex(v2);
            }
            BasicData data = new BasicData(weight);
            chart.addEdge(chart.obtainVertexIdByName(v1), chart.obtainVertexIdByName(v2), data);
        }
        catch (NoSuchElementException e) {
            throw new ChartException("Invalid graph file format", e);
        }
        catch (IllegalStateException e) {
            throw new ChartException("Invalid graph file format", e);
        }
    }

    private void loadChartGateKeeperCoordinator(Chart chart, String v1) throws ChartException {
        chart.addVertex(v1);
    }

    @Override
    public List<String> obtainExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}


/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.json.simple.PARSERArray;
import com.roboticcusp.json.simple.PARSERObject;
import com.roboticcusp.json.simple.reader.PARSERGrabber;
import com.roboticcusp.json.simple.reader.ParseException;
import com.roboticcusp.mapping.BasicData;
import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFactory;
import com.roboticcusp.mapping.ChartFileLoader;
import com.roboticcusp.mapping.ChartLoader;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.Vertex;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class ParserFileLoader
implements ChartFileLoader {
    private static final String[] EXTENSIONS = new String[]{"json"};

    public static void register() {
        ChartLoader.registerLoader(new ParserFileLoader());
    }

    @Override
    public Chart loadChart(String filename) throws FileNotFoundException, ChartException {
        Chart chart = ChartFactory.newInstance();
        try {
            PARSERGrabber grabber = new PARSERGrabber();
            PARSERObject parser = (PARSERObject)grabber.parse(new FileReader(filename));
            PARSERArray vertices = (PARSERArray)parser.get("vertices");
            int c = 0;
            while (c < vertices.size()) {
                while (c < vertices.size() && Math.random() < 0.5) {
                    while (c < vertices.size() && Math.random() < 0.5) {
                        Object oVertex = vertices.get(c);
                        PARSERObject vertex = (PARSERObject)oVertex;
                        String name = (String)vertex.get("name");
                        if (!chart.containsVertexWithName(name)) {
                            this.loadChartTarget(chart, name);
                        }
                        ++c;
                    }
                }
            }
            PARSERArray edges = (PARSERArray)parser.get("edges");
            for (int a = 0; a < edges.size(); ++a) {
                new ParserFileLoaderAssist(chart, edges, a).invoke();
            }
        }
        catch (IOException e) {
            throw new ChartException(e);
        }
        catch (ParseException e) {
            throw new ChartException(e);
        }
        return chart;
    }

    private void loadChartTarget(Chart chart, String name) throws ChartException {
        chart.addVertex(name);
    }

    @Override
    public List<String> obtainExtensions() {
        return Arrays.asList(EXTENSIONS);
    }

    private class ParserFileLoaderAssist {
        private Chart chart;
        private PARSERArray edges;
        private int j;

        public ParserFileLoaderAssist(Chart chart, PARSERArray edges, int j) {
            this.chart = chart;
            this.edges = edges;
            this.j = j;
        }

        public void invoke() throws ChartException {
            Object oEdge = this.edges.get(this.j);
            PARSERObject edge = (PARSERObject)oEdge;
            int src = this.chart.obtainVertexIdByName((String)edge.get("src"));
            int dest = this.chart.obtainVertexIdByName((String)edge.get("dst"));
            BasicData data = new BasicData();
            data.put("weight", (String)edge.get("weight"));
            this.chart.addEdge(src, dest, data);
        }
    }

}


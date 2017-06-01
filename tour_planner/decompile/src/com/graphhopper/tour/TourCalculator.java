/*
 * Decompiled with CFR 0_121.
 */
package com.graphhopper.tour;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.DefaultEdgeFilter;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.tour.Matrix;
import com.graphhopper.tour.TourResponse;
import com.graphhopper.tour.util.Edge;
import com.graphhopper.tour.util.Graph;
import com.graphhopper.tour.util.ProgressReporter;
import com.graphhopper.util.DistanceCalc;
import com.graphhopper.util.DistanceCalcEarth;
import com.graphhopper.util.shapes.GHPlace;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.util.shapes.GHPoint3D;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TourCalculator<P extends GHPlace> {
    private final Logger logger;
    private final Matrix<P> matrix;
    private final GraphHopper graphHopper;
    private final List<Edge<P>> sortedEdges;
    private final Map<GHPoint, P> knownPoints;
    private final Map<GHPoint, QueryResult> queryResults;

    public TourCalculator(Matrix<P> matrix) {
        this(matrix, null);
    }

    public TourCalculator(Matrix<P> matrix, GraphHopper graphHopper) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.matrix = matrix;
        this.graphHopper = graphHopper;
        this.sortedEdges = matrix.symmetricEdges();
        Collections.sort(this.sortedEdges, new Edge.WeightComparator());
        LocationIndex locationIndex = graphHopper.getLocationIndex();
        EncodingManager encodingManager = graphHopper.getEncodingManager();
        FlagEncoder flagEncoder = encodingManager.fetchEdgeEncoders().get(0);
        DefaultEdgeFilter edgeFilter = new DefaultEdgeFilter(flagEncoder);
        this.knownPoints = new HashMap<GHPoint, P>();
        this.queryResults = new HashMap<GHPoint, QueryResult>();
        for (P p : matrix.getPoints()) {
            this.knownPoints.put(p, p);
            this.queryResults.put(p, locationIndex.findClosest(p.lat, p.lon, edgeFilter));
        }
    }

    public TourResponse<P> calcTour(List<? extends GHPoint> points) {
        return this.calcTour(points, null);
    }

    public TourResponse<P> calcTour(List<? extends GHPoint> points, ProgressReporter progressReporter) {
        TourResponse<P> rsp = new TourResponse<>();
        if (points.size() < 2) {
            rsp.addError(new IllegalArgumentException("At least two points must be specified"));
            return rsp;
        }
        for (GHPoint p : points) {
            if (this.knownPoints.containsKey(p)) continue;
            rsp.addError(new IllegalArgumentException("Unknown point " + p));
            return rsp;
        }
        if (progressReporter == null) {
            progressReporter = ProgressReporter.SILENT;
        }
        P root = this.knownPoints.get(points.get(0));
        HashSet<GHPoint> reqPoints = new HashSet<GHPoint>();
        reqPoints.addAll(points);
        Graph<P> minSpanningTree = this.calcMinSpanningTree(root, reqPoints, progressReporter);
        List<P> rspPoints = minSpanningTree.depthFirstWalk(root);
        rspPoints.add(root);
        rsp.setPoints(rspPoints);
        return rsp;
    }

    protected Graph<P> calcMinSpanningTree(P root, Set<GHPoint> reqPoints, ProgressReporter progressReporter) {
        Graph<P> result = new Graph<P>().add(root);
        int complete = 0;
        int total = reqPoints.size() - 1;
        try {
            progressReporter.reportProgress(complete, total);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        block4 : while (result.size() < reqPoints.size()) {
            for (Edge<P> e : this.sortedEdges) {
                if (result.contains(e.to) && !result.contains(e.from)) {
                    e.reverse();
                }
                QueryResult fromQR = this.queryResults.get(e.from);
                QueryResult toQR = this.queryResults.get(e.to);
                DistanceCalcEarth distanceCalc = new DistanceCalcEarth();
                fromQR.calcSnappedPoint(distanceCalc);
                toQR.calcSnappedPoint(distanceCalc);
                GHPoint3D fromSnappedPoint = fromQR.getSnappedPoint();
                GHPoint3D toSnappedPoint = toQR.getSnappedPoint();
                if (!result.contains(e.from) || result.contains(e.to) || !reqPoints.contains(e.to)) continue;
                assert (reqPoints.contains(e.from));
                if (result.contains(e) || !reqPoints.contains(e.to)) continue;
                result.add(e);
                try {
                    progressReporter.reportProgress(++complete, total);
                }
                catch (IOException iOException) {}
                continue block4;
            }
        }
        return result;
    }
}


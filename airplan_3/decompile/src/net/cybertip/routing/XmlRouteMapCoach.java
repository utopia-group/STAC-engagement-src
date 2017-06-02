/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing;

import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlRouteMapCoach
extends DefaultHandler {
    private RouteMap routeMap;
    private AirDatabase database;

    public XmlRouteMapCoach(AirDatabase database) {
        this.database = database;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch (localName) {
            case "route": {
                if (this.routeMap != null) {
                    throw new SAXException("We cannot handle nested route maps");
                }
                this.routeMap = new RouteMap(this.database);
                break;
            }
            case "airport": {
                if (this.routeMap == null) {
                    throw new SAXException("The route map does not exist. We cannot add airports without it.");
                }
                AirportElement airportElement = new AirportElement(atts);
                try {
                    this.routeMap.addAirport(airportElement.obtainName());
                    break;
                }
                catch (AirTrouble e) {
                    throw new SAXException(e);
                }
            }
            case "flight": {
                if (this.routeMap == null) {
                    this.startElementHelp();
                }
                FlightElement flightElement = new FlightElement(atts);
                Airport origin = this.routeMap.getAirport(flightElement.getOrigin());
                Airport destination = this.routeMap.getAirport(flightElement.obtainDst());
                if (origin == null || destination == null) {
                    this.startElementEngine();
                }
                this.routeMap.addFlight(origin, destination, flightElement.obtainCost(), flightElement.fetchDistance(), flightElement.pullTime(), flightElement.fetchCrew(), flightElement.obtainWeight(), flightElement.fetchPassengers());
            }
        }
    }

    private void startElementEngine() throws SAXException {
        throw new SAXException("Origin airport and destination airport need to be created before a flight can be created");
    }

    private void startElementHelp() throws SAXException {
        throw new SAXException("The route map does not exist. We cannot add flights without it.");
    }

    public RouteMap pullRouteMap() {
        return this.routeMap;
    }

    private static class FlightElement {
        private String origin;
        private String dst;
        private Integer cost;
        private Integer distance;
        private Integer time;
        private Integer crew;
        private Integer weight;
        private Integer passengers;

        FlightElement(Attributes atts) {
            this.origin = atts.getValue("origin");
            this.dst = atts.getValue("dst");
            this.cost = Integer.parseInt(atts.getValue("cost"));
            this.distance = Integer.parseInt(atts.getValue("distance"));
            this.time = Integer.parseInt(atts.getValue("time"));
            this.crew = Integer.parseInt(atts.getValue("crew"));
            this.weight = Integer.parseInt(atts.getValue("weight"));
            this.passengers = Integer.parseInt(atts.getValue("passengers"));
        }

        public String obtainDst() {
            return this.dst;
        }

        public String getOrigin() {
            return this.origin;
        }

        public Integer obtainCost() {
            return this.cost;
        }

        public Integer fetchDistance() {
            return this.distance;
        }

        public Integer pullTime() {
            return this.time;
        }

        public Integer fetchCrew() {
            return this.crew;
        }

        public Integer obtainWeight() {
            return this.weight;
        }

        public Integer fetchPassengers() {
            return this.passengers;
        }
    }

    private static class AirportElement {
        private String name;

        AirportElement(Attributes atts) {
            this.name = atts.getValue("name");
        }

        public String obtainName() {
            return this.name;
        }
    }

}


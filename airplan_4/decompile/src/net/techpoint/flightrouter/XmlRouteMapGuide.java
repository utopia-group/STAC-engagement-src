/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter;

import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlRouteMapGuide
extends DefaultHandler {
    private RouteMap routeMap;
    private AirDatabase database;

    public XmlRouteMapGuide(AirDatabase database) {
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
                    new XmlRouteMapGuideEntity().invoke();
                }
                AirportElement airportElement = new AirportElement(atts);
                try {
                    this.routeMap.addAirport(airportElement.obtainName());
                    break;
                }
                catch (AirFailure e) {
                    throw new SAXException(e);
                }
            }
            case "flight": {
                if (this.routeMap == null) {
                    this.startElementAssist();
                }
                FlightElement flightElement = new FlightElement(atts);
                Airport origin = this.routeMap.getAirport(flightElement.fetchOrigin());
                Airport destination = this.routeMap.getAirport(flightElement.takeDst());
                if (origin == null || destination == null) {
                    new XmlRouteMapGuideAdviser().invoke();
                }
                this.routeMap.addFlight(origin, destination, flightElement.pullCost(), flightElement.grabDistance(), flightElement.obtainTime(), flightElement.pullCrew(), flightElement.fetchWeight(), flightElement.getPassengers());
            }
        }
    }

    private void startElementAssist() throws SAXException {
        new XmlRouteMapGuideUtility().invoke();
    }

    public RouteMap takeRouteMap() {
        return this.routeMap;
    }

    private class XmlRouteMapGuideAdviser {
        private XmlRouteMapGuideAdviser() {
        }

        public void invoke() throws SAXException {
            throw new SAXException("Origin airport and destination airport need to be created before a flight can be created");
        }
    }

    private class XmlRouteMapGuideUtility {
        private XmlRouteMapGuideUtility() {
        }

        public void invoke() throws SAXException {
            throw new SAXException("The route map does not exist. We cannot add flights without it.");
        }
    }

    private class XmlRouteMapGuideEntity {
        private XmlRouteMapGuideEntity() {
        }

        public void invoke() throws SAXException {
            throw new SAXException("The route map does not exist. We cannot add airports without it.");
        }
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

        public String takeDst() {
            return this.dst;
        }

        public String fetchOrigin() {
            return this.origin;
        }

        public Integer pullCost() {
            return this.cost;
        }

        public Integer grabDistance() {
            return this.distance;
        }

        public Integer obtainTime() {
            return this.time;
        }

        public Integer pullCrew() {
            return this.crew;
        }

        public Integer fetchWeight() {
            return this.weight;
        }

        public Integer getPassengers() {
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


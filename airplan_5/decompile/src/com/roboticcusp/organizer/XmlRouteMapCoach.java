/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer;

import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.xmlpull.v1.XmlPullGrabber;
import com.roboticcusp.xmlpull.v1.XmlPullGrabberException;

public class XmlRouteMapCoach {
    private RouteMap routeMap;
    private AirDatabase database;

    public XmlRouteMapCoach(AirDatabase database) {
        this.database = database;
    }

    public void startElement(String localName, XmlPullGrabber grabber) throws XmlPullGrabberException, AirException {
        switch (localName) {
            case "route": {
                if (this.routeMap != null) {
                    throw new XmlPullGrabberException("We cannot handle nested route maps");
                }
                this.routeMap = new RouteMap(this.database);
                break;
            }
            case "airport": {
                if (this.routeMap == null) {
                    this.startElementSupervisor();
                }
                AirportElement airportElement = new AirportElement(grabber);
                this.routeMap.addAirport(airportElement.grabName());
                break;
            }
            case "flight": {
                if (this.routeMap == null) {
                    this.startElementGuide();
                }
                FlightElement flightElement = new FlightElement(grabber);
                Airport origin = this.routeMap.obtainAirport(flightElement.fetchOrigin());
                Airport destination = this.routeMap.obtainAirport(flightElement.pullDst());
                if (origin == null || destination == null) {
                    throw new XmlPullGrabberException("Origin airport and destination airport need to be created before a flight can be created");
                }
                this.routeMap.addFlight(origin, destination, flightElement.getCost(), flightElement.getDistance(), flightElement.takeTime(), flightElement.fetchCrew(), flightElement.grabWeight(), flightElement.fetchPassengers());
            }
        }
    }

    private void startElementGuide() throws XmlPullGrabberException {
        throw new XmlPullGrabberException("The route map does not exist. We cannot add flights without it.");
    }

    private void startElementSupervisor() throws XmlPullGrabberException {
        throw new XmlPullGrabberException("The route map does not exist. We cannot add flights without it.");
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

        FlightElement(XmlPullGrabber grabber) {
            this.origin = grabber.grabAttributeValue(null, "origin");
            this.dst = grabber.grabAttributeValue(null, "dst");
            this.cost = Integer.parseInt(grabber.grabAttributeValue(null, "cost"));
            this.distance = Integer.parseInt(grabber.grabAttributeValue(null, "distance"));
            this.time = Integer.parseInt(grabber.grabAttributeValue(null, "time"));
            this.crew = Integer.parseInt(grabber.grabAttributeValue(null, "crew"));
            this.weight = Integer.parseInt(grabber.grabAttributeValue(null, "weight"));
            this.passengers = Integer.parseInt(grabber.grabAttributeValue(null, "passengers"));
        }

        public String pullDst() {
            return this.dst;
        }

        public String fetchOrigin() {
            return this.origin;
        }

        public Integer getCost() {
            return this.cost;
        }

        public Integer getDistance() {
            return this.distance;
        }

        public Integer takeTime() {
            return this.time;
        }

        public Integer fetchCrew() {
            return this.crew;
        }

        public Integer grabWeight() {
            return this.weight;
        }

        public Integer fetchPassengers() {
            return this.passengers;
        }
    }

    private static class AirportElement {
        private String name;

        AirportElement(XmlPullGrabber grabber) {
            this.name = grabber.grabAttributeValue(null, "name");
        }

        public String grabName() {
            return this.name;
        }
    }

}


/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.prototype;

import net.techpoint.flightrouter.prototype.Crew;

public class CrewBuilder {
    private int id;

    public CrewBuilder fixId(int id) {
        this.id = id;
        return this;
    }

    public Crew formCrew() {
        return new Crew(this.id);
    }
}


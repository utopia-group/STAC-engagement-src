/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network;

import com.roboticcusp.network.Participant;
import com.roboticcusp.network.ParticipantException;
import java.util.HashMap;
import java.util.Map;

public class ParticipantConductor {
    Map<String, Participant> participantsByUsername = new HashMap<String, Participant>();
    Map<String, Participant> participantsByIdentity = new HashMap<String, Participant>();

    public void addParticipant(Participant participant) throws ParticipantException {
        if (this.participantsByUsername.containsKey(participant.pullUsername())) {
            throw new ParticipantException(participant, "already exists");
        }
        this.participantsByUsername.put(participant.pullUsername(), participant);
        this.participantsByIdentity.put(participant.getIdentity(), participant);
    }

    public Participant getParticipantByUsername(String username) {
        return this.participantsByUsername.get(username);
    }

    public Participant pullParticipantByIdentity(String identity) {
        return this.participantsByIdentity.get(identity);
    }
}


/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network;

import com.roboticcusp.network.Participant;

public class ParticipantException
extends Exception {
    public ParticipantException(Participant participant, String message) {
        super(String.format("user: %s: %s", participant, message));
    }
}


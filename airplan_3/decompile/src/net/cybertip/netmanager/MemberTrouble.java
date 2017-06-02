/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager;

import net.cybertip.netmanager.Member;

public class MemberTrouble
extends Exception {
    public MemberTrouble(Member member, String message) {
        super(String.format("user: %s: %s", member, message));
    }
}


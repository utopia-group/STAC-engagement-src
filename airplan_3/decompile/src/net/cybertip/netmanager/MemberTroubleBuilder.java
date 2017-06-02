/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager;

import net.cybertip.netmanager.Member;
import net.cybertip.netmanager.MemberTrouble;

public class MemberTroubleBuilder {
    private String message;
    private Member member;

    public MemberTroubleBuilder assignMessage(String message) {
        this.message = message;
        return this;
    }

    public MemberTroubleBuilder defineMember(Member member) {
        this.member = member;
        return this;
    }

    public MemberTrouble makeMemberTrouble() {
        return new MemberTrouble(this.member, this.message);
    }
}


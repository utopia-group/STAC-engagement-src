/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager;

import java.util.HashMap;
import java.util.Map;
import net.cybertip.netmanager.Member;
import net.cybertip.netmanager.MemberTrouble;
import net.cybertip.netmanager.MemberTroubleBuilder;

public class MemberOverseer {
    Map<String, Member> membersByUsername = new HashMap<String, Member>();
    Map<String, Member> membersByIdentity = new HashMap<String, Member>();

    public void addMember(Member member) throws MemberTrouble {
        if (this.membersByUsername.containsKey(member.getUsername())) {
            this.addMemberHelp(member);
        }
        this.membersByUsername.put(member.getUsername(), member);
        this.membersByIdentity.put(member.takeIdentity(), member);
    }

    private void addMemberHelp(Member member) throws MemberTrouble {
        new MemberOverseerGuide(member).invoke();
    }

    public Member getMemberByUsername(String username) {
        return this.membersByUsername.get(username);
    }

    public Member obtainMemberByIdentity(String identity) {
        return this.membersByIdentity.get(identity);
    }

    private class MemberOverseerGuide {
        private Member member;

        public MemberOverseerGuide(Member member) {
            this.member = member;
        }

        public void invoke() throws MemberTrouble {
            throw new MemberTroubleBuilder().defineMember(this.member).assignMessage("already exists").makeMemberTrouble();
        }
    }

}


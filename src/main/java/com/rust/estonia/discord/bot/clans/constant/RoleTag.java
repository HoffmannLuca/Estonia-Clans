package com.rust.estonia.discord.bot.clans.constant;

public class RoleTag {

    private RoleTag() {

        throw new IllegalStateException();
    }

    public static final String ADMIN_ROLE = "admin";
    public static final String MODERATOR_ROLE = "moderator";
    public static final String CLAN_LEADER_ROLE = "clan-leader";
    public static final String CLAN_OFFICER_ROLE = "clan-officer";
    // MAX 25
    public static final String[] roleTags = {ADMIN_ROLE, MODERATOR_ROLE, CLAN_LEADER_ROLE, CLAN_OFFICER_ROLE};
}

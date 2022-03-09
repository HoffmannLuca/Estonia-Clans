package com.rust.estonia.discord.bot.clans.constant;

public class LogMessageTag {

    private LogMessageTag() {

        throw new IllegalStateException();
    }

    public static final String CLAN_CREATED = "clan-create";
    public static final String CLAN_RENAMED = "clan-rename";
    public static final String CLAN_DISBAND = "clan-disband";
    public static final String CLAN_PROMOTED = "clan-promote";
    public static final String CLAN_DEMOTED = "clan-demote";

    public static final String MEMBER_JOINED = "member-join";
    public static final String MEMBER_LEFT = "member-left";
    public static final String MEMBER_PROMOTED_OFFICER = "member-promote";
    public static final String MEMBER_PROMOTED_LEADER = "member-promote-leader";
    public static final String MEMBER_DEMOTED = "member-demote";
    public static final String MEMBER_KICKED = "member-kick";

    public static final String[] clanLogTags = {CLAN_CREATED, CLAN_RENAMED, CLAN_DISBAND, CLAN_PROMOTED, CLAN_DEMOTED};

    public static final String[] memberLogTags = {MEMBER_JOINED, MEMBER_LEFT, MEMBER_PROMOTED_OFFICER, MEMBER_PROMOTED_LEADER, MEMBER_DEMOTED, MEMBER_KICKED};
}

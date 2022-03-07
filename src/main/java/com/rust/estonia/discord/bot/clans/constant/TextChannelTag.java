package com.rust.estonia.discord.bot.clans.constant;

public class TextChannelTag {

    private TextChannelTag() {

        throw new IllegalStateException();
    }

    public static final String CLAN_COMMAND_CHANNEL = "clan-invites";
    public static final String CLAN_LOG_CHANNEL = "clan-logs";
    // MAX 25
    public static final String[] textChannelTags = {CLAN_COMMAND_CHANNEL, CLAN_LOG_CHANNEL};
}

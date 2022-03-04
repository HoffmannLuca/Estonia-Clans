package com.rust.estonia.discord.bot.clans.constant;

public class TextChannelTag {

    private TextChannelTag() {

        throw new IllegalStateException();
    }

    public static final String ADMIN_COMMAND_CHANNEL = "admin-command-channel";
    public static final String CLAN_COMMAND_CHANNEL = "clan-command-channel";
    public static final String CLAN_LOG_CHANNEL = "clan-log-channel";
    // MAX 25
    public static final String[] textChannelTags = {ADMIN_COMMAND_CHANNEL, CLAN_COMMAND_CHANNEL, CLAN_LOG_CHANNEL};
}

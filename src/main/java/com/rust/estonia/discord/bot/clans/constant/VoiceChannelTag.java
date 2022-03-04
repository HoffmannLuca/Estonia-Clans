package com.rust.estonia.discord.bot.clans.constant;

public class VoiceChannelTag {

    private VoiceChannelTag() {

        throw new IllegalStateException();
    }

    public static final String TEST_CHANNEL = "test-vc";
    // MAX 25
    public static final String[] voiceChannelTags = {TEST_CHANNEL};
}

package com.rust.estonia.discord.bot.clans.constant;

import java.util.HashMap;

public class CategoryTag {

    private CategoryTag() {

        throw new IllegalStateException();
    }

    public static final String NEW_CLAN_CATEGORY = "new-clans";
    public static final String ESTABLISHED_CLAN_CATEGORY = "established-clans";
    public static final String LEGACY_CLAN_CATEGORY = "legacy-clans";
    public static final String TOP_CLAN_CATEGORY = "top-clans";
    // MAX 25
    public static final String[] categoryTags = {NEW_CLAN_CATEGORY, ESTABLISHED_CLAN_CATEGORY, LEGACY_CLAN_CATEGORY, TOP_CLAN_CATEGORY};

}

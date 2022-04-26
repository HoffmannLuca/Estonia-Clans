package com.rust.estonia.discord.bot.clans.constant;

import java.util.HashMap;

public class CategoryTag {

    private CategoryTag() {

        throw new IllegalStateException();
    }

    public static final String NEW_CLAN_CATEGORY = "new-clans";
    public static final String ESTABLISHED_CLAN_CATEGORY = "established-clans";
    public static final String CONTENDER_CLAN_CATEGORY = "contender-clans";
    public static final String LEGACY_CLAN_CATEGORY = "legacy-clans";
    public static final String EMINENT_CLAN_CATEGORY = "eminent-clans";
    // MAX 25
    public static final String[] categoryTags = {NEW_CLAN_CATEGORY, ESTABLISHED_CLAN_CATEGORY, CONTENDER_CLAN_CATEGORY, LEGACY_CLAN_CATEGORY, EMINENT_CLAN_CATEGORY};

}

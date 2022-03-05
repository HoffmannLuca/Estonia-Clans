package com.rust.estonia.discord.bot.clans.util;

import org.javacord.api.entity.message.embed.EditableEmbedField;

@Deprecated
public class EmbedFieldModel implements EditableEmbedField {

    private String name;

    private String value;

    private boolean inline;

    @Deprecated
    public EmbedFieldModel(String name, String value, boolean inline) {

        this.name = name;
        this.value = value;
        this.inline = inline;
    }

    @Override
    @Deprecated
    public String toString() {
        return String.format(
                "EmbedField[\n" +
                        "\tname=%s, \n" +
                        "\tvalue='%s', \n" +
                        "\tinline='%s', \n" +
                        "]\n",
                name,
                value,
                inline
        );
    }

    @Override
    @Deprecated
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Deprecated
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    @Deprecated
    public void setInline(boolean inline) {
        this.inline = inline;
    }

    @Override
    @Deprecated
    public String getValue() {
        return this.value;
    }

    @Override
    @Deprecated
    public boolean isInline() {
        return this.inline;
    }

    @Override
    @Deprecated
    public String getName() {
        return this.name;
    }
}
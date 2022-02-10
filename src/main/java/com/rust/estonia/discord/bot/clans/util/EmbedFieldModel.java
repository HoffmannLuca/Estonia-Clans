package com.rust.estonia.discord.bot.clans.util;

import org.javacord.api.entity.message.embed.EditableEmbedField;

public class EmbedFieldModel implements EditableEmbedField {

    private String name;

    private String value;

    private boolean inline;

    public EmbedFieldModel(String name, String value, boolean inline) {

        this.name = name;
        this.value = value;
        this.inline = inline;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setInline(boolean inline) {
        this.inline = inline;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean isInline() {
        return this.inline;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
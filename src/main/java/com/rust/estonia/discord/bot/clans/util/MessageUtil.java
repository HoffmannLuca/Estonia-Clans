package com.rust.estonia.discord.bot.clans.util;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedField;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;


@Component
@Deprecated
public class MessageUtil {
    @Deprecated
    public void sendMessageAsEmbed(TextChannel channel, String title){
        sendMessageAsEmbedWithColorAndFields(channel, null, null, title, null, null, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbed(TextChannel channel, String title, String description){
        sendMessageAsEmbedWithColorAndFields(channel, null, null, title, description, null, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbed(TextChannel channel, String title, String description, String footer){
        sendMessageAsEmbedWithColorAndFields(channel, null, null, title, description, footer, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbed(TextChannel channel, String title, String description, String footer, String author){
        sendMessageAsEmbedWithColorAndFields(channel, null, null, title, description, footer, author, null);
    }
    @Deprecated
    public void sendMessageAsEmbed(TextChannel channel, String title, String description, String footer, String author, String thumbnailURL){
        sendMessageAsEmbedWithColorAndFields(channel, null, null, title, description, footer, author, thumbnailURL);
    }

    //With Color
    @Deprecated
    public void sendMessageAsEmbedWithColor(TextChannel channel, Color color, String title){
        sendMessageAsEmbedWithColorAndFields(channel, color, null, title, null, null, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithColor(TextChannel channel, Color color, String title, String description){
        sendMessageAsEmbedWithColorAndFields(channel, color, null, title, description, null, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithColor(TextChannel channel, Color color, String title, String description, String footer){
        sendMessageAsEmbedWithColorAndFields(channel, color, null, title, description, footer, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithColor(TextChannel channel, Color color, String title, String description, String footer, String author) {
        sendMessageAsEmbedWithColorAndFields(channel, color, null, title, description, footer, author, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithColor(TextChannel channel, Color color, String title, String description, String footer, String author, String thumbnailURL) {
        sendMessageAsEmbedWithColorAndFields(channel, color, null, title, description, footer, author, thumbnailURL);
    }

    //With Fields
    @Deprecated
    public void sendMessageAsEmbedWithFields(TextChannel channel, List<EmbedFieldModel> embedFields, String title){
        sendMessageAsEmbedWithColorAndFields(channel, null, embedFields, title, null, null, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithFields(TextChannel channel, List<EmbedFieldModel> embedFields, String title, String description){
        sendMessageAsEmbedWithColorAndFields(channel, null, embedFields, title, description, null, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithFields(TextChannel channel, List<EmbedFieldModel> embedFields, String title, String description, String footer){
        sendMessageAsEmbedWithColorAndFields(channel, null, embedFields, title, description, footer, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithFields(TextChannel channel, List<EmbedFieldModel> embedFields, String title, String description, String footer, String author){
        sendMessageAsEmbedWithColorAndFields(channel,null, embedFields, title, description, footer, author, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithFields(TextChannel channel, List<EmbedFieldModel> embedFields, String title, String description, String footer, String author, String thumbnailURL){
        sendMessageAsEmbedWithColorAndFields(channel,null, embedFields, title, description, footer, author, thumbnailURL);
    }

    //with Color and Fields
    @Deprecated
    public void sendMessageAsEmbedWithColorAndFields(TextChannel channel, Color color, List<EmbedFieldModel> embedFields, String title){
        sendMessageAsEmbedWithColorAndFields(channel, color, embedFields, title, null, null, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithColorAndFields(TextChannel channel, Color color, List<EmbedFieldModel> embedFields, String title, String description){
        sendMessageAsEmbedWithColorAndFields(channel, color, embedFields, title, description, null, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithColorAndFields(TextChannel channel, Color color, List<EmbedFieldModel> embedFields, String title, String description, String footer){
        sendMessageAsEmbedWithColorAndFields(channel, color, embedFields, title, description, footer, null, null);
    }
    @Deprecated
    public void sendMessageAsEmbedWithColorAndFields(TextChannel channel, Color color, List<EmbedFieldModel> embedFields, String title, String description, String footer, String author){
        sendMessageAsEmbedWithColorAndFields(channel,color, embedFields, title, description, footer, author, null);
    }

    @Deprecated
    public void sendMessageAsEmbedWithColorAndFields(TextChannel channel, Color color, List<EmbedFieldModel> embedFields, String title, String description, String footer, String author, String thumbnailURL){

        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(title)
                        .setAuthor(author)
                        .setDescription(description)
                        .setThumbnail(thumbnailURL)
                        .setFooter(footer)
                        .setColor(color);

        if(embedFields != null){
            for(EmbedField field: embedFields){
                embedBuilder.addField(field.getName(), field.getValue(), field.isInline());
            }
        }

        new MessageBuilder().setEmbed(embedBuilder).send(channel);

    }
    @Deprecated
    public MessageBuilder getMessageBuilder(String content, List<EmbedBuilder> embedBuilderList, List<LowLevelComponent> lowLevelComponentList){

        return new MessageBuilder().setContent(content).setEmbeds((EmbedBuilder) embedBuilderList).addActionRow((LowLevelComponent) lowLevelComponentList);
    }
}

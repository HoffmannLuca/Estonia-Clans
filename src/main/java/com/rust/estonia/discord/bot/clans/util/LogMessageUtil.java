package com.rust.estonia.discord.bot.clans.util;

import com.rust.estonia.discord.bot.clans.constant.LogMessageTag;
import com.rust.estonia.discord.bot.clans.constant.TextChannelTag;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;

@Component
public class LogMessageUtil {

    @Autowired
    private SetupService setupService;

    @Autowired
    private ClanService clanService;

    public boolean sendLogMessage(Server server, User user, String logMessageTag){

        return sendLogMessage(server, user, null, logMessageTag);
    }
    public boolean sendLogMessage(Server server, User user, Role clanRole, String logMessageTag){

        if(clanRole!=null) {
            if(Arrays.asList(LogMessageTag.clanLogTags).contains(logMessageTag)) {
                TextChannel logChannel = setupService.getServerTextChannelByChannelTag(server, TextChannelTag.CLAN_LOG_CHANNEL);

                if (logChannel != null) {
                    EmbedBuilder embedBuilder = getLogEmbedBuilder(logMessageTag, server, clanRole);

                    new MessageBuilder().addEmbed(embedBuilder.setAuthor(user)).send(logChannel);
                }
            } else if(Arrays.asList(LogMessageTag.memberLogTags).contains(logMessageTag)) {
                TextChannel clanChat = clanService.getClanTextChat(server, clanRole);

                if (clanChat != null) {
                    EmbedBuilder embedBuilder = getLogEmbedBuilder(logMessageTag, server, clanRole);

                    new MessageBuilder().addEmbed(embedBuilder.setAuthor(user)).send(clanChat);
                }
            }
        } else {
            System.out.println("clanRole == null");
        }
        return false;
    }

    private EmbedBuilder getLogEmbedBuilder(String logMessageTag, Server server, Role clanRole){


        EmbedBuilder embedBuilder = new EmbedBuilder();

        switch (logMessageTag){

            case LogMessageTag.CLAN_CREATED:
                embedBuilder.setColor(Color.GREEN).setTitle("Clan created").setDescription("**__"+clanRole.getName()+"__** "+clanRole.getMentionTag());
                break;

            case LogMessageTag.CLAN_RENAMED:
                embedBuilder.setColor(Color.YELLOW).setTitle("Clan renamed").setDescription("**__"+clanRole.getName()+"__** "+clanRole.getMentionTag());
                break;

            case LogMessageTag.CLAN_PROMOTED:
                embedBuilder.setColor(Color.BLUE).setTitle("Clan promoted to "+clanService.getClanRankName(server, clanRole)).setDescription("**__"+clanRole.getName()+"__** "+clanRole.getMentionTag());
                break;

            case LogMessageTag.CLAN_DEMOTED:
                embedBuilder.setColor(new Color(255, 108, 0)).setTitle("Clan demoted to "+clanService.getClanRankName(server, clanRole)).setDescription("**__"+clanRole.getName()+"__** "+clanRole.getMentionTag());
                break;

            case LogMessageTag.CLAN_DISBAND:
                embedBuilder.setColor(Color.RED).setTitle("Clan disband").setDescription("**__"+clanRole.getName()+"__**");
                break;

            case LogMessageTag.MEMBER_JOINED:
                embedBuilder.setColor(Color.GREEN).setTitle("Member joined clan");
                break;

            case LogMessageTag.MEMBER_PROMOTED_OFFICER:
                embedBuilder.setColor(Color.BLUE).setTitle("Member promoted to OFFICER");
                break;

            case LogMessageTag.MEMBER_PROMOTED_LEADER:
                embedBuilder.setColor(Color.BLUE).setTitle("Member promoted to LEADER");
                break;

            case LogMessageTag.MEMBER_DEMOTED:
                embedBuilder.setColor(new Color(255, 108, 0)).setTitle("Member demoted");
                break;

            case LogMessageTag.MEMBER_LEFT:
                embedBuilder.setColor(Color.RED).setTitle("Member left clan");
                break;

            case LogMessageTag.MEMBER_KICKED:
                embedBuilder.setColor(Color.RED).setTitle("Member kicked from clan");
                break;

            default:
                embedBuilder.setColor(Color.PINK).setTitle("LOG TAG "+logMessageTag+" NOT AVAILABLE");

        }

        return embedBuilder;
    }

}

package com.rust.estonia.discord.bot.clans.command.component.button.clan;

import com.rust.estonia.discord.bot.clans.command.component.button.ButtonComponentCommand;
import com.rust.estonia.discord.bot.clans.constant.ButtonTag;
import com.rust.estonia.discord.bot.clans.constant.LogMessageTag;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
import com.rust.estonia.discord.bot.clans.util.LogMessageUtil;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class JoinClanButtonCommand implements ButtonComponentCommand {

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

    @Autowired
    private ClanService clanService;

    @Autowired
    private LogMessageUtil logMessageUtil;

    @Override
    public String getName() {
        return ButtonTag.JOIN_CLAN;
    }

    @Override
    public void performCommand(ButtonInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater, User user, Message message) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Join clan error!")
                .setDescription("something went wrong..");

        if(interaction.getServer().isPresent()){
            Server server = interaction.getServer().get();
            long invitedUserId = discordCoreUtil.getIdFromMentionedTag(message.getContent());

            if(server.getMemberById(invitedUserId).isPresent()){

                if(invitedUserId==user.getId()) {

                    if (!message.getEmbeds().isEmpty()) {
                        Role clanRole = clanService.getClanRoleFromEmbedFooter(server, message.getEmbeds().get(0));

                        if (clanRole != null) {

                            if(clanRole.getUsers().size()<25) {

                                clanRole.addUser(user);

                                logMessageUtil.sendLogMessage(server, user, clanRole, LogMessageTag.MEMBER_JOINED);

                                responseEmbedBuilder.setColor(Color.GREEN).setTitle("Join clan success!")
                                        .setDescription("You are now a member of " + clanRole.getMentionTag());
                            } else {
                                responseEmbedBuilder.setDescription("this clan is already full");
                            }
                        } else {
                            responseEmbedBuilder.setDescription("clan role not available");
                        }
                    } else {
                        responseEmbedBuilder.setDescription("no embed available");
                    }
                    message.delete();

                } else {
                    responseEmbedBuilder.setDescription("This invite is not for you");
                }
            } else {
                responseEmbedBuilder.setDescription("User is not available");
            }
        } else {
            responseEmbedBuilder.setDescription("Server is not available");
        }

        interaction.createImmediateResponder().addEmbed(responseEmbedBuilder).setFlags(InteractionCallbackDataFlag.EPHEMERAL).respond();
    }
}

package com.rust.estonia.discord.bot.clans.command.component.button.clan;

import com.rust.estonia.discord.bot.clans.command.component.button.ButtonComponentCommand;
import com.rust.estonia.discord.bot.clans.constant.ButtonTag;
import com.rust.estonia.discord.bot.clans.constant.LogMessageTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
import com.rust.estonia.discord.bot.clans.util.LogMessageUtil;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class DisbandClanButtonCommand implements ButtonComponentCommand {

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

    @Autowired
    private SetupService setupService;

    @Autowired
    private ClanService clanService;

    @Autowired
    private LogMessageUtil logMessageUtil;

    @Override
    public String getName() {
        return ButtonTag.DISBAND_CLAN;
    }

    @Override
    public boolean hasPermission(Server server, User user) {

        if(setupService.roleHasUserByRoleTag(server, user, RoleTag.ADMIN_ROLE)){
            return true;
        }
        if(setupService.roleHasUserByRoleTag(server, user, RoleTag.MODERATOR_ROLE)){
            return true;
        }
        if(setupService.roleHasUserByRoleTag(server, user, RoleTag.CLAN_LEADER_ROLE)){
            return true;
        }

        return server.isOwner(user);
    }

    @Override
    public void performCommand(ButtonInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater, User user, Message message) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan disband error!")
                .setDescription("something went wrong..");

        if(interaction.getServer().isPresent()) {
            Server server = interaction.getServer().get();
            String[] mentionedTagArray = message.getContent().split(" ");
            if(mentionedTagArray.length>0){
                long roleId = discordCoreUtil.getIdFromMentionedTag(mentionedTagArray[0]);
                if(server.getRoleById(roleId).isPresent()) {
                    Role clanRole = server.getRoleById(roleId).get();
                    if (clanService.isClanRole(server, clanRole)) {

                        logMessageUtil.sendLogMessage(server, user, clanRole, LogMessageTag.CLAN_DISBAND);

                        String clanName = clanRole.getName();
                        clanService.deleteClan(server, clanRole);

                        responseEmbedBuilder.setColor(Color.GREEN)
                                .setTitle("Clan disband success!")
                                .setDescription("**" + clanName + "** got deleted!");
                    } else {
                        responseEmbedBuilder.setDescription("role is not a clan!");
                    }
                } else {
                    responseEmbedBuilder.setDescription("role not available!");
                }
            } else {
                responseEmbedBuilder.setDescription("no clan role available!");
            }
        } else {
            responseEmbedBuilder.setDescription("No server available!");
        }

        messageUpdater.addEmbed(responseEmbedBuilder).setContent(" ").update();
    }
}

package com.rust.estonia.discord.bot.clans.command.component.button.clan;

import com.rust.estonia.discord.bot.clans.command.component.button.ButtonComponentCommand;
import com.rust.estonia.discord.bot.clans.constant.ButtonTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
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
public class GiveClanLeaderToUserButtonCommand implements ButtonComponentCommand {

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

    @Autowired
    private SetupService setupService;

    @Autowired
    private ClanService clanService;

    @Override
    public String getName() {
        return ButtonTag.GIVE_CLAN_LEADER_TO_USER;
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
                .setTitle("Give leader role error!")
                .setDescription("something went wrong..");

        if(interaction.getServer().isPresent()) {
            Server server = interaction.getServer().get();
            String[] mentionedTagArray = message.getContent().split(" ");

            if(mentionedTagArray.length>1){
                long roleId = discordCoreUtil.getIdFromMentionedTag(mentionedTagArray[0]);
                long userId = discordCoreUtil.getIdFromMentionedTag(mentionedTagArray[1]);

                if(server.getRoleById(roleId).isPresent()) {
                    Role clanRole = server.getRoleById(roleId).get();

                    if (clanService.isClanRole(server, clanRole)) {

                        if(server.getMemberById(userId).isPresent()) {
                            User newLeader = server.getMemberById(userId).get();

                            if(clanRole.hasUser(newLeader)) {

                                if (clanService.setNewClanLeader(server, clanRole, newLeader)) {
                                    responseEmbedBuilder.setColor(Color.GREEN)
                                            .setTitle("Give leader role success!")
                                            .setDescription(newLeader.getMentionTag() + " is now the new leader of " + clanRole.getMentionTag());
                                } else {
                                    responseEmbedBuilder.setDescription("setNewClanLeader() failed!!");
                                }
                            } else {
                                responseEmbedBuilder.setDescription(newLeader.getMentionTag()+" is not a member of your clan!");
                            }
                        } else {
                            responseEmbedBuilder.setDescription("user is not available!");
                        }
                    } else {
                        responseEmbedBuilder.setDescription("role is not a clan!");
                    }
                } else {
                    responseEmbedBuilder.setDescription("role not available!");
                }
            } else {
                responseEmbedBuilder.setDescription("mentioned tags missing");
            }
        } else {
            responseEmbedBuilder.setDescription("No server available!");
        }

        messageUpdater.addEmbed(responseEmbedBuilder).setContent("").update();
    }
}

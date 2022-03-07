package com.rust.estonia.discord.bot.clans.command.component.button.clan;

import com.rust.estonia.discord.bot.clans.command.component.button.ButtonComponentCommand;
import com.rust.estonia.discord.bot.clans.constant.ButtonTag;
import com.rust.estonia.discord.bot.clans.constant.MenuTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.component.SelectMenuOptionBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GiveClanLeaderSelectButtonCommand implements ButtonComponentCommand {

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

    @Autowired
    private SetupService setupService;

    @Autowired
    private ClanService clanService;

    @Override
    public String getName() {
        return ButtonTag.GIVE_CLAN_LEADER_SELECT;
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

        return false;
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

            if(mentionedTagArray.length>0){
                long roleId = discordCoreUtil.getIdFromMentionedTag(mentionedTagArray[0]);

                if(server.getRoleById(roleId).isPresent()) {
                    Role clanRole = server.getRoleById(roleId).get();

                    if (clanService.isClanRole(server, clanRole)) {

                        List<SelectMenuOption> userOptions= new ArrayList<>();

                        for (User member : clanRole.getUsers()){
                            if(member.getId()!=user.getId()) {
                                String nickname = "";
                                if(member.getNickname(server).isPresent()){
                                    nickname = member.getNickname(server).get();
                                }
                                userOptions.add(new SelectMenuOptionBuilder()
                                        .setLabel(member.getName())
                                        .setValue(member.getIdAsString())
                                        .setDescription(nickname)
                                        .build());
                            }
                        }
                        if(!userOptions.isEmpty()) {

                            responseEmbedBuilder.setColor(Color.YELLOW)
                                    .setTitle("Give leader role to...")
                                    .setDescription("Select a member from your clan as the new leader");

                            messageUpdater.addComponents(ActionRow.of(SelectMenu.create(MenuTag.SELECT_NEW_CLAN_LEADER, userOptions)));
                        } else {
                            responseEmbedBuilder.setDescription("you are the only member of this clan!");

                            messageUpdater.addComponents(ActionRow.of(Button.danger(ButtonTag.DISBAND_CLAN, "Disband")));
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

        messageUpdater.addEmbed(responseEmbedBuilder).update();
    }
}

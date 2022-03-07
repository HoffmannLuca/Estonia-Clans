package com.rust.estonia.discord.bot.clans.command.component.menu;

import com.rust.estonia.discord.bot.clans.constant.ButtonTag;
import com.rust.estonia.discord.bot.clans.constant.MenuTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class SelectNewClanLeaderMenuCommand implements MenuComponentCommand {

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

    @Autowired
    private SetupService setupService;

    @Autowired
    private ClanService clanService;

    @Override
    public String getName() {
        return MenuTag.SELECT_NEW_CLAN_LEADER;
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
    public void performCommand(SelectMenuInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater, User user, Message message) {


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

                        if(!interaction.getChosenOptions().isEmpty()){
                            String memberId = interaction.getChosenOptions().get(0).getValue();

                            if(server.getMemberById(memberId).isPresent()){
                                User member = server.getMemberById(memberId).get();
                                Role leaderRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE);
                                if(leaderRole!=null) {

                                    responseEmbedBuilder.setColor(Color.YELLOW)
                                            .setTitle("Give leader role to "+member.getName())
                                            .setDescription("Are you sure you want to promote " + member.getMentionTag() + " to " + leaderRole.getMentionTag() + " ? otherwise dismiss this message");

                                    messageUpdater.setContent(clanRole.getMentionTag() + " " + member.getMentionTag())
                                            .addComponents(ActionRow.of(
                                                    Button.success(ButtonTag.GIVE_CLAN_LEADER_TO_USER, "Give " + member.getName() + " leader role"),
                                                    Button.primary(ButtonTag.GIVE_CLAN_LEADER_SELECT, "Select other member")

                                                    ));

                                } else {
                                    responseEmbedBuilder.setDescription("No Role has been setup for the role tag **" + RoleTag.CLAN_LEADER_ROLE.toUpperCase() + "**");
                                }
                            } else {
                                responseEmbedBuilder.setDescription("no member available!");
                            }
                        } else {
                            responseEmbedBuilder.setDescription("no option available!");
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

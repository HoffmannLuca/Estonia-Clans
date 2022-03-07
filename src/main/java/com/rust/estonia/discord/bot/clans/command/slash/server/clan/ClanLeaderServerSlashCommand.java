package com.rust.estonia.discord.bot.clans.command.slash.server.clan;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import com.rust.estonia.discord.bot.clans.constant.ButtonTag;
import com.rust.estonia.discord.bot.clans.constant.OptionLabelTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.constant.ServerSlashTag;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ClanLeaderServerSlashCommand implements ServerSlashCommand {

    @Autowired
    private SetupService setupService;

    @Autowired
    private ClanService clanService;

    private final String FIRST_OPTION_RENAME = "rename";
    private final String FIRST_OPTION_PROMOTE = "promote";
    private final String FIRST_OPTION_DEMOTE = "demote";
    private final String FIRST_OPTION_DISBAND = "disband";

    @Override
    public String getName() { return ServerSlashTag.CLAN_LEADER_COMMAND; }

    @Override
    public SlashCommandBuilder getCommandBuilder() {
        return SlashCommand.with(ServerSlashTag.CLAN_LEADER_COMMAND, "description",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_RENAME, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, OptionLabelTag.NAME, "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_PROMOTE, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.USER, OptionLabelTag.USER, "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_DEMOTE, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.USER, OptionLabelTag.USER, "description", true)
                                )
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_DISBAND, "description")

                )
        ).setDefaultPermission(false);
    }

    @Override
    public List<ApplicationCommandPermissions> addApplicationCommandPermissions(List<ApplicationCommandPermissions> permissionsList, Server server) {

        permissionsList = setupService.addPermissionsBySetupRoleTag(permissionsList, server, RoleTag.CLAN_LEADER_ROLE, true);

        return permissionsList;
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                               User user, TextChannel channel, Server server) {

        switch (firstOption){

            case FIRST_OPTION_RENAME:
                renameClan(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_PROMOTE:
                promoteUser(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_DEMOTE:
                demoteUser(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_DISBAND:
                disbandClan(interaction, secondOption, commandArguments, user, channel, server);
                break;

            default:

                interaction.createImmediateResponder()
                        .setContent("ERROR: "+ServerSlashTag.CLAN_LEADER_COMMAND+" - No command found with the name "+firstOption)
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
        }
    }

    private void renameClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan rename error!")
                .setDescription("something went wrong..");


        String newClanName = "";

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.NAME)){
                if(option.getStringValue().isPresent()){
                    newClanName = option.getStringValue().get();
                }
            }
        }

        if (!newClanName.equals("")) {
            Role clanRole = clanService.getClanRoleByLeader(server, user);
            if(clanRole!=null) {
                String oldClanName = clanRole.getName();
                clanService.renameClan(server, clanRole, newClanName);

                responseEmbedBuilder.setColor(Color.GREEN)
                        .setTitle("Clan rename success!")
                        .setDescription(clanRole.getMentionTag() + " clan was renamed from **" + oldClanName + "** to **" + newClanName + "**");

            } else {
                responseEmbedBuilder.setDescription("Your clan role does not exist");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void promoteUser(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("User promote error!")
                .setDescription("something went wrong..");


        User member = null;

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.USER)){
                if(option.getUserValue().isPresent()){
                    member = option.getUserValue().get();
                }
            }
        }

        if(member!=null) {
            if(!member.isBot()) {
                Role clanRole = clanService.getClanRoleByLeader(server, user);
                if (clanRole != null) {
                    if (clanRole.hasUser(member)) {
                        Role officerRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_OFFICER_ROLE);
                        if(officerRole!=null){
                            if(officerRole.hasUser(member)){
                                Role leaderRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE);
                                if(leaderRole!=null) {

                                    responseEmbedBuilder.setColor(Color.YELLOW)
                                            .setTitle("User promote warning!")
                                            .setDescription("Are you sure you want to promote " + member.getMentionTag() + " to " + leaderRole.getMentionTag() + " ? otherwise dismiss this message");

                                    response.setContent(clanRole.getMentionTag()+" "+member.getMentionTag()).addComponents(
                                            ActionRow.of(
                                                    Button.primary(ButtonTag.GIVE_CLAN_LEADER_TO_USER, "Give "+member.getName()+" leader role")
                                            )
                                    );
                                } else {
                                    responseEmbedBuilder.setDescription("No Role has been setup for the role tag **" + RoleTag.CLAN_LEADER_ROLE.toUpperCase() + "**");
                                }
                            } else {
                                officerRole.addUser(member);

                                responseEmbedBuilder.setColor(Color.GREEN)
                                        .setTitle("User promote success!")
                                        .setDescription(member.getMentionTag()+" is now a "+officerRole.getMentionTag());
                            }
                        } else {
                            responseEmbedBuilder.setDescription("No Role has been setup for the role tag **" + RoleTag.CLAN_OFFICER_ROLE.toUpperCase() + "**");
                        }
                    } else {
                        responseEmbedBuilder.setDescription(member.getMentionTag() + " is not a member of your clan");
                    }
                } else {
                    responseEmbedBuilder.setDescription("Your clan role does not exist");
                }
            } else {
                responseEmbedBuilder.setDescription(member.getMentionTag() + " is a bot. you can only select real users");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void demoteUser(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("User demote error!")
                .setDescription("something went wrong..");


        User member = null;

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.USER)){
                if(option.getUserValue().isPresent()){
                    member = option.getUserValue().get();
                }
            }
        }

        if(member!=null) {
            if(!member.isBot()) {
                Role clanRole = clanService.getClanRoleByLeader(server, user);
                if (clanRole != null) {
                    if (clanRole.hasUser(member)) {
                        Role officerRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_OFFICER_ROLE);
                        if(officerRole!=null){
                            if(officerRole.hasUser(member)){

                                officerRole.removeUser(member);

                                responseEmbedBuilder.setColor(Color.GREEN)
                                        .setTitle("User demote success!")
                                        .setDescription(member.getMentionTag()+" is now a regular member and not a "+officerRole.getMentionTag()+" anymore");
                            } else {
                                responseEmbedBuilder.setDescription(member.getMentionTag() + " is just a regular member and not a "+officerRole.getMentionTag());
                            }
                        } else {
                            responseEmbedBuilder.setDescription("No Role has been setup for the role tag **" + RoleTag.CLAN_OFFICER_ROLE.toUpperCase() + "**");
                        }
                    } else {
                        responseEmbedBuilder.setDescription(member.getMentionTag() + " is not a member of your clan");
                    }
                } else {
                    responseEmbedBuilder.setDescription("Your clan role does not exist");
                }
            } else {
                responseEmbedBuilder.setDescription(member.getMentionTag() + " is a bot. you can only select real users");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void disbandClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan disband error!")
                .setDescription("something went wrong..");

        Role clanRole = clanService.getClanRoleByLeader(server, user);
        if(clanRole!=null) {

            responseEmbedBuilder.setColor(Color.YELLOW)
                    .setTitle("Clan disband warning!")
                    .setDescription("Are you sure you want to disband your clan? instead you could give someone else the leader role");

            response.setContent(clanRole.getMentionTag()).addComponents(
                    ActionRow.of(
                            Button.primary(ButtonTag.GIVE_CLAN_LEADER_SELECT, "Give leader role to..."),
                            Button.danger(ButtonTag.DISBAND_CLAN, "Disband")
                    )
            );

        } else {
            responseEmbedBuilder.setDescription("Your clan role does not exist");
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }
}

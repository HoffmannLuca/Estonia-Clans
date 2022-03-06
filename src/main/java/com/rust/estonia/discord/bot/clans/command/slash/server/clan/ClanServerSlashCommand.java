package com.rust.estonia.discord.bot.clans.command.slash.server.clan;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import com.rust.estonia.discord.bot.clans.constant.OptionLabelTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.constant.ServerSlashTag;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ClanServerSlashCommand implements ServerSlashCommand {

    @Autowired
    private SetupService setupService;

    @Autowired
    private ClanService clanService;

    private final String FIRST_OPTION_INFO = "info";
    private final String FIRST_OPTION_LEAVE = "leave";

    @Override
    public String getName() {
        return ServerSlashTag.CLAN_COMMAND;
    }

    @Override
    public SlashCommandBuilder getCommandBuilder() {
        return SlashCommand.with(ServerSlashTag.CLAN_COMMAND, "description",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_INFO, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, OptionLabelTag.CLAN, "description", true)
                                )
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_LEAVE, "description")
                )
        ).setDefaultPermission(true);
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                               User user, TextChannel channel, Server server) {

        switch (firstOption){

            case FIRST_OPTION_INFO:
                showClanInfo(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_LEAVE:
                leaveClan(interaction, secondOption, commandArguments, user, channel, server);
                break;

            default:

                interaction.createImmediateResponder()
                        .setContent("ERROR: "+ServerSlashTag.CLAN_COMMAND+" - No command found with the name "+firstOption)
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
        }
    }

    private void showClanInfo(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan info error!")
                .setDescription("something went wrong..");

        Role clanRole = null;

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.CLAN)){
                if(option.getRoleValue().isPresent()){
                    clanRole = option.getRoleValue().get();
                }
            }
        }

        if (clanRole != null) {
            if(clanService.isClanRole(server, clanRole)){

                responseEmbedBuilder = clanService.getClanInfoEmbedBuilder(server, clanRole);

            } else {
                responseEmbedBuilder.setDescription(clanRole.getMentionTag() + " is not a clan");
            }
        }

        interaction.createImmediateResponder()
                .addEmbed(responseEmbedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }

    private void leaveClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan leave error!")
                .setDescription("something went wrong..");

        if(clanService.isClanMember(server, user)) {
            Role clanRole = clanService.getClanRoleByLeader(server, user);
            if (clanRole==null) {

                clanRole = clanService.getClanRoleByMember(server, user);
                if(clanRole!=null) {
                    clanRole.removeUser(user);
                    Role leaderRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE);
                    Role officerRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_OFFICER_ROLE);
                    if(leaderRole!=null){
                        leaderRole.removeUser(user);
                    }
                    if(officerRole!=null){
                        officerRole.removeUser(user);
                    }
                    responseEmbedBuilder.setColor(Color.GREEN)
                            .setTitle("Clan leave success!")
                            .setDescription("You are no longer a member of " + clanRole.getMentionTag());
                } else {
                    responseEmbedBuilder.setDescription("You are not in a clan");
                }
            } else {
                //TODO add button (give leader to) & (disband) with confirmation and member select menu
                responseEmbedBuilder.setTitle("Clan leave warning!").setColor(Color.ORANGE)
                        .setDescription("You are the leader of your clan! you have to give leader role to someone else in your clan or disband clan");
            }
        } else {
            responseEmbedBuilder.setDescription("You are not in a clan");
        }

        interaction.createImmediateResponder()
                .addEmbed(responseEmbedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }
}

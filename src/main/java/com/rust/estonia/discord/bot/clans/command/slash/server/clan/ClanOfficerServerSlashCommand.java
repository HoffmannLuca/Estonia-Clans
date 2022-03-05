package com.rust.estonia.discord.bot.clans.command.slash.server.clan;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import com.rust.estonia.discord.bot.clans.constant.OptionLabelTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.constant.ServerSlashTag;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
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
public class ClanOfficerServerSlashCommand implements ServerSlashCommand {

    @Autowired
    private SetupService setupService;

    private final String FIRST_OPTION_INVITE = "invite";
    private final String FIRST_OPTION_KICK = "kick";

    @Override
    public String getName() { return ServerSlashTag.CLAN_OFFICER_COMMAND; }

    @Override
    public SlashCommandBuilder getCommandBuilder() {
        return SlashCommand.with(ServerSlashTag.CLAN_OFFICER_COMMAND, "description",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_INVITE, "description",
                            Collections.singletonList(
                                SlashCommandOption.create(SlashCommandOptionType.USER, OptionLabelTag.USER, "description", true)
                            )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_KICK, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.USER, OptionLabelTag.USER, "description", true)
                                )
                        )

                )
        ).setDefaultPermission(false);
    }

    @Override
    public List<ApplicationCommandPermissions> addApplicationCommandPermissions(List<ApplicationCommandPermissions> permissionsList, Server server) {

        permissionsList = setupService.addPermissionsBySetupRoleTag(permissionsList, server, RoleTag.CLAN_LEADER_ROLE, true);
        permissionsList = setupService.addPermissionsBySetupRoleTag(permissionsList, server, RoleTag.CLAN_OFFICER_ROLE, true);

        return permissionsList;
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                               User user, TextChannel channel, Server server) {

        switch (firstOption){

            case FIRST_OPTION_INVITE:
                inviteUser(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_KICK:
                kickUser(interaction, secondOption, commandArguments, user, channel, server);
                break;

            default:

                interaction.createImmediateResponder()
                        .setContent("ERROR: "+ServerSlashTag.CLAN_OFFICER_COMMAND+" - No command found with the name "+firstOption)
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
        }
    }

    private void inviteUser(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan member invite error!")
                .setDescription("something went wrong..");



        interaction.createImmediateResponder()
                .addEmbed(responseEmbedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }

    private void kickUser(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan member kick error!")
                .setDescription("something went wrong..");



        interaction.createImmediateResponder()
                .addEmbed(responseEmbedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }
}

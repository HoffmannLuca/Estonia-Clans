package com.rust.estonia.discord.bot.clans.command.slash.server.clan;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import com.rust.estonia.discord.bot.clans.constant.ServerSlashTag;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ClanServerSlashCommand implements ServerSlashCommand {

    private final String FIRST_OPTION_INFO = "info";
    private final String FIRST_OPTION_LEAVE = "leave";

    @Override
    public String getName() {
        return ServerSlashTag.CLAN_COMMAND;
    }

    @Override
    public SlashCommandBuilder getCommandBuilder() {
        return SlashCommand.with(ServerSlashTag.CLAN_COMMAND, "A command dedicated to channels",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_INFO, "Allows a permission to a user for a channel",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, "CLAN", "The channel to modify", true)
                                )
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_LEAVE, "Allows a permission to a user for a channel")
                )
        ).setDefaultPermission(true);
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        interaction.createImmediateResponder()
                .setContent(ServerSlashTag.CLAN_COMMAND)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }
}

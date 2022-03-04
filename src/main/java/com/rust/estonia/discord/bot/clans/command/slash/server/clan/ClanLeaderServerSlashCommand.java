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
public class ClanLeaderServerSlashCommand implements ServerSlashCommand {

    private final String FIRST_OPTION_DISBAND = "disband";
    private final String FIRST_OPTION_RENAME = "rename";
    private final String FIRST_OPTION_PROMOTE = "promote";
    private final String FIRST_OPTION_DEMOTE = "demote";

    @Override
    public String getName() { return ServerSlashTag.CLAN_LEADER_COMMAND; }

    @Override
    public SlashCommandBuilder getCommandBuilder() {
        return SlashCommand.with(ServerSlashTag.CLAN_LEADER_COMMAND, "description",
                Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_DISBAND, "description"),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_RENAME, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "NAME", "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_PROMOTE, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.USER, "USER", "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_DEMOTE, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.USER, "USER", "description", true)
                                )
                        )

                )
        ).setDefaultPermission(true);
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        interaction.createImmediateResponder()
                .setContent(ServerSlashTag.CLAN_LEADER_COMMAND)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }
}

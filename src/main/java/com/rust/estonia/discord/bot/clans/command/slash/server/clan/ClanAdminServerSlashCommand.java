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
public class ClanAdminServerSlashCommand implements ServerSlashCommand {

    private final String FIRST_OPTION_CREATE = "create";
    private final String FIRST_OPTION_DISBAND = "disband";
    private final String FIRST_OPTION_RENAME = "rename";
    private final String FIRST_OPTION_PROMOTE = "promote";
    private final String FIRST_OPTION_DEMOTE = "demote";

    @Override
    public String getName() { return ServerSlashTag.CLAN_ADMIN_COMMAND; }

    @Override
    public SlashCommandBuilder getCommandBuilder() {
        return SlashCommand.with(ServerSlashTag.CLAN_ADMIN_COMMAND, "description",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_CREATE, "description",
                                Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.USER, "LEADER", "description", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "NAME", "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_DISBAND, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, "CLAN", "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_RENAME, "description",
                                Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, "CLAN", "description", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, "NAME", "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_PROMOTE, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, "CLAN", "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_DEMOTE, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, "CLAN", "description", true)
                                )
                        )

                )
        ).setDefaultPermission(true);
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        interaction.createImmediateResponder()
                .setContent(ServerSlashTag.CLAN_ADMIN_COMMAND)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }
}

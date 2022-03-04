package com.rust.estonia.discord.bot.clans.command.slash.server;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

public interface ServerSlashCommand {

    String getName();

    SlashCommandBuilder getCommandBuilder();

    void performCommand(SlashCommandInteraction interaction,
                        String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                        User user, TextChannel channel, Server server);

}

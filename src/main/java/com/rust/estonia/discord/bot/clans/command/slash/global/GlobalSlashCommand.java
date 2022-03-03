package com.rust.estonia.discord.bot.clans.command.slash.global;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

public interface GlobalSlashCommand {

    String getName();

    SlashCommandBuilder getCommandBuilder();

    void performCommand(SlashCommandInteraction interaction,
                        String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                        User user, TextChannel channel);

    default void deactivateCommand(DiscordApi api) {

        api.getGlobalSlashCommands().thenAccept(slashCommands ->
                slashCommands.stream().filter(slashCommand ->
                        slashCommand.getName().equals(getName())
                ).forEach(SlashCommand::deleteGlobal)
        );
    }
}

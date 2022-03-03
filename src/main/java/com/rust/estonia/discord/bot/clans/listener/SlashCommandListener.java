package com.rust.estonia.discord.bot.clans.listener;

import com.rust.estonia.discord.bot.clans.command.slash.global.GlobalSlashCommand;
import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlashCommandListener implements SlashCommandCreateListener {

    @Autowired
    private List<GlobalSlashCommand> globalSlashCommands;

    @Autowired
    private List<ServerSlashCommand> serverSlashCommands;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent slashCommandCreateEvent) {

        if(slashCommandCreateEvent.getInteraction().asSlashCommandInteraction().isPresent()) {
            SlashCommandInteraction interaction = slashCommandCreateEvent.getInteraction().asSlashCommandInteraction().get();

            if (interaction.getChannel().isPresent()) {

                SlashCommandInteractionOption firstOption = null;
                SlashCommandInteractionOption secondOption = null;
                List<SlashCommandInteractionOption> commandArguments = interaction.getArguments();

                if(interaction.getOptions().get(0).isSubcommandOrGroup()){
                    firstOption = interaction.getOptions().get(0);
                    if(firstOption.getOptions().get(0).isSubcommandOrGroup()){
                        secondOption = interaction.getOptions().get(0);
                    }
                }

                if (!tryToPerformGlobalCommand(
                            interaction,
                            interaction.getCommandName(),
                            firstOption,
                            secondOption,
                            commandArguments,
                            interaction.getUser(),
                            interaction.getChannel().get())) {

                    if (interaction.getServer().isPresent()) {
                        if (!tryToPerformServerCommand(
                                    interaction,
                                    interaction.getCommandName(),
                                    firstOption,
                                    secondOption,
                                    commandArguments,
                                    interaction.getServer().get(),
                                    interaction.getUser(),
                                    interaction.getChannel().get())) {

                            interaction.createImmediateResponder()
                                    .setContent("ERROR - COMMAND NOT AVAILABLE")
                                    .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                                    .respond();
                        }
                    } else {
                        interaction.createImmediateResponder()
                                .setContent("ERROR - SERVER NOT AVAILABLE")
                                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                                .respond();
                    }
                }
            } else {
                System.out.println("ERROR - NO CHANNEL AVAILABLE");
            }
        }
    }

    private boolean tryToPerformGlobalCommand(SlashCommandInteraction interaction,
                                              String commandName, SlashCommandInteractionOption firstOption, SlashCommandInteractionOption secondOption, List<SlashCommandInteractionOption> commandArguments,
                                              User user, TextChannel channel){

        for(GlobalSlashCommand commandClass: globalSlashCommands){
            if(commandClass.getName().equals(commandName)){
                commandClass.performCommand(interaction, firstOption, secondOption, commandArguments, user, channel);
                return true;
            }
        }
        return false;
    }

    private boolean tryToPerformServerCommand(SlashCommandInteraction interaction,
                                              String commandName, SlashCommandInteractionOption firstOption, SlashCommandInteractionOption secondOption, List<SlashCommandInteractionOption> commandArguments,
                                              Server server, User user, TextChannel channel){

        for(ServerSlashCommand commandClass: serverSlashCommands){
            if(commandClass.getName().equals(commandName)){
                commandClass.performCommand(interaction, firstOption, secondOption, commandArguments, user, channel, server);
                return true;
            }
        }
        return false;
    }
}

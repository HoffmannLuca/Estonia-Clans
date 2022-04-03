package com.rust.estonia.discord.bot.clans.listener;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.listener.server.ServerJoinListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotServerJoinListener implements ServerJoinListener {

    @Autowired
    private List<ServerSlashCommand> serverSlashCommands;

    @Override
    public void onServerJoin(ServerJoinEvent event) {

        Server server = event.getServer();
        List<SlashCommandBuilder> serverCommandBuilder = new ArrayList<>();

        for(ServerSlashCommand command : serverSlashCommands){
            serverCommandBuilder.add(command.getCommandBuilder(server));
        }

        event.getApi().bulkOverwriteServerApplicationCommands(server, serverCommandBuilder);
    }
}

package com.rust.estonia.discord.bot.clans.util;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.ServerApplicationCommandPermissionsBuilder;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationCommandUtil {

    @Autowired
    private List<ServerSlashCommand> serverSlashCommands;

    public void updateServerSlashCommandPermissions(Server server) {

        updateServerSlashCommandPermissions(server, server.getApi());
    }
    public void updateServerSlashCommandPermissions(Server server, DiscordApi api) {

        List<ServerApplicationCommandPermissionsBuilder> serverPermissionBuilder = new ArrayList<>();
        for(ServerSlashCommand command : serverSlashCommands){
            serverPermissionBuilder.add(command.getPermissionBuilder(server));
        }

        api.batchUpdateApplicationCommandPermissions(server, serverPermissionBuilder);
    }
    public void updateServerSlashCommands(Server server){

        List<SlashCommandBuilder> serverCommandBuilder = new ArrayList<>();
        for(ServerSlashCommand command : serverSlashCommands){
            serverCommandBuilder.add(command.getCommandBuilder(server));
        }

        server.getApi().bulkOverwriteServerApplicationCommands(server, serverCommandBuilder);
    }
}

package com.rust.estonia.discord.bot.clans.util;

import com.rust.estonia.discord.bot.clans.command.slash.global.GlobalSlashCommand;
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
    private List<GlobalSlashCommand> globalSlashCommands;

    @Autowired
    private List<ServerSlashCommand> serverSlashCommands;

    public void setSlashCommands(DiscordApi api){

        List<SlashCommandBuilder> globalCommandBuilder = new ArrayList<>();
        for(GlobalSlashCommand command : globalSlashCommands){
            globalCommandBuilder.add(command.getCommandBuilder());
        }
        api.bulkOverwriteGlobalApplicationCommands(globalCommandBuilder).join();

        List<SlashCommandBuilder> serverCommandBuilder = new ArrayList<>();
        for(ServerSlashCommand command : serverSlashCommands){
            serverCommandBuilder.add(command.getCommandBuilder());
        }
        for(Server server : api.getServers()){

            api.bulkOverwriteServerApplicationCommands(server, serverCommandBuilder);

            updateServerSlashCommandPermissions(server, api);
        }
    }

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
}

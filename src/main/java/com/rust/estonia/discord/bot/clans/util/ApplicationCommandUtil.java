package com.rust.estonia.discord.bot.clans.util;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.ServerApplicationCommandPermissionsBuilder;
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
}

package com.rust.estonia.discord.bot.clans.command.slash.server;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ServerSlashCommand {

    String getName();

    SlashCommandBuilder getCommandBuilder();

    default ServerApplicationCommandPermissionsBuilder getPermissionBuilder(Server server){

        try {
            for (SlashCommand command : server.getSlashCommands().get()){

                if(command.getName().equals(this.getName())){

                    List<ApplicationCommandPermissions> permissions = new ArrayList<>();
                    permissions = addApplicationCommandPermissions(permissions, server);
                    return new ServerApplicationCommandPermissionsBuilder(command.getId(), permissions);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    default List<ApplicationCommandPermissions> addApplicationCommandPermissions(List<ApplicationCommandPermissions> permissionsList, Server server){

        permissionsList.add(ApplicationCommandPermissions.create(server.getOwnerId(), ApplicationCommandPermissionType.USER, true));
        return permissionsList;
    }

    void performCommand(SlashCommandInteraction interaction,
                        String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                        User user, TextChannel channel, Server server);

}

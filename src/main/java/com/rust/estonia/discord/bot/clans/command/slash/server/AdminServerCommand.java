package com.rust.estonia.discord.bot.clans.command.slash.server;

import com.rust.estonia.discord.bot.clans.constant.GlobalSlashTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.constant.ServerSlashTag;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminServerCommand implements ServerSlashCommand {

    @Autowired
    private SetupService setupService;

    @Override
    public String getName() {
        return ServerSlashTag.ADMIN_COMMAND;
    }

    @Override
    public SlashCommandBuilder getCommandBuilder() {

        return SlashCommand.with(ServerSlashTag.ADMIN_COMMAND, "An admin command").setDefaultPermission(false);
    }

    @Override
    public List<ApplicationCommandPermissions> addApplicationCommandPermissions(List<ApplicationCommandPermissions> permissionsList, Server server) {

        permissionsList.add(ApplicationCommandPermissions.create(server.getOwnerId(), ApplicationCommandPermissionType.USER, true));
        permissionsList = setupService.addPermissionsBySetupRoleTag(permissionsList, server, RoleTag.ADMIN_ROLE, true);

        return permissionsList;
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL).setContent("ADMIN COMMAND").respond();
    }
}

package com.rust.estonia.discord.bot.clans.command.component.button;

import com.rust.estonia.discord.bot.clans.constant.ButtonTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteMessageButtonCommand implements ButtonComponentCommand {

    @Autowired
    private SetupService setupService;

    @Override
    public String getName() { return ButtonTag.DELETE_MESSAGE; }

    @Override
    public boolean hasPermission(Server server, User user) {

        if(setupService.roleHasUserByRoleTag(server, user, RoleTag.ADMIN_ROLE)){
            return true;
        }
        if(setupService.roleHasUserByRoleTag(server, user, RoleTag.MODERATOR_ROLE)){
            return true;
        }

        return false;
    }

    @Override
    public void performCommand(ButtonInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater, User user, Message message) {

        message.delete();
    }
}

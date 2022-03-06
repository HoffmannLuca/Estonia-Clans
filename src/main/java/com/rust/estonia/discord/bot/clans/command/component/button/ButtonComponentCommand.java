package com.rust.estonia.discord.bot.clans.command.component.button;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;

public interface ButtonComponentCommand {

    String getName();

    default boolean hasPermission(Server server, User user){ return true; }

    void performCommand(ButtonInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater, User user, Message message);
}

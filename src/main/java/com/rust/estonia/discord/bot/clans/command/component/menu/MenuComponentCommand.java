package com.rust.estonia.discord.bot.clans.command.component.menu;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;

public interface MenuComponentCommand {

    String getName();

    default boolean hasPermission(User user){ return true; }

    void performCommand(SelectMenuInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater, User user, Message message);
}

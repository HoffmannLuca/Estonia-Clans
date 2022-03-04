package com.rust.estonia.discord.bot.clans.command.component.menu;

import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;

public interface MenuComponentCommand {

    String getName();

    void performCommand(SelectMenuInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater);
}

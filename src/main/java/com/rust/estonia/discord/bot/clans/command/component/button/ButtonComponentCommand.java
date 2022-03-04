package com.rust.estonia.discord.bot.clans.command.component.button;

import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;

public interface ButtonComponentCommand {

    String getName();

    void performCommand(ButtonInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater);
}
